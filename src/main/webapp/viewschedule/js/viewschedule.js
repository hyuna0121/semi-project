// ===== Kakao Maps 버전 =====
let map, infoWindow, bounds;
let markers = []; // { id, category, marker, overlay }
const categoryColors = {
  spot:"#22c55e", food:"#f59e0b", cafe:"#14b8a6", hotel:"#8b5cf6", transport:"#ef4444"
};

// Kakao SDK가 로드된 뒤 실행
kakao.maps.load(initKakao);

function initKakao(){
  const data = window.__ITINERARY__;
  hydrateHeader(data);

  // 초기 중심
  const first = data.days[0].items[0];
  map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(first.lat, first.lng),
    level: 6
  });

  infoWindow = new kakao.maps.InfoWindow({ removable:false });

  renderDayTabs(data.days);
  renderDays(data.days);
  drawMarkersForDay(0);
  wireTopActions();
}

function hydrateHeader(data){
  document.getElementById("tripTitle").textContent = data.title;
  const start = new Date(data.startDate);
  const end = new Date(data.endDate);
  const nights = Math.max(0, Math.round((end - start) / (1000*60*60*24)));
  document.getElementById("tripDate").textContent =
    formatDateDot(start) + " ~ " + formatDateDot(end) + " (" + nights + "박" + (nights+1) + "일)";
  document.getElementById("tripCompanions").textContent = "동행: " + data.companions + "명";
  document.getElementById("tripBudget").textContent = "예산: ₩" + data.budgetKRW.toLocaleString();
}

function renderDayTabs(days){
  const tabs = document.getElementById("dayTabs");
  tabs.innerHTML = "";
  days.forEach(function(d, idx){
    const el = document.createElement("button");
    el.className = "day-tab" + (idx===0 ? " active" : "");
    el.textContent = d.label || ("Day " + (idx+1));
    el.dataset.index = idx;
    el.addEventListener("click", function(){
      document.querySelectorAll(".day-tab").forEach(function(t){ t.classList.remove("active"); });
      el.classList.add("active");
      switchDay(idx);
    });
    tabs.appendChild(el);
  });
}

function renderDays(days){
  const container = document.getElementById("dayContainer");
  container.innerHTML = "";
  days.forEach(function(d, idx){
    const dayEl = document.createElement("section");
    dayEl.className = "day";
    dayEl.dataset.index = idx;
    dayEl.innerHTML = "<h3>" + (d.label || ("Day " + (idx+1))) + " · " + formatDateDot(new Date(d.date)) + "</h3>";
    d.items.forEach(function(item){
      const card = document.createElement("div");
      card.className = "item";
      card.dataset.id = item.id;
      card.dataset.category = item.category;
      card.innerHTML =
        '<div class="time">' + item.time + '</div>' +
        '<div>' +
          '<div class="title">' + item.title + '</div>' +
          '<div class="memo">' + (item.memo || "") + '</div>' +
          '<div class="tags">' +
            '<span class="badge ' + item.category + '">' + mapCategoryKorean(item.category) + '</span>' +
          '</div>' +
        '</div>';
      card.addEventListener("click", function(){ focusMarker(item.id); });
      if(idx!==0) dayEl.style.display = "none";
      dayEl.appendChild(card);
    });
    container.appendChild(dayEl);
  });
}

function switchDay(dayIndex){
  document.querySelectorAll(".day").forEach(function(d){
    d.style.display = Number(d.dataset.index)===dayIndex ? "block" : "none";
  });
  drawMarkersForDay(dayIndex);
}

function drawMarkersForDay(dayIndex){
  clearMarkers();
  const day = window.__ITINERARY__.days[dayIndex];
  bounds = new kakao.maps.LatLngBounds();

  day.items.forEach(function(item, order){
    const pos = new kakao.maps.LatLng(item.lat, item.lng);

    // 1) 마커
    const marker = new kakao.maps.Marker({ position: pos });
    marker.setMap(map);

    // 2) 순번 뱃지용 커스텀 오버레이(라벨 대용)
    const badge = document.createElement('div');
    badge.style.cssText =
      "transform:translate(-50%,-50%);padding:2px 6px;border-radius:999px;" +
      "background:" + (categoryColors[item.category] || "#2563eb") + ";color:#fff;font-size:12px;font-weight:700;";
    badge.textContent = String(order + 1);

    const overlay = new kakao.maps.CustomOverlay({
      position: pos,
      content: badge,
      yAnchor: 1.2
    });
    overlay.setMap(map);

    // 3) 인포윈도우
    var memoHtml = item.memo ? ('<div style="margin-top:6px">' + item.memo + '</div>') : '';
    var infoHtml =
      '<div style="font-size:13px;min-width:220px">' +
        '<div style="font-weight:700">' + item.time + ' · ' + item.title + '</div>' +
        '<div style="color:#6b7280;margin-top:4px">' + mapCategoryKorean(item.category) + '</div>' +
        memoHtml +
      '</div>';

    kakao.maps.event.addListener(marker, 'click', function(){
      openInfo(marker, infoHtml);
      activateListItem(item.id);
    });

    markers.push({ id:item.id, category:item.category, marker:marker, overlay:overlay });
    bounds.extend(pos);
  });

  if (!bounds.isEmpty()) map.setBounds(bounds);
  applyCategoryFilter();
}

function openInfo(marker, html){
  infoWindow.setContent(html);
  infoWindow.open(map, marker);
}

function focusMarker(id){
  const rec = markers.find(function(m){ return m.id === id; });
  if(!rec) return;
  map.panTo(rec.marker.getPosition());
  openInfo(rec.marker,
    '<div style="font-size:13px">선택한 위치로 이동했습니다.</div>'
  );
  activateListItem(id);
}

function activateListItem(id){
  document.querySelectorAll(".item").forEach(function(el){
    el.classList.toggle("active", el.dataset.id === id);
  });
}

function clearMarkers(){
  markers.forEach(function(rec){
    if (rec.marker) rec.marker.setMap(null);
    if (rec.overlay) rec.overlay.setMap(null);
  });
  markers = [];
  if (infoWindow) infoWindow.close();
}

function wireTopActions(){
  document.getElementById("categoryFilter").addEventListener("change", applyCategoryFilter);
  document.getElementById("fitAllBtn").addEventListener("click", function(){
    if (bounds && !bounds.isEmpty()) map.setBounds(bounds);
  });
}

function applyCategoryFilter(){
  const val = document.getElementById("categoryFilter").value;

  document.querySelectorAll(".item").forEach(function(el){
    const show = (val === "all") || (el.dataset.category === val);
    el.style.display = show ? "grid" : "none";
  });

  // Kakao는 setVisible이 없어서 setMap(null/map)로 토글
  markers.forEach(function(rec){
    const show = (val === "all") || (rec.category === val);
    if (show){
      rec.marker.setMap(map);
      rec.overlay.setMap(map);
    } else {
      rec.marker.setMap(null);
      rec.overlay.setMap(null);
    }
  });
}

function formatDateDot(d){
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth()+1).padStart(2,"0");
  const dd = String(d.getDate()).padStart(2,"0");
  return yyyy + "." + mm + "." + dd;
}

function mapCategoryKorean(c){
  switch(c){
    case "spot": return "관광";
    case "food": return "맛집";
    case "cafe": return "카페";
    case "hotel": return "숙소";
    case "transport": return "이동";
    default: return c;
  }
}
