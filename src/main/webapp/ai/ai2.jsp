<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>AI 맞춤일정 2/5</title>
  <link rel="stylesheet" href="css/ai.css">
</head>
<body>
<div class="screen">

  <div class="topbar">
    <a class="back" href="ai1.jsp" aria-label="뒤로">←</a>
    <div class="step">2/5</div>
  </div>

  <form action="ai3.jsp" method="get">
    <input type="hidden" name="city" value="<%=request.getParameter("city")%>">

    <div class="card">
      <div class="hero">
        <div class="icon">📅</div>
        <h1>여행 기간은?</h1>
        <div class="sub">원하는 기간을 선택해 주세요.</div>
      </div>

      <div class="choices" id="periodChoices">
        <label class="choice"><input type="radio" name="days" value="1" required><span>당일치기</span></label>
        <label class="choice"><input type="radio" name="days" value="2"><span>1박 2일</span></label>
        <label class="choice"><input type="radio" name="days" value="3"><span>2박 3일</span></label>
        <label class="choice"><input type="radio" name="days" value="4"><span>3박 4일</span></label>
        <label class="choice"><input type="radio" name="days" value="5"><span>4박 5일</span></label>
        <label class="choice"><input type="radio" name="days" value="6"><span>5박 6일</span></label>
      </div>
    </div>

    <div class="bottom">
      <button class="btn-primary" type="submit">다음</button>
    </div>
  </form>
</div>

<script>
  // 선택 시 pill에 .selected 토글
  (function(){
    var wrap = document.getElementById('periodChoices');
    wrap.addEventListener('change', function(e){
      if(e.target && e.target.name==='days'){
        var labels = wrap.querySelectorAll('.choice');
        for(var i=0;i<labels.length;i++) labels[i].classList.remove('selected');
        e.target.closest('.choice').classList.add('selected');
      }
    });
    // 처음 클릭 효과
    var choices = wrap.querySelectorAll('.choice');
    for(var i=0;i<choices.length;i++){
      choices[i].addEventListener('click', function(){ this.classList.add('active'); });
      choices[i].addEventListener('mouseleave', function(){ this.classList.remove('active'); });
    }
  })();
</script>
</body>
</html>
