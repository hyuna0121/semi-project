// 상태
const state = {
  step: 0,
  city: null,
  days: null,
  companions: new Set(),
  interests: new Set(),
  pace: null,
  result: null
};

// 공통
const qs = function (s, el) { return (el || document).querySelector(s); };
const qsa = function (s, el) { return Array.prototype.slice.call((el || document).querySelectorAll(s)); };
const showStep = function (n) {
  state.step = n;
  qsa('[data-step]').forEach(function (sec) {
    var sn = parseInt(sec.dataset.step);
    sec.hidden = sn !== n;
    if (sn === 0 || sn === 6) sec.hidden = sn !== n;
    sec.classList.toggle('hidden', sn !== n);
  });
};

// 초기 바인딩
window.addEventListener('DOMContentLoaded', function () {
  // 0단계
  qsa('[data-step="0"] [data-next"]').forEach(function (b) {
    b.addEventListener('click', function () { showStep(1); });
  });

  // 1단계: 도시
  attachSingleSelect(1, '.chip[data-city]', function (v) { state.city = v; });

  // 2단계: 기간
  attachSingleSelect(2, '.chip[data-days]', function (v) {
    var active = qsa('.step[data-step="2"] .chip.active');
    if (active.length) state.days = parseInt(active[0].dataset.days);
  });

  // 3단계: 동행
  attachMultiSelect(3, '.chip[data-companion]', function (set) { state.companions = set; });

  // 4단계: 스타일
  attachMultiSelect(4, '.chip[data-interest]', function (set) { state.interests = set; });

  // 5단계: 페이스 + 제출
  attachSingleSelect(5, '.chip[data-pace]', function (v) { state.pace = v; });
  qs('#submitBtn').addEventListener('click', submitRecommend);

  // 다시 추천
  qsa('[data-restart]').forEach(function (b) {
    b.addEventListener('click', function () {
      Object.assign(state, { step: 0, city: null, days: null, companions: new Set(), interests: new Set(), pace: null, result: null });
      qsa('.chip.active').forEach(function (c) { c.classList.remove('active'); });
      showStep(0);
    });
  });

  // 시작
  showStep(0);
});

// 단일 선택
function attachSingleSelect(stepNo, selector, onChange) {
  var sec = qs('.step[data-step="' + stepNo + '"]');
  var chips = qsa(selector, sec);
  chips.forEach(function (ch) {
    ch.addEventListener('click', function () {
      chips.forEach(function (c) { c.classList.remove('active'); });
      ch.classList.add('active');
      qs('[data-next]', sec).disabled = false;
      onChange(ch.dataset.city || ch.dataset.days || ch.dataset.pace);
    });
  });
  qs('[data-prev]', sec).addEventListener('click', function () { showStep(stepNo - 1); });
  qs('[data-next]', sec).addEventListener('click', function () { showStep(stepNo + 1); });
}

// 다중 선택
function attachMultiSelect(stepNo, selector, onChange) {
  var sec = qs('.step[data-step="' + stepNo + '"]');
  var chips = qsa(selector, sec);
  var set = new Set();
  chips.forEach(function (ch) {
    ch.addEventListener('click', function () {
      ch.classList.toggle('active');
      var key = ch.dataset.companion || ch.dataset.interest;
      if (ch.classList.contains('active')) set.add(key); else set.delete(key);
      qs('[data-next]', sec).disabled = set.size === 0;
      onChange(new Set(set));
    });
  });
  qs('[data-prev]', sec).addEventListener('click', function () { showStep(stepNo - 1); });
  qs('[data-next]', sec).addEventListener('click', function () { showStep(stepNo + 1); });
}

// 제출 -> 추천 API 호출 -> 결과 렌더
function submitRecommend() {
  var ctx = window.__CTX__ || '';
  var interests = Array.from(state.interests).join(',');
  var companions = Array.from(state.companions).join(',');

  var params = new URLSearchParams({
    city: state.city || '',
    days: state.days || 2,
    pace: state.pace || 'normal',
    budget: 2,
    interests: interests
  });

  fetch(ctx + '/ai/recommend?' + params.toString())
    .then(function (res) {
      if (!res.ok) throw new Error('서버 오류');
      return res.json();
    })
    .then(function (data) {
      state.result = data;
      renderResult();
      showStep(6);
    })
    .catch(function (err) {
      alert('추천 중 오류 발생: ' + err.message);
    });
}

// 결과 렌더
function renderResult() {
  var r = state.result;
  var title = qs('#result-title');
  var sub = qs('#result-sub');
  title.textContent = toKCity(r.city) + ' · ' + state.days + '일 추천일정입니다.';
  sub.textContent = '관심사/평점 기반 자동 구성 · 식사/동선 고려';

  // 지도
  var mapEl = qs('#map');
  if (window.L) {
    mapEl.innerHTML = '';
    var map = L.map(mapEl).setView([37.5665, 126.9780], 12);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);

    var coords = [];
    if (r.days && r.days.length) {
      r.days[0].items.forEach(function (it, idx) {
        if (typeof it.lat === 'number' && typeof it.lon === 'number') {
          L.marker([it.lat, it.lon]).addTo(map).bindPopup((idx + 1) + '. ' + it.name);
          coords.push([it.lat, it.lon]);
        }
      });
      if (coords.length) map.fitBounds(coords, { padding: [20, 20] });
    }
  } else {
    mapEl.textContent = '지도를 불러오지 못했습니다.';
  }

  // 탭/패널
  var tabs = qs('#dayTabs');
  var panels = qs('#dayPanels');
  tabs.innerHTML = '';
  panels.innerHTML = '';

  r.days.forEach(function (d, i) {
    var btn = document.createElement('button');
    btn.textContent = 'Day ' + (i + 1);
    if (i === 0) btn.classList.add('active');
    btn.addEventListener('click', function () {
      qsa('.tabs button').forEach(function (b) { b.classList.remove('active'); });
      btn.classList.add('active');
      qsa('.day-panel').forEach(function (p, idx) {
        p.style.display = (idx === i) ? 'block' : 'none';
      });
    });
    tabs.appendChild(btn);

    var panel = document.createElement('div');
    panel.className = 'day-panel';
    if (i !== 0) panel.style.display = 'none';

    d.items.forEach(function (it, idx) {
      var row = document.createElement('div');
      row.className = 'day-item';
      var html = ''
        + '<div class="time">' + (it.time || '') + '</div>'
        + '<div class="meta">'
        + '<b>' + (idx + 1) + '. ' + it.name + '</b>'
        + '<span class="badge">' + (it.category || 'spot') + '</span>';
      if (it.note) html += '<div class="note">' + it.note + '</div>';
      html += '</div>';
      row.innerHTML = html;
      panel.appendChild(row);
    });

    panels.appendChild(panel);
  });
}

// 도시 코드 한글 변환
function toKCity(code) {
  var map = {
    seoul: '서울', busan: '부산', jeju: '제주', tokyo: '도쿄', osaka: '오사카', sapporo: '삿포로',
    nagoya: '나고야', okinawa: '오키나와', hongkong: '홍콩', shanghai: '상하이',
    beijing: '베이징', macau: '마카오', gyeongju: '경주', yeosu: '여수'
  };
  return map[code] || code;
}
