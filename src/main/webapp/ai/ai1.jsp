<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>도시 선택</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
  <div class="step-wrap">
    <div class="step-head">
      <button class="step-back" onclick="location.href='ai0.jsp'">←</button>
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#2563eb" viewBox="0 0 24 24">
	  <path d="M12 2a10 10 0 1 0 .001 20.001A10 10 0 0 0 12 2zm-1 17.93A8.001 8.001 0 0 1 4.07 13H7v-2H4.07A8.001 8.001 0 0 1 11 4.07V7h2V4.07A8.001 8.001 0 0 1 19.93 11H17v2h2.93A8.001 8.001 0 0 1 13 19.93V17h-2v2.93z"/>
	  </svg>

      <h2 class="step-title">떠나고 싶은 도시는?</h2>
      <p class="step-sub">도시 1곳을 선택해주세요.</p>
      <div class="step-progress">1/5</div>
    </div>

    <form id="cityForm" action="ai2.jsp" method="post">
      <input type="hidden" name="city" id="cityField">
      <div class="pill-group">
        <label class="pill"><input type="radio" name="cityOpt" value="seoul"><span>서울</span></label>
        <label class="pill"><input type="radio" name="cityOpt" value="busan"><span>부산</span></label>
        <label class="pill"><input type="radio" name="cityOpt" value="jeju"><span>제주</span></label>
        <label class="pill"><input type="radio" name="cityOpt" value="gyeongju"><span>경주</span></label>
        <label class="pill"><input type="radio" name="cityOpt" value="tokyo"><span>도쿄</span></label>
        <label class="pill"><input type="radio" name="cityOpt" value="osaka"><span>오사카</span></label>
      </div>
    </form>
  </div>

  <div class="footer-bar">
    <button id="nextBtn1" class="btn-next" type="button">다음</button>
  </div>

  <script>
    (function(){
      var form = document.getElementById('cityForm');
      var field = document.getElementById('cityField');
      var next  = document.getElementById('nextBtn1');
      var radios = document.querySelectorAll('input[name="cityOpt"]');

      radios.forEach(function(r){
        r.addEventListener('change', function(){
          field.value = r.value;
          next.classList.add('enabled');
          next.disabled = false;
        });
      });
      next.addEventListener('click', function(){
        if (!field.value) return;
        form.submit();
      });
    })();
  </script>
</body>
</html>
