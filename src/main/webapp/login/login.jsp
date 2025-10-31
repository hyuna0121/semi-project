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
	<form method="post" action="loginProc.jsp">
	<h2> 로그인 </h2>
		<table width="800">
			<tr>
				<td width="300" align="center"> 아이디(ID) </td>
				<td width="500"> <input type="text" name="id"> </td>
			</tr>
			<tr>
				<td width="300" align="center"> 비밀번호(PWD) </td>
				<td width="500"> <input type="password" name="pwd"> </td>
			</tr>
			<tr>
				<td colspan="2" align="center"> <button type="submit">로그인</button> </td>
			</tr>
		
		</table>
	</form>
</center>
</body>
</html>
