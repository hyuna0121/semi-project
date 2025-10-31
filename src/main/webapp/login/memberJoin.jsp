<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>회원가입</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/memberjoin.css">
  <script src="${pageContext.request.contextPath}/js/memberjoin.js" defer></script>
</head>
<body>
<center>
  <div class="container">
    <h2>회원가입</h2>

    <!-- 필요 시 서블릿 사용: action='${pageContext.request.contextPath}/MemberJoin' -->
    <form id="joinForm" action="${pageContext.request.contextPath}/MemberJoinProc.jsp" method="post">
      <table>
        <tr>
          <td class="label">이름</td>
          <td><input type="text" name="name" placeholder="이름을 입력하세요"></td>
        </tr>

        <tr>
          <td class="label">아이디</td>
          <td>
            <div class="inline">
              <input type="text" id="id" name="id" placeholder="아이디를 입력하세요">
              <button type="button" class="btn" id="btnIdCheck">중복확인</button>
            </div>
            <span id="idResult" class="hint"></span>
          </td>
        </tr>

        <tr>
          <td class="label">닉네임</td>
          <td>
            <div class="inline">
              <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요">
              <button type="button" class="btn" id="btnNickCheck">중복확인</button>
            </div>
            <span id="nickResult" class="hint"></span>
          </td>
        </tr>

        <tr>
          <td class="label">생년월일</td>
          <td><input type="text" name="birth" placeholder="ex) 19870908"></td>
        </tr>

        <tr>
          <td class="label">패스워드</td>
          <td><input type="password" name="pass1" placeholder="pw를 입력하세요"></td>
        </tr>

        <tr>
          <td class="label">패스워드 확인</td>
          <td><input type="password" name="pass2" placeholder="pw를 다시 입력하세요"></td>
        </tr>

        <tr>
          <td class="label">주소</td>
          <td><input type="text" name="address" placeholder="주소를 입력하세요"></td>
        </tr>

        <tr>
          <td colspan="2" class="center">
            <button type="submit" class="btn primary">가입</button>
            <button type="reset" class="btn ghost">취소</button>
          </td>
        </tr>
      </table>
    </form>
  </div>
</center>

  <!-- JS에서 context-path 사용 -->
  <div id="appConfig" data-context-path="${pageContext.request.contextPath}"></div>
</body>
</html>
