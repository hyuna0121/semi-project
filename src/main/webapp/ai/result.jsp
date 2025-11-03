<%@ page contentType="text/html; charset=UTF-8" %>
<%
  String ctx = request.getContextPath();              // 예: /semi-project
  String q   = request.getQueryString();              // ex) city=seoul&days=2&pace=normal&interests=hotplace
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 카드형 결과 (Google Maps)</title>
  <!-- 절대경로로 공통 CSS 로드 -->
  <link rel="stylesheet" href="<%=ctx%>/ai/css/ai.css">
  <style>
    .result-card{width:min(1100px,95vw);margin:22px auto}
    .city-head{display:flex;align-items:center;gap:14px;margin-bottom:10px}
    .city-head h2{margin:0}
    /* 지도 높이 필수 */
    #map{height:360px!important;border-radius:12px;margin-bottom:12px}
    /* 진단 패널 */
    #diag{
      font:14px/1.4 system-ui,-apple-system,Segoe UI,Roboto,'Noto Sans KR',sans-serif;
      background:#fff3cd;color:#533f03;border:1px solid #ffe69c;
      padding:8px 10px;border-radius:8px;width:min(1100px,95vw);margin:12px auto 0
    }
  </style>
</head>
<body>

  <!-- 진단 패널(상태 로그 표시) -->
  <div id="diag">상태: 초기화 중…</div>

  <div class="result-card">
    <div class="city-head">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#2563eb" viewBox="0 0 24 24">
  	  <path d="M15 3l-6 2-6-2v16l6 2 6-2 6 2V5l-6-2zm-1 2.47V19l-4 1.33V6.8L14 5.47zM5 5.47L8 6.5V20L5 18.97V5.47zM19 6.5v13.5l-3-1.03V5.47l3 1.03z"/>
	  </svg>

      <div>
        <h2 id="title"></h2>
        <div id="subtitle" style="color:#64748b"></div>
      </div>
    </div>

    <div id="map"></div>
    <div class="tabs" id="tabs"></div>
    <div id="panels"></div>
  </div>

  <script>
    // ===== 진단 로그 헬퍼 =====
    (function(){
      var box=document.getElementById('diag');
      function log(s){ if(box) box.textContent="상태: "+s; }
      window._diagLog=log;
      window.addEventListener('error', function(e){ log("스크립트 오류: "+(e.message||e)); });
      window.addEventListener('unhandledrejection', function(e){
        var r=e.reason; log("Promise 오류: "+(r&&r.message?r.message:r));
      });
      log("페이지 로드");
    })();

    // ===== 전역 상태 =====
    window._gmLoaded=false;      // Google Maps 로드 완료?
    window._dataReady=false;     // 추천 데이터 준비됨?
    window._data=null;           // 추천 데이터
    window.gmap=null; window.gmarkers=[]; window.ginfowin=null;

    // ===== 도시명 변환 =====
    function toKCity(code){
      var map={seoul:"서울",busan:"부산",jeju:"제주",tokyo:"도쿄",osaka:"오사카",sapporo:"삿포로",
               nagoya:"나고야",okinawa:"오키나와",hongkong:"홍콩",shanghai:"상하이",
               beijing:"베이징",macau:"마카오",gyeongju:"경주",yeosu:"여수"};
      return map[code]||code;
    }

    // ===== Google Maps 콜백 (script ?callback=gmReady) =====
    function gmReady(){
      window._gmLoaded=true;
      (window._diagLog||function(){})("Google Maps 로드 성공");
      tryRender();
    }

    // ===== 추천 API 호출 =====
    (function(){
      var log=window._diagLog||function(){};
      var url="<%=ctx%>/ai/recommend" + <%= (q!=null && !q.isEmpty()) ? "'?"+q+"'" : "''" %>;
      log("추천 API 호출: "+url);

      fetch(url).then(function(res){
        return res.text().then(function(body){
          if(!res.ok){ throw new Error("HTTP "+res.status+" / "+body); }
          try{ return JSON.parse(body); }
          catch(e){ throw new Error("Invalid JSON: "+body.slice(0,200)); }
        });
      }).then(function(data){
        if(data && data.error){ log("서버 오류: "+(data.message||"unknown")); return; }
        window._data=data; window._dataReady=true;
        log("추천 API OK, 지도 준비 대기…");
        tryRender();
      }).catch(function(err){
        log("추천 API 오류: "+err.message);
        console.error(err);
      });
    })();

    // ===== 두 조건(지도+데이터) 충족 시 렌더 =====
    function tryRender(){
      var log=window._diagLog||function(){};
      if(!window._gmLoaded){ log("지도 대기중…"); return; }
      if(!window._dataReady){ log("데이터 대기중…"); return; }
      renderResult(window._data);
      log("렌더 완료");
    }

    // ===== 렌더링 =====
    function renderResult(r){
      var title=document.getElementById("title");
      var sub=document.getElementById("subtitle");
      title.textContent = toKCity(r.city)+", "+(r.daysCount || (r.days ? r.days.length : 0))+"일 추천일정입니다.";
      var ints=(r.interests && r.interests.join(", "))||"";
      sub.textContent="관심사: "+ints+" · 페이스: "+(r.pace||"");

      if(!(window.google && window.google.maps)){
        (window._diagLog||function(){})("구글 객체 없음");
        document.getElementById("map").textContent="구글맵 로드 실패 (API키/리퍼러/결제 확인)";
        return;
      }

      var mapEl=document.getElementById("map");
      window.gmap=new google.maps.Map(mapEl,{
        center:{lat:37.5665,lng:126.9780}, zoom:12,
        mapTypeControl:false, streetViewControl:false, fullscreenControl:true
      });
      window.ginfowin=new google.maps.InfoWindow();

      var tabs=document.getElementById("tabs");
      var panels=document.getElementById("panels");
      tabs.innerHTML=""; panels.innerHTML="";
      window.gmarkers=[];

      var days=r.days||[];
      for(var i=0;i<days.length;i++){
        (function(i){
          // 탭
          var btn=document.createElement("button");
          btn.textContent="Day "+(i+1);
          if(i===0) btn.classList.add("active");
          btn.onclick=function(){
            var all=document.querySelectorAll(".tabs button");
            for(var k=0;k<all.length;k++) all[k].classList.remove("active");
            btn.classList.add("active");
            var ps=document.querySelectorAll(".day-panel");
            for(var k2=0;k2<ps.length;k2++) ps[k2].style.display=(k2===i)?"block":"none";
            resetMapForDay(days[i]);
          };
          tabs.appendChild(btn);

          // 패널
          var panel=document.createElement("div");
          panel.className="day-panel";
          if(i!==0) panel.style.display="none";

          var items=days[i].items || [];
          for(var j=0;j<items.length;j++){
            var it=items[j];
            var row=document.createElement("div");
            row.className="day-item";
            var html=""
              + "<div class='time'>" + (it.time || "") + "</div>"
              + "<div class='meta'>"
              +   "<b>" + (j+1) + ". " + (it.name || "") + "</b>"
              +   "<span class='badge'>" + (it.category || "spot") + "</span>";
            if (it.note){ html += "<div class='note'>" + it.note + "</div>"; }
            html += "</div>";
            row.innerHTML=html;
            panel.appendChild(row);
          }
          panels.appendChild(panel);
        })(i);
      }
      if(days.length>0) resetMapForDay(days[0]);
    }

    function resetMapForDay(dayObj){
      // 마커 리셋
      for(var i=0;i<window.gmarkers.length;i++){
        if(window.gmarkers[i] && window.gmarkers[i].setMap) window.gmarkers[i].setMap(null);
      }
      window.gmarkers=[];
      var bounds=new google.maps.LatLngBounds();
      var items=(dayObj && dayObj.items) ? dayObj.items : [];
      for(var j=0;j<items.length;j++){
        var it=items[j];
        if(typeof it.lat==="number" && typeof it.lon==="number"){
          var pos={lat:it.lat,lng:it.lon};
          var marker=new google.maps.Marker({position:pos,map:window.gmap,label:String(j+1)});
          (function(m,it,j){
            m.addListener("click",function(){
              var html="<div style='min-width:180px'>"
                      + "<b>"+(j+1)+". "+(it.name||"")+"</b><br>"
                      + "<small>"+(it.time||"")+" · "+(it.category||"spot")+"</small>";
              if(it.note){ html+="<div style='margin-top:6px'>"+it.note+"</div>"; }
              html+="</div>";
              if(!window.ginfowin) window.ginfowin=new google.maps.InfoWindow();
              window.ginfowin.setContent(html); window.ginfowin.open(window.gmap,m);
            });
          })(marker,it,j);
          window.gmarkers.push(marker);
          try{ bounds.extend(pos); }catch(e){}
        }
      }
      try{
        if(window.gmarkers.length) window.gmap.fitBounds(bounds);
        else { window.gmap.setCenter({lat:37.5665,lng:126.9780}); window.gmap.setZoom(12); }
      }catch(e){}
    }
  </script>

  <!-- ✅ 본인 Google Maps API 키로 교체하세요 -->
  <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB51GcI3hnltyOrrEqdW2EtfppSGXpR7hw&v=weekly&v=weekly&callback=gmReady" async defer></script>
</body>
</html>
