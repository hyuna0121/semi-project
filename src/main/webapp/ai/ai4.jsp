<%@ page contentType="text/html; charset=UTF-8" %>
<%
  request.setCharacterEncoding("UTF-8");
  String comps = request.getParameter("companions");
  if (comps != null) session.setAttribute("w_companions", comps);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>여행 스타일</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
  <div class="step-wrap">
    <div class="step-head">
      <button class="step-back" onclick="location.href='ai3.jsp'">←</button>
     <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#2563eb" viewBox="0 0 24 24">
  	 <path d="M20 5h-3.2l-1.6-2H8.8L7.2 5H4a2 2 0 0 0-2 2v11a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2zm-8 14a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-2.5a2.5 2.5 0 1 0 .001-5.001A2.5 2.5 0 0 0 12 16.5z"/>
	 </svg>

      <h2 class="step-title">내가 선호하는 여행 스타일은?</h2>
      <p class="step-sub">다중 선택이 가능해요.</p>
      <div class="step-progress">4/5</div>
    </div>

    <form id="intForm" action="ai5.jsp" method="post">
      <input type="hidden" name="interests" id="intField">
      <div class="pill-group">
        <label class="pill"><input type="checkbox" name="iOpt" value="activity"><span>체험·액티비티</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="hotplace"><span>SNS 핫플</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="nature"><span>자연과 함께</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="mustsee"><span>유명 관광지</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="healing"><span>여유롭게 힐링</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="culture"><span>문화·예술·역사</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="vibes"><span>감성 여행</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="shopping"><span>쇼핑</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="food"><span>먹방</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="cafe"><span>카페</span></label>
        <label class="pill"><input type="checkbox" name="iOpt" value="nightview"><span>야경</span></label>
      </div>
    </form>
  </div>

  <div class="footer-bar">
    <button id="nextBtn4" class="btn-next" type="button">다음</button>
  </div>

  <script>
    (function(){
      var form = document.getElementById('intForm');
      var field = document.getElementById('intField');
      var next  = document.getElementById('nextBtn4');
      var checks = document.querySelectorAll('input[name="iOpt"]');

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
