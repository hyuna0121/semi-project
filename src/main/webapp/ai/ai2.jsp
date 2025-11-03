<%@ page contentType="text/html; charset=UTF-8" %>
<%
  request.setCharacterEncoding("UTF-8");
  String city = request.getParameter("city");
  if (city != null && !city.isEmpty()) session.setAttribute("w_city", city);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>여행 기간 선택</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
  <div class="step-wrap">
    <div class="step-head">
      <button class="step-back" onclick="location.href='ai1.jsp'">←</button>
     <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#2563eb" viewBox="0 0 24 24">
  	 <path d="M7 2h2v2h6V2h2v2h3a1 1 0 0 1 1 1v15a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a1 1 0 0 1 1-1h3V2zm13 6H4v12a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V8zM6 10h4v4H6v-4z"/>
	 </svg>

      <h2 class="step-title">여행 기간은?</h2>
      <p class="step-sub">원하는 기간을 선택해 주세요.</p>
      <div class="step-progress">2/5</div>
    </div>

    <form id="daysForm" action="ai3.jsp" method="post">
      <input type="hidden" name="days" id="daysField">
      <div class="pill-group">
        <label class="pill"><input type="radio" name="daysOpt" value="1"><span>당일치기</span></label>
        <label class="pill"><input type="radio" name="daysOpt" value="2"><span>1박 2일</span></label>
        <label class="pill"><input type="radio" name="daysOpt" value="3"><span>2박 3일</span></label>
        <label class="pill"><input type="radio" name="daysOpt" value="4"><span>3박 4일</span></label>
        <label class="pill"><input type="radio" name="daysOpt" value="5"><span>4박 5일</span></label>
        <label class="pill"><input type="radio" name="daysOpt" value="6"><span>5박 6일</span></label>
      </div>
    </form>
  </div>

  <div class="footer-bar">
    <button id="nextBtn2" class="btn-next" type="button">다음</button>
  </div>

  <script>
    (function(){
      var form = document.getElementById('daysForm');
      var field = document.getElementById('daysField');
      var next  = document.getElementById('nextBtn2');
      var radios = document.querySelectorAll('input[name="daysOpt"]');

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
