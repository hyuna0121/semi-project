package controller;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import okhttp3.*;   // ★ OkHttp 사용

@WebServlet("/ai/recommend")
public class AiRecommendServlet extends HttpServlet {

    private static String GEMINI_API_KEY = "";
    private static String GEMINI_MODEL   = "gemini-1.5-flash";

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final Map<String, String> CACHE = new HashMap<>();

    @Override
    public void init() {
        try (InputStream in = getServletContext().getResourceAsStream("/WEB-INF/config.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                GEMINI_API_KEY = props.getProperty("gemini.api.key", "").trim();
                GEMINI_MODEL   = props.getProperty("gemini.model", "gemini-1.5-flash").trim();
                System.out.println("[Gemini] ✅ config.properties loaded. model=" + GEMINI_MODEL);
            } else {
                System.err.println("[Gemini] ⚠ config.properties not found under /WEB-INF/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String GEMINI_URL() {
        return "https://generativelanguage.googleapis.com/v1beta/models/"
                + GEMINI_MODEL + ":generateContent?key=" + GEMINI_API_KEY;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String city = nv(req.getParameter("city"), "seoul");
        int days    = parseInt(nv(req.getParameter("days"), "2"), 2, 1, 10);
        String pace = nv(req.getParameter("pace"), "normal");
        String interestsParam = nv(req.getParameter("interests"), "hotplace");

        List<String> interests = new ArrayList<>();
        for (String s : interestsParam.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) interests.add(t);
        }
        if (interests.isEmpty()) interests.add("hotplace");

        String cacheKey = city + "|" + days + "|" + pace + "|" + String.join(",", interests);
        if (CACHE.containsKey(cacheKey)) {
            resp.getWriter().write(CACHE.get(cacheKey));
            return;
        }

        JsonObject input = new JsonObject();
        input.addProperty("city", city);
        input.addProperty("daysCount", days);
        input.addProperty("pace", pace);
        input.add("interests", GSON.toJsonTree(interests));

        JsonObject result;
        try {
            if (!GEMINI_API_KEY.isEmpty()) {
                result = callGemini(input);
            } else {
                result = ruleBasedFallback(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ruleBasedFallback(input);
        }

        JsonObject fixed = validateAndFix(result, city, days);
        String json = GSON.toJson(fixed);
        CACHE.put(cacheKey, json);
        resp.getWriter().write(json);
    }

    /* ===== Gemini 호출 (OkHttp) ===== */
    private JsonObject callGemini(JsonObject userInput) throws IOException {
        String systemPrompt =
                "You are a travel planner. Respond in EXACT JSON following this schema only:\n" +
                "{ city, daysCount, pace, interests[], days:[{date, items:[{time,name,category,lat,lon,note}]}] }\n" +
                "Rules:\n" +
                "- Use valid lat/lon ranges.\n" +
                "- time format HH:MM (09:00,12:00,15:00,19:00 preferred).\n" +
                "- category in {spot, food, cafe, nature}.\n" +
                "- days.length == daysCount.\n" +
                "- Return JSON only, no explanations.";

        JsonObject body = new JsonObject();

        // system_instruction
        JsonObject sys = new JsonObject();
        JsonArray sysParts = new JsonArray();
        JsonObject sysPart = new JsonObject();
        sysPart.addProperty("text", systemPrompt);
        sysParts.add(sysPart);
        sys.add("parts", sysParts);
        body.add("system_instruction", sys);

        // contents (user)
        JsonArray contents = new JsonArray();
        JsonObject contentUser = new JsonObject();
        contentUser.addProperty("role", "user");
        JsonArray userParts = new JsonArray();
        JsonObject up = new JsonObject();
        up.addProperty("text", userInput.toString());
        userParts.add(up);
        contentUser.add("parts", userParts);
        contents.add(contentUser);
        body.add("contents", contents);

        JsonObject gen = new JsonObject();
        gen.addProperty("temperature", 0.6);
        gen.addProperty("max_output_tokens", 1024);
        gen.addProperty("response_mime_type", "application/json");
        body.add("generation_config", gen);

        Request request = new Request.Builder()
                .url(GEMINI_URL())
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(GSON.toJson(body), MediaType.parse("application/json")))
                .build();

        try (Response r = HTTP.newCall(request).execute()) {
            if (!r.isSuccessful()) {
                String err = r.body() != null ? r.body().string() : "";
                throw new IOException("Gemini HTTP " + r.code() + ": " + err);
            }
            String res = r.body() != null ? r.body().string() : "{}";
            JsonObject root = JsonParser.parseString(res).getAsJsonObject();
            JsonArray candidates = root.has("candidates") ? root.getAsJsonArray("candidates") : new JsonArray();
            if (candidates.size() == 0) throw new IOException("No candidates");
            JsonObject cand = candidates.get(0).getAsJsonObject();
            JsonObject content = cand.getAsJsonObject("content");
            JsonArray parts = (content != null && content.has("parts")) ? content.getAsJsonArray("parts") : new JsonArray();
            if (parts.size() == 0) throw new IOException("No parts in response");
            String jsonText = parts.get(0).getAsJsonObject().get("text").getAsString();
            return JsonParser.parseString(jsonText).getAsJsonObject();
        }
    }

    /* ===== 폴백 ===== */
    private JsonObject ruleBasedFallback(JsonObject input) {
        String city = input.get("city").getAsString();
        int days = input.get("daysCount").getAsInt();
        String pace = input.get("pace").getAsString();

        List<String> ints = new ArrayList<>();
        input.getAsJsonArray("interests").forEach(e -> ints.add(e.getAsString()));

        JsonObject out = new JsonObject();
        out.addProperty("city", city);
        out.addProperty("daysCount", days);
        out.addProperty("pace", pace);
        out.add("interests", GSON.toJsonTree(ints));

        JsonArray dayArr = new JsonArray();
        for (int d=1; d<=days; d++) {
            JsonObject day = new JsonObject();
            day.addProperty("date", "Day " + d);
            JsonArray items = new JsonArray();
            items.add(item("09:00","시작 포인트","spot",37.5665,126.9780,"시청 근처 집결"));
            items.add(item("12:00","로컬 맛집","food",37.57,126.982,"인기 메뉴 추천"));
            items.add(item("15:00","카페 브레이크","cafe",37.565,126.99,"디저트 세트"));
            items.add(item("19:00","야경 포인트","nature",37.5705,126.975,"뷰가 좋아요"));
            day.add("items", items);
            dayArr.add(day);
        }
        out.add("days", dayArr);
        return out;
    }

    private JsonObject item(String time, String name, String cat, double lat, double lon, String note){
        JsonObject o = new JsonObject();
        o.addProperty("time", time);
        o.addProperty("name", name);
        o.addProperty("category", cat);
        o.addProperty("lat", lat);
        o.addProperty("lon", lon);
        o.addProperty("note", note);
        return o;
    }

    /* ===== 검증/보정 ===== */
    private JsonObject validateAndFix(JsonObject r, String city, int days) {
        JsonObject out = new JsonObject();
        out.addProperty("city", city);
        out.addProperty("daysCount", days);
        out.addProperty("pace", sv(r,"pace","normal"));
        out.add("interests", r.has("interests") ? r.getAsJsonArray("interests") : new JsonArray());

        JsonArray daysIn = r.has("days") ? r.getAsJsonArray("days") : new JsonArray();
        JsonArray daysOut = new JsonArray();

        for (int i=0;i<days;i++){
            JsonObject dayIn = (i < daysIn.size() && daysIn.get(i).isJsonObject()) ? daysIn.get(i).getAsJsonObject() : new JsonObject();
            JsonArray itemsIn = dayIn.has("items") ? dayIn.getAsJsonArray("items") : new JsonArray();
            JsonArray itemsOut = new JsonArray();

            int count = 0;
            for (JsonElement el : itemsIn){
                if (!el.isJsonObject()) continue;
                JsonObject it = el.getAsJsonObject();
                String time = sv(it,"time","09:00");
                String name = sv(it,"name","Spot");
                String cat  = sv(it,"category","spot");
                double lat  = sd(it,"lat",37.5665);
                double lon  = sd(it,"lon",126.9780);
                String note = sv(it,"note","");

                if (lat < -90 || lat > 90) lat = 37.5665;
                if (lon < -180 || lon > 180) lon = 126.9780;
                if (!Arrays.asList("spot","food","cafe","nature").contains(cat)) cat = "spot";

                JsonObject fixed = new JsonObject();
                fixed.addProperty("time", time);
                fixed.addProperty("name", name);
                fixed.addProperty("category", cat);
                fixed.addProperty("lat", lat);
                fixed.addProperty("lon", lon);
                if (!note.isEmpty()) fixed.addProperty("note", note);
                itemsOut.add(fixed);
                if (++count >= 6) break;
            }

            if (itemsOut.size()==0){
                itemsOut.add(item("09:00","시작 포인트","spot",37.5665,126.9780,"집결지"));
                itemsOut.add(item("12:00","로컬 맛집","food",37.57,126.982,"추천 메뉴"));
                itemsOut.add(item("15:00","카페 브레이크","cafe",37.565,126.99,"디저트"));
                itemsOut.add(item("19:00","야경 포인트","nature",37.5705,126.975,"야경"));
            }

            JsonObject dayOut = new JsonObject();
            dayOut.addProperty("date", "Day " + (i+1));
            dayOut.add("items", itemsOut);
            daysOut.add(dayOut);
        }

        out.add("days", daysOut);
        return out;
    }

    /* ===== 유틸 ===== */
    private static String nv(String s, String def){ return (s==null||s.trim().isEmpty()) ? def : s.trim(); }
    private static int parseInt(String s, int def, int min, int max){
        try { int v = Integer.parseInt(s); return Math.max(min, Math.min(max, v)); }
        catch (Exception e){ return def; }
    }
    private static String sv(JsonObject o, String k, String def){
        return (o.has(k) && !o.get(k).isJsonNull()) ? o.get(k).getAsString() : def;
    }
    private static double sd(JsonObject o, String k, double def){
        try { return (o.has(k) && !o.get(k).isJsonNull()) ? o.get(k).getAsDouble() : def; }
        catch (Exception e){ return def; }
    }
}
