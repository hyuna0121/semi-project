<%@ page contentType="text/html; charset=UTF-8" %>
<%
  String ctx = request.getContextPath();
  String q = request.getQueryString(); // city=...&days=...&pace=...&interests=...
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 추천 일정 결과</title>
  <link rel="stylesheet" href="css/ai.css">
  <style>
    /* result 전용 보정 */
    .map-wrap{margin-top:20px;border-radius:16px;overflow:hidden;box-shadow:var(--shadow);}
    #map{height:400px;width:100%;}
    .tabs{justify-content:center;margin-top:24px;}
    .day-item{background:#fff;margin-bottom:12px;border-radius:12px;padding:14px 18px;border:1px solid var(--line);}
    .day-item b{font-size:17px}
    .day-item .time{width:70px;color:var(--muted)}
    .panel-wrap{margin-top:20px}
    .none{color:var(--muted);text-align:center;padding:30px}
  </style>
</head>
<body>
<div class="screen">
  <div class="topbar">
    <a class="back" href="ai5.jsp">←</a>
    <div class="step">결과</div>
  </div>

  <div class="card">
    <div class="hero">
      <div class="icon">📍</div>
      <h1 id="title">AI 추천 일정</h1>
      <div class="sub" id="subtitle">여행 정보를 불러오는 중...</div>
    </div>

    <div class="map-wrap">
      <div id="map"></div>
    </div>

    <div class="tabs" id="tabs"></div>
    <div class="panel-wrap" id="panels"></div>
  </div>

  <div class="bottom">
    <button class="btn-primary" type="button" onclick="location.href='ai0.jsp'">처음으로</button>
  </div>
</div>

<script>
  // 도시명 변환
  function toKCity(code){
    const map = {
      seoul:"서울", busan:"부산", jeju:"제주", tokyo:"도쿄", osaka:"오사카",
      sapporo:"삿포로", nagoya:"나고야", okinawa:"오키나와", hongkong:"홍콩",
      shanghai:"상하이", beijing:"베이징", macau:"마카오", gyeongju:"경주", yeosu:"여수"
    };
    return map[code] || code;
  }

  let gmap=null, gmarkers=[], ginfowin=null;

  function clearMarkers(){
    gmarkers.forEach(m=>m.setMap(null));
    gmarkers=[];
    if(ginfowin) ginfowin.close();
  }

  function fitBoundsIfAny(bounds){
    try{ if(bounds && !bounds.isEmpty()) gmap.fitBounds(bounds); }catch(e){}
  }

  // API 호출
  (function(){
    const base = "<%=ctx%>/ai/recommend";
    const url = base + <%= (q!=null && !q.isEmpty()) ? "'?"+q+"'" : "''" %>;
    fetch(url)
      .then(r=>r.text())
      .then(txt=>{
        try { return JSON.parse(txt); }
        catch(e){ throw new Error("Invalid JSON: "+txt.slice(0,200)); }
      })
      .then(data=>{
        if(data.error){ alert("서버 오류: "+data.message); return; }
        renderResult(data);
      })
      .catch(e=>{
        alert("추천 데이터를 불러오는 중 오류: "+e.message);
        console.error(e);
      });
  })();

  // 렌더링
  function renderResult(r){
    document.getElementById("title").textContent = toKCity(r.city)+" "+(r.daysCount||r.days?.length||0)+"일 여행 추천";
    document.getElementById("subtitle").textContent = "관심사: "+(r.interests?.join(", ")||"-")+" · 페이스: "+(r.pace||"-");

    const mapEl=document.getElementById("map");
    if(!(window.google && window.google.maps)){
      mapEl.textContent="구글 맵을 불러오지 못했습니다.";
      return;
    }

    gmap = new google.maps.Map(mapEl,{
      center:{lat:37.5665,lng:126.9780},
      zoom:12,
      mapTypeControl:false,
      streetViewControl:false
    });
    ginfowin = new google.maps.InfoWindow();

    const tabs=document.getElementById("tabs");
    const panels=document.getElementById("panels");
    tabs.innerHTML="";
    panels.innerHTML="";
    clearMarkers();

    const days=r.days||[];
    if(!days.length){
      panels.innerHTML="<div class='none'>추천 일정이 없습니다.</div>";
      return;
    }

    const bounds=new google.maps.LatLngBounds();

    days.forEach((day,i)=>{
      // 탭 버튼
      const btn=document.createElement("button");
      btn.textContent="Day "+(i+1);
      btn.className=i===0?"active":"";
      btn.addEventListener("click",()=>{
        document.querySelectorAll(".tabs button").forEach(b=>b.classList.remove("active"));
        btn.classList.add("active");
        document.querySelectorAll(".day-panel").forEach((p,idx)=>p.style.display=(idx===i)?"block":"none");
        resetMapForDay(days[i]);
      });
      tabs.appendChild(btn);

      // 일정 리스트
      const panel=document.createElement("div");
      panel.className="day-panel";
      if(i!==0) panel.style.display="none";

      day.items?.forEach((it,j)=>{
        const row=document.createElement("div");
        row.className="day-item";
        row.innerHTML="<div class='time'>"+(it.time||"")+"</div>"
          +"<div class='meta'><b>"+(j+1)+". "+(it.name||"")+"</b>"
          +"<span class='badge'>"+(it.category||"spot")+"</span>"
          +(it.note?"<div class='note'>"+it.note+"</div>":"")+"</div>";
        panel.appendChild(row);
      });
      panels.appendChild(panel);
    });
    resetMapForDay(days[0]);
  }

  // 지도 갱신
  function resetMapForDay(dayObj){
    clearMarkers();
    const bounds=new google.maps.LatLngBounds();
    (dayObj.items||[]).forEach((it,j)=>{
      if(typeof it.lat==="number" && typeof it.lon==="number"){
        const pos={lat:it.lat,lng:it.lon};
        const marker=new google.maps.Marker({position:pos,map:gmap,label:String(j+1)});
        marker.addListener("click",()=>{
          const html="<div style='min-width:180px'><b>"+(j+1)+". "+(it.name||"")+"</b><br>"
                    +"<small>"+(it.time||"")+" · "+(it.category||"spot")+"</small>"
                    +(it.note?"<div style='margin-top:6px'>"+it.note+"</div>":"")+"</div>";
          ginfowin.setContent(html);
          ginfowin.open(gmap,marker);
        });
        gmarkers.push(marker);
        bounds.extend(pos);
      }
    });
    fitBoundsIfAny(bounds);
  }
</script>

<!-- Google Maps JS API: 본인 키로 교체 -->
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB51GcI3hnltyOrrEqdW2EtfppSGXpR7hw&v=weekly" async defer></script>
</body>
</html>
