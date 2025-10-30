<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<center>
	<h2>회원가입</h2>
	<form action="MemberJoinProc.jsp" method="post">
	<table width="600" border="1">
	<tr height="50">
		<td width="150" align="center">이름</td>
		<td width="350" align="center"> <input type="text" name="name" size="40" placeholder="이름을 입력하세요"></td>
	</tr>
	<tr height="50">
  	 <td width="150" align="center">아이디</td>
   	 <td width="350" align="center">
        <input type="text" id="id" name="id" size="25" placeholder="아이디를 입력하세요">
        <input type="button" value="중복확인" onclick="checkId()">
        <span id="idResult" style="margin-left:10px;"></span>
    </td>
	</tr>
	
	<script>
	function checkId() {
		const id = document.getElementById("id").value; 
		const resultSpan = document.getElementById("idResult");
		
		if(!id) {
			alert ("아이디를 입력해주세요!");
			return;			
		}
		
	    const pattern = /^[a-zA-Z0-9_\-@.]+$/;
	    if (!pattern.test(id)) {
	        alert("아이디는 영문자, 숫자, _, -, @, .만 사용할 수 있습니다!");
	        return;
	    }
		
		 fetch("IdCheck.jsp?id=" + encodeURIComponent(id))
	        .then(response => response.text())
	        .then(data => {
	            resultSpan.innerHTML = data; // 서버의 응답 표시
	        })
	        .catch(error => {
	            console.error("오류 발생:", error);
	        });
		
		
	}
	</script>
	<tr height="50">
  	 <td width="150" align="center">닉네임</td>
   	 <td width="350" align="center">
        <input type="text" id="nickname" name="nickname" size="25" placeholder="닉네임을 입력하세요">
        <input type="button" value="중복확인" onclick="checkNickname()">
        <span id="nickResult" style="margin-left:10px;"></span>
    </td>
	</tr>
	<script>
function checkNickname() {
    const nick = document.getElementById("nickname").value;
    const resultSpan = document.getElementById("nickResult");

    if (!nick) {
        alert("닉네임을 입력해주세요!");
        return;
    }

    // 서버에 AJAX 요청
    fetch("NickCheck.jsp?nickname=" + encodeURIComponent(nick))
        .then(response => response.text()) // 서버에서 받은 텍스트
        .then(data => {
            resultSpan.innerHTML = data; // <span>에 결과 출력
        })
        .catch(error => {
            console.error("오류 발생:", error);
        });
}
</script>

	<tr height="50">
		<td width="150" align="center">생년월일</td>
		<td width="350" align="center"> <input type="text" name="birth" size="40" placeholder="ex)19870908"></td>
	</tr>
	<tr height="50">
		<td width="150" align="center">패스워드</td>
		<td width="350" align="center"> <input type="password" name="pass1" size="40" placeholder="pw를 입력하세요"></td>	
	</tr>
	<tr height="50">
		<td width="150" align="center">패스워드 확인</td>
		<td width="350" align="center"> <input type="password" name="pass2" size="40" placeholder="pw를 다시 한 번 입력하세요"></td>
	</tr>	
	<tr height="50">
		<td width="150" align="center">주소</td>
		<td width="350" align="center"> <input type="text" name="address" size="40" placeholder="주소를 입력하세요"></td>
	</tr>	
	<tr height="50">
		<td width="150" align="center" colspan="2"> 
			<input type="submit" value="가입"> &nbsp;&nbsp;		
			<input type="reset" value="취소">
		</td>
	</tr>	
	</table>	
	</form>	
	</center>
</body>
</html>