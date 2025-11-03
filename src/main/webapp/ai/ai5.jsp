<%@ page contentType="text/html; charset=UTF-8" %>
<%
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 맞춤일정 5/5</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
<div class="screen">
  <div class="topbar">
    <a class="back" href="ai4.jsp?city=<%=request.getParameter("city")%>&days=<%=request.getParameter("days")%>">←</a>
    <div class="step">5/5</div>
  </div>

  <form action="<%=ctx%>/ai/finish" method="post">
    <input type="hidden" name="city" value="<%=request.getParameter("city")%>">
    <input type="hidden" name="days" value="<%=request.getParameter("days")%>">
    <% String[] withVals = request.getParameterValues("with");
       if(withVals!=null){ for(String w:withVals){ %>
      <input type="hidden" name="with" value="<%=w%>">
    <% }} 
       String[] styleVals = request.getParameterValues("style");
       if(styleVals!=null){ for(String s:styleVals){ %>
      <input type="hidden" name="style" value="<%=s%>">
    <% }} %>

    <div class="card">
      <div class="hero">
        <div class="icon">🧭</div>
        <h1>선호하는 여행 일정은?</h1>
        <div class="sub">선택해 주신 스타일로 일정을 만들어드려요.</div>
      </div>

      <div class="choices" id="tempoChoices">
        <label class="choice"><input type="radio" name="tempo" value="fast" required><span>빠듯한 일정 선호</span></label>
        <label class="choice"><input type="radio" name="tempo" value="normal"><span>보통</span></label>
        <label class="choice"><input type="radio" name="tempo" value="relaxed"><span>널널한 일정 선호</span></label>
      </div>
    </div>

    <div class="bottom">
      <button class="btn-primary" type="submit">추천 받기</button>
    </div>
  </form>
</div>

<script>
(function(){
  var wrap=document.getElementById('tempoChoices');
  wrap.addEventListener('change',function(e){
    if(e.target && e.target.name==='tempo'){
      wrap.querySelectorAll('.choice').forEach(function(c){c.classList.remove('selected')});
      e.target.closest('.choice').classList.add('selected');
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
