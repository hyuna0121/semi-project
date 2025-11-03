<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 맞춤일정 4/5</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
<div class="screen">
  <div class="topbar">
    <a class="back" href="ai3.jsp?city=<%=request.getParameter("city")%>&days=<%=request.getParameter("days")%>">←</a>
    <div class="step">4/5</div>
  </div>

  <form action="ai5.jsp" method="get">
    <input type="hidden" name="city" value="<%=request.getParameter("city")%>">
    <input type="hidden" name="days" value="<%=request.getParameter("days")%>">
    <% String[] withVals = request.getParameterValues("with");
       if(withVals!=null){ for(String w:withVals){ %>
      <input type="hidden" name="with" value="<%=w%>">
    <% }} %>

    <div class="card">
      <div class="hero">
        <div class="icon">📸</div>
        <h1>내가 선호하는 여행 스타일은?</h1>
        <div class="sub">다중 선택이 가능해요.</div>
      </div>

      <div class="choices" id="styleChoices">
        <label class="choice"><input type="checkbox" name="style" value="activity"><span>체험·액티비티</span></label>
        <label class="choice"><input type="checkbox" name="style" value="sns"><span>SNS 핫플레이스</span></label>
        <label class="choice"><input type="checkbox" name="style" value="nature"><span>자연과 함께</span></label>
        <label class="choice"><input type="checkbox" name="style" value="culture"><span>문화·역사</span></label>
        <label class="choice"><input type="checkbox" name="style" value="healing"><span>여유롭게 힐링</span></label>
        <label class="choice"><input type="checkbox" name="style" value="shopping"><span>쇼핑 위주</span></label>
        <label class="choice"><input type="checkbox" name="style" value="foodie"><span>관광보다 먹방</span></label>
      </div>
    </div>

    <div class="bottom">
      <button class="btn-primary" type="submit">다음</button>
    </div>
  </form>
</div>

<script>
(function(){
  var wrap=document.getElementById('styleChoices');
  wrap.addEventListener('change',function(e){
    if(e.target && e.target.name==='style'){
      e.target.closest('.choice').classList.toggle('selected', e.target.checked);
    }
  });
  wrap.querySelectorAll('.choice').forEach(function(c){
    c.addEventListener('pointerdown',()=>c.classList.add('active'));
    c.addEventListener('pointerleave',()=>c.classList.remove('active'));
  });
})();
</script>
</body>
</html>
