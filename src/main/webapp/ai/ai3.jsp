<%@ page contentType="text/html; charset=UTF-8" %>
<%
  request.setCharacterEncoding("UTF-8");
  String days = request.getParameter("days");
  if (days != null && !days.isEmpty()) session.setAttribute("w_days", days);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>동행 선택</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
  <div class="step-wrap">
    <div class="step-head">
      <button class="step-back" onclick="location.href='ai2.jsp'">←</button>
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#2563eb" viewBox="0 0 24 24">
  	  <path d="M12 2a10 10 0 1 0 .001 20.001A10 10 0 0 0 12 2zm-4 7a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3zm8 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3zM7.5 14a5.5 5.5 0 0 0 9 0h-9z"/>
	  </svg>

      <h2 class="step-title">누구와 떠나나요?</h2>
      <p class="step-sub">다중 선택이 가능해요.</p>
      <div class="step-progress">3/5</div>
    </div>

    <form id="compForm" action="ai4.jsp" method="post">
      <input type="hidden" name="companions" id="compField">
      <div class="pill-group">
        <label class="pill"><input type="checkbox" name="cOpt" value="solo"><span>혼자</span></label>
        <label class="pill"><input type="checkbox" name="cOpt" value="friends"><span>친구와</span></label>
        <label class="pill"><input type="checkbox" name="cOpt" value="couple"><span>연인과</span></label>
        <label class="pill"><input type="checkbox" name="cOpt" value="kids"><span>아이와</span></label>
        <label class="pill"><input type="checkbox" name="cOpt" value="parents"><span>부모님과</span></label>
        <label class="pill"><input type="checkbox" name="cOpt" value="others"><span>기타</span></label>
      </div>
    </form>
  </div>

  <div class="footer-bar">
    <button id="nextBtn3" class="btn-next" type="button">다음</button>
  </div>

  <script>
    (function(){
      var form = document.getElementById('compForm');
      var field = document.getElementById('compField');
      var next  = document.getElementById('nextBtn3');
      var checks = document.querySelectorAll('input[name="cOpt"]');

      function update(){
        var picked = [];
        checks.forEach(function(c){ if (c.checked) picked.push(c.value); });
        field.value = picked.join(',');
        if (picked.length > 0){
          next.classList.add('enabled'); next.disabled = false;
        } else {
          next.classList.remove('enabled'); next.disabled = true;
        }
      }
      checks.forEach(function(c){ c.addEventListener('change', update); });
      update();

      next.addEventListener('click', function(){
        if (!field.value) return;
        form.submit();
      });
    })();
  </script>
</body>
</html>
