<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>로그인</title>
  <!-- 외부 CSS 연결 -->
  <link rel="stylesheet" href="css/login.css" />
  <!-- 웹폰트(선택) -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
  <div class="login-wrap">
    <div class="brand">
      <img class="logo" src="image/logo.png" alt="서비스 로고">
    </div>
    
  <form class="card" method="post" action="loginProc.jsp">
    <!-- 아이디 / 비밀번호 -->
    <div class="field">
      <input class="input" type="text" id="id" name="id" placeholder="아이디 입력" required>
    </div>
    <div class="field">
      <input class="input" type="password" id="pwd" name="pwd" placeholder="비밀번호 입력" required>
    </div>

    <!-- 아이디 저장 -->
    <label class="remember">
      <input type="checkbox" id="rememberId" name="rememberId" value="true">
      <span>아이디 저장</span>
    </label>

    <!-- 로그인 버튼 -->
    <button class="btn" type="submit">로그인</button>

    <!-- 하단 링크 -->
    <div class="links">
      <a href="findId.jsp">아이디 찾기</a>
      <span>|</span>
      <a href="findPwd.jsp">비밀번호 찾기</a>
    </div>
   </form>
  </div>
  
  <!-- 아이디 기억하기 -->
  <script>
  	window.onload = function() {
  		let savedId = getCookie("savedUserId");
  		
  		if (savedId !== "") {
  			document.getElementById("id").value = savedId;
  			document.getElementById("rememberId").checked = true;
  		}
  	};
  	
  	function getCookie(name) {
  		let value = "; " + document.cookie;
  		let parts = value.split("; " + name + "=");
  		if (parts.length === 2) {
  			return parts.pop().split(";").shift();
  		}
  		return "";
  	}
  </script>
</body>
</html>
