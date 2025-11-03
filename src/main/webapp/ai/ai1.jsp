<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 맞춤일정 1/5</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
<div class="screen">
  <div class="topbar">
    <div class="back" style="visibility:hidden">←</div>
    <div class="step">1/5</div>
  </div>

  <form action="ai2.jsp" method="get">
    <div class="card">
      <div class="hero">
        <div class="icon">🗺️</div>
        <h1>떠나고 싶은 도시는?</h1>
        <div class="sub">도시 1곳을 선택해 주세요.</div>
      </div>

      <div class="choices" id="cityChoices">
        <label class="choice"><input type="radio" name="city" value="seoul" required><span>서울</span></label>
        <label class="choice"><input type="radio" name="city" value="busan"><span>부산</span></label>
        <label class="choice"><input type="radio" name="city" value="jeju"><span>제주</span></label>
        <label class="choice"><input type="radio" name="city" value="tokyo"><span>도쿄</span></label>
        <label class="choice"><input type="radio" name="city" value="osaka"><span>오사카</span></label>
        <label class="choice"><input type="radio" name="city" value="sapporo"><span>삿포로</span></label>
      </div>
    </div>

    <div class="bottom">
      <button class="btn-primary" type="submit">다음</button>
    </div>
  </form>
</div>

<script>
(function(){
  var wrap=document.getElementById('cityChoices');
  wrap.addEventListener('change',function(e){
    if(e.target && e.target.name==='city'){
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
