<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>여행일정공유</title>
  <link rel="stylesheet" href="css/mainpage.css" />
</head>
<body>
  <!-- 헤더 -->
  <%@ include file="../header.jsp" %>

  <section class="main">
    <div class="main__bg bg1 active"></div>
    <div class="main__bg bg2"></div>
    <div class="main__bg bg3"></div>
    
    <div class="main__overlay"></div>

    <div class="main__content">
      <h1 class="main__title">여행 일정을 만들고<br>공유하세요</h1>

      <form class="main__pill" action="<%=request.getContextPath()%>/schedules" method="get">
        <span class="main__text">당신만의 여행을 계획해보세요</span>
        <button class="main__cta" type="submit">일정 보러가기</button>
      </form>
    </div>
  </section>
  
  <!-- footer -->
  <%@ include file="../footer.jsp" %>
  
  <script src="js/mainpage.js"></script>
</body>
</html>
