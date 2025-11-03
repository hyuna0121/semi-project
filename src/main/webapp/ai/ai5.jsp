<%@ page contentType="text/html; charset=UTF-8" %>
<%
  request.setCharacterEncoding("UTF-8");
  String ints = request.getParameter("interests");
  if (ints != null) session.setAttribute("w_interests", ints);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>일정 템포</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
  <div class="step-wrap">
    <div class="step-head">
      <button class="step-back" onclick="location.href='ai4.jsp'">←</button>
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#2563eb" viewBox="0 0 24 24">
  	  <path d="M13.5 5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5zM8 22l1-5 2-2 1 3 3 4h2l-3.5-5.5 1-4.5 2 2V7l-3-1-2 2-2 1-2 5-2 8h2z"/>
	  </svg>

      <h2 class="step-title">선호하는 여행 일정은?</h2>
      <p class="step-sub">선택하신 스타일로 일정을 만들어드려요.</p>
      <div class="step-progress">5/5</div>
    </div>

    <form id="paceForm" action="result.jsp" method="post">
      <input type="hidden" name="pace" id="paceField">
      <div class="pill-group">
        <label class="pill"><input type="radio" name="paceOpt" value="fast"><span>빠릿한 일정</span></label>
        <label class="pill"><input type="radio" name="paceOpt" value="normal"><span>보통 페이스</span></label>
        <label class="pill"><input type="radio" name="paceOpt" value="slow"><span>넉넉한 일정</span></label>
      </div>
    </form>
  </div>

  <div class="footer-bar">
    <button id="nextBtn5" class="btn-next" type="button">다음</button>
  </div>

  <script>
    (function(){
      var form = document.getElementById('paceForm');
      var field = document.getElementById('paceField');
      var next  = document.getElementById('nextBtn5');
      var radios = document.querySelectorAll('input[name="paceOpt"]');

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
