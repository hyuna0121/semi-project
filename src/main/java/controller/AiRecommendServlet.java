package controller;

import java.io.IOException;
import java.util.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 5단계 입력(도시/기간/동행/관심사/템포)을 결합해 추천 일정 JSON 생성
 * URL: GET /ai/recommend
 */
@WebServlet("/ai/recommend")
public class AiRecommendServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");

    try {
      // 1) 세션/파라미터에서 최종 입력 수집 (세션 우선, 쿼리가 있으면 덮어쓰기 허용)
      HttpSession session = req.getSession(false);
      String city = pick(req, session, "city", "w_city", "seoul");
      int days = parseInt(pick(req, session, "days", "w_days", "2"), 2);
      String pace = pick(req, session, "pace", "w_pace", "normal");

      String companionsCsv = pick(req, session, "companions", "w_companions", "");
      Set<String> companions = toSet(companionsCsv);

      String interestsCsv = pick(req, session, "interests", "w_interests", "");
      Set<String> interests = toSet(interestsCsv);

      // 2) 도시별 POI(간단 샘플) 로드 – 실제로는 DB에서 SELECT 해오면 됩니다
      List<Poi> all = samplePois(city);

      // 3) 스코어링: 관심사/동행/인기도를 가중치로 점수 계산
      List<ScoredPoi> ranked = rankPois(all, interests, companions);

      // 4) 템포별 1일 방문 수
      int perDay = pace.equals("fast") ? 6 : pace.equals("slow") ? 3 : 4;

      // 5) 일자별로 POI 배치 + 식사/카페 자동 끼워넣기
      List<Map<String,Object>> dayPlans = buildItinerary(ranked, days, perDay);

      // 6) JSON 결과
      Map<String,Object> result = new LinkedHashMap<>();
      result.put("city", city);
      result.put("daysCount", days);
      result.put("pace", pace);
      result.put("interests", interests.toArray(new String[0]));
      result.put("days", dayPlans);

      resp.setContentType("application/json; charset=UTF-8");
      resp.getWriter().write(new GsonBuilder().create().toJson(result));

    } catch (Exception e) {
	  e.printStackTrace();
	  resp.setStatus(500);
	  resp.setContentType("application/json; charset=UTF-8");
	  Map<String,Object> err = new LinkedHashMap<>();
	  err.put("error", true);
	  err.put("message", e.getClass().getSimpleName() + " - " + (e.getMessage()==null?"":e.getMessage()));
	  resp.getWriter().write(new com.google.gson.Gson().toJson(err));
    }
  }

  /* ----------------- 유틸/모델 ----------------- */

  private static String pick(HttpServletRequest req, HttpSession ses, String q, String s, String def){
    String v = req.getParameter(q);
    if (v != null && !v.trim().isEmpty()) return v.trim();
    if (ses != null) {
      Object o = ses.getAttribute(s);
      if (o != null) {
        String sv = String.valueOf(o).trim();
        if (!sv.isEmpty()) return sv;
      }
    }
    return def;
  }
  private static int parseInt(String v, int def){ try { return Integer.parseInt(v); } catch(Exception e){ return def; } }
  private static Set<String> toSet(String csv){
    Set<String> set = new LinkedHashSet<>();
    if (csv == null || csv.trim().isEmpty()) return set;
    for (String t : csv.split("\\s*,\\s*")) if (!t.isEmpty()) set.add(t);
    return set;
  }

  /* POI 모델 */
  static class Poi {
    String name, category; // spot, food, cafe, nature, culture, shopping ...
    double lat, lon;
    double rating;  // 0~5
    boolean kidFriendly, seniorFriendly, coupleHot;
    Set<String> tags; // hotplace, activity, nightview 등

    Poi(String name, String category, double lat, double lon, double rating,
        boolean kidFriendly, boolean seniorFriendly, boolean coupleHot, String... tags){
      this.name = name; this.category = category; this.lat=lat; this.lon=lon; this.rating=rating;
      this.kidFriendly=kidFriendly; this.seniorFriendly=seniorFriendly; this.coupleHot=coupleHot;
      this.tags = new LinkedHashSet<>(Arrays.asList(tags));
    }
  }
  static class ScoredPoi {
    Poi poi; double score;
    ScoredPoi(Poi p, double s){ this.poi=p; this.score=s; }
  }

  /* 도시별 샘플 데이터 (좌표/카테고리/태그) */
  private static List<Poi> samplePois(String city){
    List<Poi> L = new ArrayList<>();
    if ("busan".equals(city)) {
      L.add(new Poi("해운대 해수욕장","nature",35.1587,129.1604,4.6,false,true,true,"hotplace","nightview"));
      L.add(new Poi("광안대교 야경","nature",35.1534,129.1187,4.7,false,true,true,"nightview","hotplace"));
      L.add(new Poi("자갈치 시장","food",35.0977,129.0306,4.4,false,true,false,"food"));
      L.add(new Poi("감천 문화마을","culture",35.0970,129.0107,4.5,false,true,true,"hotplace","vibes"));
      L.add(new Poi("서면 카페거리","cafe",35.1577,129.0595,4.3,false,true,true,"cafe","hotplace"));
      L.add(new Poi("신세계 센텀시티","shopping",35.1683,129.1291,4.5,true,true,false,"shopping"));
    } else { // default seoul
      L.add(new Poi("경복궁","culture",37.5796,126.9770,4.7,true,true,false,"culture","mustsee"));
      L.add(new Poi("북촌 한옥마을","vibes",37.5827,126.9830,4.5,true,true,true,"hotplace","vibes"));
      L.add(new Poi("남산 N서울타워","nature",37.5512,126.9882,4.6,false,true,true,"nightview","hotplace"));
      L.add(new Poi("광장시장","food",37.5700,127.0010,4.4,false,true,false,"food","mustsee"));
      L.add(new Poi("카페거리(성수)","cafe",37.5446,127.0565,4.3,false,true,true,"cafe","hotplace"));
      L.add(new Poi("명동 쇼핑거리","shopping",37.5636,126.9827,4.2,false,true,true,"shopping","hotplace"));
      L.add(new Poi("뚝섬 한강공원","nature",37.5311,127.0669,4.3,true,true,false,"nature","activity"));
    }
    return L;
  }

  /* 관심사/동행 기반 스코어링 */
  private static List<ScoredPoi> rankPois(List<Poi> all, Set<String> interests, Set<String> companions){
    List<ScoredPoi> out = new ArrayList<>();
    for (Poi p : all) {
      double s = 0.0;
      // 기본 가중치: 평점
      s += p.rating * 1.2;

      // 관심사 매칭(태그/카테고리)
      for (String it : interests) {
        if (p.tags.contains(it)) s += 1.5;
        if ("food".equals(it) && "food".equals(p.category)) s += 1.3;
        if ("cafe".equals(it) && "cafe".equals(p.category)) s += 1.2;
        if ("shopping".equals(it) && "shopping".equals(p.category)) s += 1.2;
        if ("nature".equals(it) && "nature".equals(p.category)) s += 1.2;
        if ("culture".equals(it) && "culture".equals(p.category)) s += 1.2;
        if ("hotplace".equals(it) && p.tags.contains("hotplace")) s += 1.0;
        if ("nightview".equals(it) && p.tags.contains("nightview")) s += 1.0;
        if ("activity".equals(it) && p.tags.contains("activity")) s += 1.0;
      }

      // 동행 보정
      if (companions.contains("kids") && p.kidFriendly) s += 0.7;
      if (companions.contains("parents") && p.seniorFriendly) s += 0.6;
      if (companions.contains("couple") && p.coupleHot) s += 0.6;

      out.add(new ScoredPoi(p, s));
    }
    out.sort((a,b)-> Double.compare(b.score, a.score));
    return out;
  }

  /* 일정 구성: 오전/점심/오후/저녁 패턴 + 식사/카페 자동 삽입 */
  private static List<Map<String,Object>> buildItinerary(List<ScoredPoi> ranked, int days, int perDay){
	  List<Map<String,Object>> dayPlans = new ArrayList<>();
	  int idx = 0;

	  for (int d=1; d<=days; d++){
	    List<Map<String,Object>> items = new ArrayList<>();

	    // 1) spot 위주로 perDay개 채우기 (food/cafe는 여기서 건너뜀)
	    int added = 0;
	    while (added < perDay && idx < ranked.size()){
	      Poi p = ranked.get(idx++).poi;
	      if ("food".equals(p.category) || "cafe".equals(p.category)) continue;
	      items.add(row(timeByIndex(added), p, noteFor(p)));
	      added++;
	    }

	    // 2) 식사/카페 후보
	    Poi lunch  = pickFirstOf(ranked, "food");
	    Poi cafe   = pickFirstOf(ranked, "cafe");
	    Poi dinner = pickNextOf(ranked, "food", lunch);

	    // 3) 안전한 삽입 인덱스 계산
	    //    clamp: 0 ~ items.size() 사이로 잘라서 넣기
	    if (lunch != null) {
	      int posLunch = clamp(1, 0, items.size());      // 보통 2번째에 넣고 싶지만, 비어있으면 0
	      items.add(posLunch, row("12:00", lunch, "인기 메뉴 추천"));
	    }
	    if (cafe != null) {
	      int posCafe = clamp(3, 0, items.size());       // 보통 4번째, 부족하면 마지막에
	      items.add(posCafe, row("15:00", cafe, "디저트/브레이크"));
	    }
	    if (dinner != null) {
	      // 저녁은 보통 맨 끝
	      items.add(row("19:00", dinner, "현지 맛집"));
	    }

	    Map<String,Object> day = new LinkedHashMap<>();
	    day.put("date", "Day " + d);
	    day.put("items", items);
	    dayPlans.add(day);
	  }
	  return dayPlans;
	}

	private static int clamp(int val, int min, int max){
	  if (val < min) return min;
	  if (val > max) return max;
	  return val;
	}
  private static String timeByIndex(int i){
    switch(i){ case 0: return "09:00"; case 1: return "10:30"; case 2: return "13:30";
      case 3: return "16:00"; case 4: return "17:30"; default: return "09:00"; }
  }
  private static Map<String,Object> row(String time, Poi p, String note){
    Map<String,Object> m = new LinkedHashMap<>();
    m.put("time", time); m.put("name", p.name); m.put("category", p.category);
    m.put("lat", p.lat); m.put("lon", p.lon); m.put("note", note);
    return m;
  }
  private static String noteFor(Poi p){
    if (p.tags.contains("nightview")) return "야경이 좋아요";
    if (p.tags.contains("hotplace")) return "요즘 핫플!";
    if ("culture".equals(p.category)) return "역사/전통을 느껴보세요";
    if ("nature".equals(p.category)) return "도심 속 힐링";
    return "";
  }
  private static Poi pickFirstOf(List<ScoredPoi> ranked, String cat){
    for (ScoredPoi s : ranked) if (cat.equals(s.poi.category)) return s.poi;
    return null;
  }
  private static Poi pickNextOf(List<ScoredPoi> ranked, String cat, Poi except){
    for (ScoredPoi s : ranked) if (cat.equals(s.poi.category) && s.poi != except) return s.poi;
    return except; // 그래도 없으면 같은 것 재사용
  }
}
