// 공개/비공개 토글 (값은 항상 value="N", 존재 유무로 판단)
(function () {
  const cb = document.getElementById('visibility');
  const icon = document.getElementById('visibilityIcon');
  const text = document.getElementById('visibilityText');
  if (!cb) return;

  const sync = () => {
    if (cb.checked) {          // 비공개 (파라미터 존재)
      icon.textContent = 'lock';
      text.textContent = '비공개';
    } else {                   // 공개 (파라미터 없음)
      icon.textContent = 'lock_open_right';
      text.textContent = '공개';
    }
  };
  cb.addEventListener('change', sync);
  sync(); // 초기 표시
})();

// 날짜 범위 선택기 (서블릿 호환: name="demo" 에 "YYYY-MM-DD ~ YYYY-MM-DD")
$(function () {
  const $view = $('#demoView');      // 화면에 보이는 입력
  const $demo = $('#demoHidden');    // 실제 전송(name="demo")
  const $start = $('#startDate');    // 옵션
  const $end = $('#endDate');        // 옵션
  if (!$view.length) return;

  const today = moment();

  $view.daterangepicker({
    locale: {
      format: 'YYYY-MM-DD',
      separator: ' ~ ',
      applyLabel: '확인',
      cancelLabel: '취소',
      daysOfWeek: ['일','월','화','수','목','금','토'],
      monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월']
    },
    startDate: today,
    endDate: today,
    autoUpdateInput: true
  }, function (start, end) {
    const pretty = start.format('YYYY-MM-DD') + ' ~ ' + end.format('YYYY-MM-DD');
    $view.val(pretty);
    $demo.val(pretty);               // ✅ 서블릿이 읽는 값
    $start.val(start.format('YYYY-MM-DD'));
    $end.val(end.format('YYYY-MM-DD'));
  });

  // 초기값 셋팅
  const initPretty = today.format('YYYY-MM-DD') + ' ~ ' + today.format('YYYY-MM-DD');
  $view.val(initPretty);
  $demo.val(initPretty);
  $start.val(today.format('YYYY-MM-DD'));
  $end.val(today.format('YYYY-MM-DD'));
});

// ===== 동행인 추가 (최종 통합) =====
(function () {
  var ctx   = window.CTX || "";
  var form  = document.querySelector('form[action$="/processAddSchedule"]') || document.querySelector("form");
  var input = document.getElementById("companionInput");
  var addBtn= document.getElementById("companionAddBtn");
  var list  = document.getElementById("companionList");
  var msg   = document.getElementById("companionMsg");

  if (!form || !input || !addBtn || !list) return;

  function showMsg(text){
    if (!msg) return;
    msg.textContent = text || "";
    msg.style.display = text && text.length > 0 ? "block" : "none";
  }

  function alreadyAdded(userId){
    return !!document.querySelector('[data-companion-id="' + userId + '"]');
  }

  function toJsonSafe(res){
    var ct = res.headers.get("content-type") || "";
    if (!res.ok) throw new Error("HTTP " + res.status);
    if (ct.indexOf("application/json") === -1) throw new Error("NOT_JSON");
    return res.json();
  }

  function addItem(user){
    var userId = user.id;

    // ---- 서버 전송용 hidden: 반드시 폼에 붙인다 (가장 중요) ----
    var hidden = document.createElement("input");
    hidden.type  = "hidden";
    hidden.name  = "companions[]";   // ★ 서버에서 getParameterValues("companions[]") 로 받음
    hidden.value = userId;           // ★ 반드시 users.id (닉네임 X)
    form.appendChild(hidden);

    // ---- 화면 표시용 아이템 ----
    var li = document.createElement("li");
    li.className = "companion-item";
    li.setAttribute("data-companion-id", userId);

    var img = document.createElement("img");
    var url = user.profileUrl || (ctx + "/mypage/image/default_profile.png");
    img.src = url + "?v=" + Date.now(); // 캐시 방지
    img.className = "avatar";
    img.alt = "프로필";

    var meta = document.createElement("div");
    meta.className = "meta";
    var strong = document.createElement("strong");
    strong.textContent = user.nickname || userId;
    var span = document.createElement("span");
    span.textContent = (user.name ? user.name + " · " : "") + userId;
    meta.appendChild(strong);
    meta.appendChild(span);

    var removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.className = "remove";
    removeBtn.textContent = "삭제";
    removeBtn.addEventListener("click", function () {
      // 폼에 붙인 해당 hidden도 같이 제거
      var hiddens = form.querySelectorAll('input[name="companions[]"][value="' + userId + '"]');
      hiddens.forEach(function(h){ h.remove(); });
      li.remove();
    });

    li.appendChild(img);
    li.appendChild(meta);
    li.appendChild(removeBtn);
    list.appendChild(li);

    // 디버그: 현재 폼에 들어간 companions[] 출력
    var all = Array.from(form.querySelectorAll('input[name="companions[]"]')).map(function(x){return x.value;});
    console.log("companions now:", all);
  }

  function findFriend(q){
    fetch(ctx + "/friend/find?q=" + encodeURIComponent(q), { method: "GET" })
      .then(toJsonSafe)
      .then(function(data){
        if (!data || !data.found){ showMsg("해당 사용자를 찾을 수 없습니다."); return; }
        var u = data.user;
        if (alreadyAdded(u.id)){ showMsg("이미 추가된 동행인입니다."); return; }
        addItem(u);
        showMsg("");
        input.value = "";
        input.focus();
      })
      .catch(function(err){
        console.error("friend/find error:", err);
        showMsg("조회 중 오류가 발생했습니다.");
      });
  }

  function onAddClick(){
    var q = (input.value || "").trim();
    if (!q){ showMsg("아이디 또는 닉네임을 입력하세요."); input.focus(); return; }
    findFriend(q);
  }

  // 엔터로 폼 제출되는 것 방지 + 추가 실행
  input.addEventListener("keydown", function(e){
    if (e.key === "Enter"){
      e.preventDefault();
      onAddClick();
    }
  });
  addBtn.addEventListener("click", onAddClick);
})();
