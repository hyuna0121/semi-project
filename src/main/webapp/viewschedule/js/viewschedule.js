let map, markers = [], infoWindows = [], bounds;
const categoryColors = {
  spot:"#22c55e", food:"#f59e0b", cafe:"#14b8a6", hotel:"#8b5cf6", transport:"#ef4444"
};

function initMap(){
  const data = window.__ITINERARY__;
  hydrateHeader(data);
  const first = data.days[0].items[0];

  map = new google.maps.Map(document.getElementById("map"), {
    center: {lat:first.lat, lng:first.lng},
    zoom: 12,
    mapTypeControl:false,
    streetViewControl:false
  });

  renderDayTabs(data.days);
  renderDays(data.days);
  drawMarkersForDay(0);
  wireTopActions();
}

function hydrateHeader(data){
  document.getElementById("tripTitle").textContent = data.title;
  const start = new Date(data.startDate);
  const end = new Date(data.endDate);
  const nights = Math.max(0, Math.round((end-start)/(1000*60*60*24)));
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
      document.querySelectorAll(".day-tab").forEach(function(t){t.classList.remove("active");});
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
      dayEl.appendChild(card);
    });
    if(idx!==0) dayEl.style.display = "none";
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
  bounds = new google.maps.LatLngBounds();

  day.items.forEach(function(item, order){
    const pos = {lat:item.lat, lng:item.lng};
    const marker = new google.maps.Marker({
      position:pos,
      map:map,
      label:{text:String(order+1), fontSize:"12px"},
      icon:{
        path:google.maps.SymbolPath.CIRCLE,
        fillColor:categoryColors[item.category] || "#2563eb",
        fillOpacity:0.9,
        strokeWeight:1,
        scale:8
      }
    });
    const info = new google.maps.InfoWindow({
      content:
        '<div style="font-size:13px;">' +
        '<div style="font-weight:700">' + item.time + ' · ' + item.title + '</div>' +
        '<div style="color:#6b7280;margin-top:4px">' + mapCategoryKorean(item.category) + '</div>' +
        (item.memo ? '<div style="margin-top:6px">' + item.memo + '</div>' : '') +
        '</div>'
    });
    marker.addListener("click", function(){
      openInfo(marker, info);
      activateListItem(item.id);
    });
    markers.push({id:item.id, category:item.category, marker:marker});
    infoWindows.push(info);
    bounds.extend(pos);
  });

  if(!bounds.isEmpty()) map.fitBounds(bounds, 64);
  applyCategoryFilter();
}

function openInfo(marker, info){
  infoWindows.forEach(function(i){i.close();});
  info.open({map:map, anchor:marker});
}

function focusMarker(id){
  const rec = markers.find(function(m){return m.id===id;});
  if(!rec) return;
  map.panTo(rec.marker.getPosition());
  map.setZoom(14);
  activateListItem(id);
}

function activateListItem(id){
  document.querySelectorAll(".item").forEach(function(el){
    el.classList.toggle("active", el.dataset.id===id);
  });
}

function clearMarkers(){
  markers.forEach(function(m){m.marker.setMap(null);});
  markers = [];
  infoWindows = [];
}

function wireTopActions(){
  document.getElementById("categoryFilter").addEventListener("change", applyCategoryFilter);
  document.getElementById("fitAllBtn").addEventListener("click", function(){
    if(bounds && !bounds.isEmpty()) map.fitBounds(bounds, 64);
  });
}

function applyCategoryFilter(){
  const val = document.getElementById("categoryFilter").value;
  document.querySelectorAll(".item").forEach(function(el){
    const show = (val==="all") || (el.dataset.category===val);
    el.style.display = show ? "grid" : "none";
  });
  markers.forEach(function(rec){
    const show = (val==="all") || (rec.category===val);
    rec.marker.setVisible(show);
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

window.initMap = initMap; // 전역 등록 필수!
