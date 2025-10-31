<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>회원가입</title>
  <link rel="stylesheet" href="css/memberjoin.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <form class="card" action="MemberJoinProc.jsp" method="post" autocomplete="off">
      <h2 class="title">회원가입</h2>

      <!-- 이름 -->
      <label class="row">
        <span class="label">이름</span>
        <input class="input" type="text" name="name" placeholder="이름을 입력하세요" required>
      </label>

      <!-- 아이디 + 중복확인 -->
      <div class="row">
        <span class="label">아이디</span>
        <div class="input-group">
          <input class="input" type="text" id="id" name="id" placeholder="아이디를 입력하세요" required>
          <button class="subbtn" type="button" onclick="checkId()">중복확인</button>
        </div>
        <span id="idResult" class="hint"></span>
      </div>

      <!-- 닉네임 + 중복확인 -->
      <div class="row">
        <span class="label">닉네임</span>
        <div class="input-group">
          <input class="input" type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요" required>
          <button class="subbtn" type="button" onclick="checkNickname()">중복확인</button>
        </div>
        <span id="nickResult" class="hint"></span>
      </div>

      <!-- 생년월일 -->
	  <label class="row">
	  	<span class="label">생년월일</span>
	  	<input class="input" type="text" name="birth"
	         placeholder="예) 19870908"
	         pattern="\d{8}" inputmode="numeric" maxlength="8"
	         title="생년월일 8자리(YYYYMMDD)를 입력하세요">
 	  </label>


      <!-- 비밀번호 -->
      <label class="row">
        <span class="label">비밀번호</span>
        <input class="input" type="password" name="pass1" placeholder="비밀번호를 입력하세요" required>
      </label>

      <!-- 비밀번호 확인 -->
      <label class="row">
        <span class="label">비밀번호 확인</span>
        <input class="input" type="password" name="pass2" placeholder="비밀번호를 다시 입력하세요" required>
      </label>

      <!-- 주소 -->
      <label class="row">
        <span class="label">주소</span>
        <input class="input" type="text" name="address" placeholder="주소를 입력하세요" required>
      </label>

      <!-- 버튼들 -->
      <div class="actions">
        <button class="btn" type="submit">가입</button>
        <button class="btn-outline" type="reset">취소</button>
      </div>

      <div class="links">
        <a href="login.jsp">로그인으로 돌아가기</a>
      </div>
    </form>
  </div>

  <script>
    function checkId() {
      const id = document.getElementById("id").value.trim();
      const resultSpan = document.getElementById("idResult");
      if (!id) { alert("아이디를 입력해주세요!"); return; }
      const pattern = /^[a-zA-Z0-9_\-@.]+$/;
      if (!pattern.test(id)) { alert("아이디는 영문/숫자/_, -, @, .만 가능합니다!"); return; }

      fetch("IdCheck.jsp?id=" + encodeURIComponent(id))
        .then(r => r.text())
        .then(t => resultSpan.innerHTML = t)
        .catch(e => console.error(e));
    }

    function checkNickname() {
      const nick = document.getElementById("nickname").value.trim();
      const resultSpan = document.getElementById("nickResult");
      if (!nick) { alert("닉네임을 입력해주세요!"); return; }

      fetch("NickCheck.jsp?nickname=" + encodeURIComponent(nick))
        .then(r => r.text())
        .then(t => resultSpan.innerHTML = t)
        .catch(e => console.error(e));
    }
  </script>
</body>
</html>
