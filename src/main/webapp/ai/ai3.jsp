<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 맞춤일정 3/5</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
<div class="screen">
  <div class="topbar">
    <a class="back" href="ai2.jsp?city=<%=request.getParameter("city")%>">←</a>
    <div class="step">3/5</div>
  </div>

  <form action="ai4.jsp" method="get">
    <input type="hidden" name="city" value="<%=request.getParameter("city")%>">
    <input type="hidden" name="days" value="<%=request.getParameter("days")%>">

    <div class="card">
      <div class="hero">
        <div class="icon">😎</div>
        <h1>누구와 떠나나요?</h1>
        <div class="sub">다중 선택이 가능해요.</div>
      </div>

      <div class="choices" id="withChoices">
        <label class="choice"><input type="checkbox" name="with" value="solo"><span>혼자</span></label>
        <label class="choice"><input type="checkbox" name="with" value="friends"><span>친구와</span></label>
        <label class="choice"><input type="checkbox" name="with" value="lover"><span>연인과</span></label>
        <label class="choice"><input type="checkbox" name="with" value="kids"><span>아이와</span></label>
        <label class="choice"><input type="checkbox" name="with" value="parents"><span>부모님과</span></label>
        <label class="choice"><input type="checkbox" name="with" value="other"><span>기타</span></label>
      </div>
    </div>

    <div class="bottom">
      <button class="btn-primary" type="submit">다음</button>
    </div>
  </form>
</div>

<script>
(function(){
  var wrap=document.getElementById('withChoices');
  wrap.addEventListener('change',function(e){
    if(e.target && e.target.name==='with'){
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
