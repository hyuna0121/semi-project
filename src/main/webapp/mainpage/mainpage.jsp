<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../header.jsp" %>
<%@ page import="java.util.*" %>

<%
    // 샘플 일정 데이터
    List<Map<String, String>> schedules = new ArrayList<>();
    schedules.add(Map.of("name", "여행 일정 1", "type", "my"));
    schedules.add(Map.of("name", "친구 여행 일정 1", "type", "friend"));
    schedules.add(Map.of("name", "여행 일정 2", "type", "my"));

    // 2025년 10월
    Calendar cal = new GregorianCalendar(2025, Calendar.OCTOBER, 1);
    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    int startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

    // 일정 포함 날짜 (샘플)
    Set<Integer> myDays = new HashSet<>(Arrays.asList(13, 14, 15, 20));
    Set<Integer> friendDays = new HashSet<>(Arrays.asList(20, 21, 22, 23));
%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>일정 보기</title>
<style>
  body {
    font-family: 'Noto Sans KR', sans-serif;
    background-color: #fafafa;
    margin: 0;
  }

  /* 메인 콘텐츠 영역 */
  .content-container {
    max-width: 1000px;
    margin: 120px auto 100px; /* 헤더, 푸터 공간 확보 */
    background-color: #fff;
    padding: 30px 40px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    text-align: center;
  }

  /* 상단 바 */
  .top-bar {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-bottom: 15px;
  }

  /* 배너 */
  .banner {
    background: #f2f2f2;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    border: 1px solid #ddd;
    border-radius: 6px;
  }

  /* 일정 목록 */
  .schedule-list {
    margin-bottom: 40px;
    text-align: left;
  }

  .schedule-list div {
    padding: 10px;
    border: 1px solid #ddd;
    margin-bottom: 5px;
    border-radius: 5px;
  }

  .friend { background-color: #e3f2fd; }

  /* 달력 */
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    text-align: center;
  }

  th, td {
    border: 1px solid #ddd;
    padding: 8px;
    height: 60px;
  }

  th { background: #f7f7f7; }

  .myDay { background: #e0f7fa; }
  .friendDay { background: #fff3e0; }
  .both { background: #ffcdd2; }
</style>
</head>

<body>

  <div class="content-container">

    <div class="top-bar">
      <button class="btn btn-outline-secondary btn-sm">문고</button>
      <button class="btn btn-outline-secondary btn-sm">마이페이지</button>
      <button class="btn btn-outline-secondary btn-sm">일정 검색</button>
    </div>

    <div class="banner">배너 이미지 영역</div>

    <h2>내 일정</h2>
    <div class="schedule-list">
      <% for (Map<String, String> s : schedules) { %>
        <div class="<%= "friend".equals(s.get("type")) ? "friend" : "" %>">
          <%= s.get("name") %>
        </div>
      <% } %>
    </div>

    <h2>2025년 10월</h2>
    <table>
      <thead>
        <tr>
          <th>일</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th>
        </tr>
      </thead>
      <tbody>
        <%
          int day = 1;
          for (int week = 0; week < 6 && day <= maxDay; week++) {
        %>
        <tr>
          <% for (int d = 1; d <= 7; d++) {
                String cls = "";
                String content = "";

                if (week == 0 && d < startDayOfWeek) {
                    content = "";
                } else if (day <= maxDay) {
                    boolean mine = myDays.contains(day);
                    boolean fr = friendDays.contains(day);
                    cls = mine && fr ? "both" : mine ? "myDay" : fr ? "friendDay" : "";
                    content = String.valueOf(day);
                    day++;
                }
          %>
            <td class="<%= cls %>"><%= content %></td>
          <% } %>
        </tr>
        <% } %>
      </tbody>
    </table>

  </div>

<%@ include file="../footer.jsp" %>
</body>
</html>
