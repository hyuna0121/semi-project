<%@ page contentType="text/html; charset=UTF-8" %>
<%
  // 초기화가 필요하면 아래 주석 해제
  // session.invalidate();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AI 맞춤일정</title>
<link rel="stylesheet" href="css/ai.css">
</head>
	<body style="display:grid;place-items:center;height:80vh;font-family:sans-serif">
	  <div style="text-align:center">
	    <h1>취향에 맞게 일정을 추천해 드려요!</h1>
	    <p>순식간에 여행 준비 끝</p>
	    <form action="ai1.jsp" method="get">
	      <button type="submit" style="padding:12px 20px;font-weight:700;">바로 추천받기</button>
	    </form>
	  </div>
	</body>
</html>
