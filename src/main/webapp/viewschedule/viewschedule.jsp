<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>여행 일정 한눈에 보기</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <!-- CSS 절대경로로 연결 -->
  <link rel="stylesheet" href="<c:url value='/viewschedule/css/viewschedule.css'/>">
</head>
<body>
  <!-- 상단 요약 바 -->
  <header class="topbar">
    <div class="trip-title">
      <h1 id="tripTitle">오사카·교토 3박4일</h1>
      <div class="meta">
        <span id="tripDate">2025.11.12 ~ 2025.11.15 (3박4일)</span>
        <span class="dot">•</span>
        <span id="tripCompanions">동행: 3명</span>
        <span class="dot">•</span>
        <span id="tripBudget">예산: ₩1,200,000</span>
      </div>
    </div>
    <div class="top-actions">
      <select id="categoryFilter" title="카테고리">
        <option value="all">전체</option>
        <option value="spot">관광</option>
        <option value="food">맛집</option>
        <option value="cafe">카페</option>
        <option value="hotel">숙소</option>
        <option value="transport">이동</option>
      </select>
      <button id="fitAllBtn" class="ghost">모두 보기</button>
    </div>
  </header>

  <!-- 좌측 일정 / 우측 지도 -->
  <main class="layout">
    <section class="left">
      <nav class="day-tabs" id="dayTabs"></nav>
      <div id="dayContainer" class="day-container"></div>
    </section>

    <aside class="right">
      <div id="map" class="map"></div>
      <div class="legend">
        <span class="chip spot">관광</span>
        <span class="chip food">맛집</span>
        <span class="chip cafe">카페</span>
        <span class="chip hotel">숙소</span>
        <span class="chip transport">이동</span>
      </div>
    </aside>
  </main>

  <!-- 일정 데이터 샘플 -->
  <script>
    const itinerary = {
      title: "오사카·교토 3박4일",
      startDate: "2025-11-12",
      endDate: "2025-11-15",
      companions: 3,
      budgetKRW: 1200000,
      days: [
        {
          date: "2025-11-12",
          label: "Day 1 (수)",
          items: [
            { id:"d1_1", time:"09:30", title:"간사이공항 도착", category:"transport",
              lat:34.432, lng:135.232, memo:"라피트 특급 탑승" },
            { id:"d1_2", time:"11:00", title:"난바 파크스", category:"spot",
              lat:34.661, lng:135.506, memo:"옥상정원 산책" },
            { id:"d1_3", time:"12:30", title:"쿠시카츠 다루마", category:"food",
              lat:34.664, lng:135.503, memo:"점심" },
            { id:"d1_4", time:"15:00", title:"호텔 체크인(신사이바시)", category:"hotel",
              lat:34.673, lng:135.501, memo:"짐 풀기" }
          ]
        },
        {
          date: "2025-11-13",
          label: "Day 2 (목)",
          items: [
            { id:"d2_1", time:"09:00", title:"교토 후시미 이나리", category:"spot",
              lat:34.967, lng:135.772, memo:"빨간 토리이" },
            { id:"d2_2", time:"12:00", title:"이치란 라멘 교토", category:"food",
              lat:35.003, lng:135.770, memo:"점심" },
            { id:"d2_3", time:"14:00", title:"기온 산책", category:"spot",
              lat:35.003, lng:135.778, memo:"거리 산책/카페" }
          ]
        },
        {
          date: "2025-11-14",
          label: "Day 3 (금)",
          items: [
            { id:"d3_1", time:"10:00", title:"오사카성", category:"spot",
              lat:34.687, lng:135.525, memo:"성 내부 관람" },
            { id:"d3_2", time:"13:00", title:"도톤보리 식사", category:"food",
              lat:34.668, lng:135.501, memo:"타코야키" },
            { id:"d3_3", time:"16:00", title:"우메다 공중정원", category:"spot",
              lat:34.705, lng:135.489, memo:"야경" }
          ]
        },
        {
          date: "2025-11-15",
          label: "Day 4 (토)",
          items: [
            { id:"d4_1", time:"10:00", title:"신사이바시 쇼핑", category:"spot",
              lat:34.674, lng:135.501, memo:"기념품" },
            { id:"d4_2", time:"14:30", title:"간사이공항 이동", category:"transport",
              lat:34.432, lng:135.232, memo:"귀국" }
          ]
        }
      ]
    };
    window.__ITINERARY__ = itinerary;
  </script>

  <!-- JS -->
  <script src="<c:url value='/viewschedule/js/viewschedule.js'/>"></script>

  <!-- Google Maps API (※ 실제 키로 교체하세요) -->
  <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB51GcI3hnltyOrrEqdW2EtfppSGXpR7hw&callback=initMap">
  </script>
</body>
</html>
