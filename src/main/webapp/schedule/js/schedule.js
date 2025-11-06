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
    const pretty = `${start.format('YYYY-MM-DD')} ~ ${end.format('YYYY-MM-DD')}`;
    $view.val(pretty);
    $demo.val(pretty);               // ✅ 서블릿이 읽는 값
    $start.val(start.format('YYYY-MM-DD'));
    $end.val(end.format('YYYY-MM-DD'));
  });

  // 초기값 셋팅
  const initPretty = `${today.format('YYYY-MM-DD')} ~ ${today.format('YYYY-MM-DD')}`;
  $view.val(initPretty);
  $demo.val(initPretty);
  $start.val(today.format('YYYY-MM-DD'));
  $end.val(today.format('YYYY-MM-DD'));
});
// ===== 동행인 추가 기능 =====
document.addEventListener("DOMContentLoaded", function() {
  const addBtn = document.getElementById("addBuddyBtn");
  const buddyList = document.getElementById("buddyList");

  if (!addBtn || !buddyList) return;

  addBtn.addEventListener("click", function() {
    const wrapper = document.createElement("div");
    wrapper.className = "buddy-item mb-2 d-flex gap-2 align-items-center";

    const input = document.createElement("input");
    input.type = "text";
    input.name = "travelBuddies";   // 서블릿에서 배열로 받음
    input.placeholder = "동행인 이름 또는 아이디 입력";
    input.className = "form-control soft-input flex-grow-1";

    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.textContent = "삭제";
    removeBtn.className = "btn btn-outline-danger btn-sm";
    removeBtn.addEventListener("click", () => wrapper.remove());

    wrapper.appendChild(input);
    wrapper.appendChild(removeBtn);
    buddyList.appendChild(wrapper);
  });
});
