<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>로그인</title>
  <!-- 외부 CSS 연결 -->
  <link rel="stylesheet" href="./css/login.css" />
  <!-- 웹폰트(선택) -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet" />
  
  <script>
  	const contextPath = "<%= request.getContextPath() %>";
  </script>
</head>
<body>
  <div class="login-wrap">
    <div class="brand">
      <img class="logo" src="image/logo.png" alt="서비스 로고" onclick="showMain()">
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
      <a href="#" id="openFindIdModal">아이디 찾기</a>
      <span>|</span>
      <a href="#" id="openResetPwModal">비밀번호 재설정하기</a>
      <span>|</span>
      <a href="./memberJoin.jsp">회원가입</a>
    </div>
   </form>
  </div>
  
  <div class="findIdModal">
  	<div class="content">
  		<div class="title">
			<h2>아이디 찾기</h2> 
  		</div>
  		<div>
		  	<form id="findIdForm" action="">
			    <div class="field">
			      <span class="label">이메일</span>
			      <input class="input" type="text" id="email" name="email" placeholder="이메일 입력" required>
			    </div>
			    
			    <div class="field">
			      <span class="label">이름</span>
			      <input class="input" type="text" id="name" name="name" placeholder="이름 입력" required>
			    </div>
			    
			    <div class="btn-wrap">
   				  <button type="reset" class="closeModalBtn">취소하기</button> 
			      <button type="submit" class="findIdBtn">아이디 찾기</button>
			    </div>
		  	</form>
  		</div>
	</div>
  </div>
  
  <div class="resetPwModal">
  	<div class="content">
  		<div class="title">
			<h2>비밀번호 재설정하기</h2> 
  		</div>
  		<div>
		  	<form id="resetPwForm" action="">
			    <div class="field">
			      <span class="label">아이디</span>
			      <input class="input" type="text" id="resetId" name="resetId" placeholder="아이디 입력" required>
			    </div>
			    
			    <div class="field">
			      <span class="label">이메일</span>
			      <input class="input" type="text" id="resetEmail" name="resetEmail" placeholder="이메일 입력" required>
			    </div>
			    
			    <div class="btn-wrap">
   				  <button type="reset" class="closeResetModalBtn">취소하기</button> 
			      <button type="submit" class="findIdBtn">재설정하기</button>
			    </div>
		  	</form>
  		</div>
	</div>
  </div>
  
  <script src="./js/login.js"></script>
  <script type="text/javascript">
  	const logo = document.querySelector('.logo');
  	function showMain() {
		const ctx = '<%= request.getContextPath() %>';
	    location.href = ctx + '/mainpage/mainpage.jsp';
	}
  	logo.addEventListener('click', showMain);
  </script>
</body>
</html>
