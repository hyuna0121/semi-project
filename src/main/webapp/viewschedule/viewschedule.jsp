<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>여행 일정 한눈에 보기</title>
<meta name="viewport" content="width=device-width, initial-scale=1" />

<link rel="stylesheet" href="<c:url value='/viewschedule/css/viewschedule.css'/>">

<!-- Kakao Maps SDK (여기 JavaScript 키로 교체) -->
<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=ff3bef976f88c37cbea42d17e34c311d&libraries=services&autoload=false"></script>

<style>#map.map{min-height:420px}</style>
</head>
<body>
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

  <!-- 일정 데이터 (샘플) -->
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

  <!-- Kakao 지도 + 렌더링 -->
  <script>
    let map, info, markers = [];
    let currentDayIndex = 0;

    const catEmoji = { spot:"🗺️", food:"🍜", cafe:"☕", hotel:"🏨", transport:"🚆" };

    kakao.maps.load(init);

    function init(){
      // 상단 요약
      document.getElementById('tripTitle').textContent = itinerary.title;
      document.getElementById('tripDate').textContent =
        itinerary.startDate + " ~ " + itinerary.endDate + " (" + (itinerary.days.length-1) + "박" + itinerary.days.length + "일)";
      document.getElementById('tripCompanions').textContent = "동행: " + itinerary.companions + "명";
      document.getElementById('tripBudget').textContent = "예산: ₩" + itinerary.budgetKRW.toLocaleString();

      // 지도
      map = new kakao.maps.Map(document.getElementById('map'), {
        center: new kakao.maps.LatLng(34.68, 135.50), level: 7
      });
      info = new kakao.maps.InfoWindow({removable:false});

      // UI
      document.getElementById('categoryFilter').addEventListener('change', renderDay);
      document.getElementById('fitAllBtn').addEventListener('click', fitVisible);

      renderTabs();
      renderDay(0);
    }

    function renderTabs(){
      const tabs = document.getElementById('dayTabs');
      tabs.innerHTML = '';
      for (var i=0;i<itinerary.days.length;i++){
        (function(idx){
          const d = itinerary.days[idx];
          const b = document.createElement('button');
          b.textContent = d.label ? d.label : ("Day " + (idx+1));
          if(idx===0) b.classList.add('active');
          b.addEventListener('click', function(){
            currentDayIndex = idx;
            document.querySelectorAll('#dayTabs button').forEach(function(x){ x.classList.remove('active'); });
            b.classList.add('active');
            renderDay();
          });
          tabs.appendChild(b);
        })(i);
      }
    }

    function clearMarkers(){
      markers.forEach(function(m){ m.setMap(null); });
      markers = [];
      info.close();
    }

    function renderDay(forceIndex){
      if (typeof forceIndex === 'number') currentDayIndex = forceIndex;

      const day = itinerary.days[currentDayIndex];
      const filter = document.getElementById('categoryFilter').value;

      // 좌측 리스트
      const box = document.getElementById('dayContainer');
      box.innerHTML = '';
      day.items.forEach(function(it, idx){
        if (filter !== 'all' && it.category !== filter) return;
        const row = document.createElement('div');
        row.className = 'item';

        var html =
          '<div class="time">' + (it.time || '') + '</div>' +
          '<div class="title">' + (catEmoji[it.category] || '📍') + ' ' + (idx+1) + '. ' + it.title + '</div>' +
          '<div class="memo">' + (it.memo || '') + '</div>';

        row.innerHTML = html;
        row.addEventListener('click', function(){ focusMarker(it, idx+1); });
        box.appendChild(row);
      });

      // 마커
      clearMarkers();
      const bounds = new kakao.maps.LatLngBounds();
      day.items.forEach(function(it, idx){
        if (filter !== 'all' && it.category !== filter) return;
        if (typeof it.lat !== 'number' || typeof it.lng !== 'number') return;

        const pos = new kakao.maps.LatLng(it.lat, it.lng);
        const marker = new kakao.maps.Marker({ position: pos });
        marker.setMap(map);
        markers.push({ marker: marker, data: it, order: idx+1 });
        bounds.extend(pos);

        kakao.maps.event.addListener(marker, 'click', function(){ openInfo(marker, it, idx+1); });
      });

      if (!bounds.isEmpty()) map.setBounds(bounds);
    }

    function openInfo(marker, it, order){
      var memoHtml = it.memo ? ('<div style="margin-top:6px">' + it.memo + '</div>') : '';
      var html =
        '<div style="min-width:220px">' +
          '<b>' + order + '. ' + it.title + '</b><br/>' +
          '<small>' + (it.time || '') + ' · ' + it.category + '</small>' +
          memoHtml +
        '</div>';
      info.setContent(html);
      info.open(map, marker);
    }

    function focusMarker(it, order){
      const found = markers.find(function(m){ return m.data.id === it.id; });
      if (found){
        map.panTo(found.marker.getPosition());
        openInfo(found.marker, found.data, order);
      }
    }

    function fitVisible(){
      const bounds = new kakao.maps.LatLngBounds();
      markers.forEach(function(m){ bounds.extend(m.marker.getPosition()); });
      if (!bounds.isEmpty()) map.setBounds(bounds);
    }
  </script>

  <!-- (필요시) 기존 앱 다른 스크립트 유지 -->
  <script src="<c:url value='/viewschedule/js/viewschedule.js'/>"></script>
</body>
</html>
