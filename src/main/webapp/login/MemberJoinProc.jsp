<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.text.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입 처리</title>
</head>
<body>
<%
request.setCharacterEncoding("UTF-8");

// 폼 값 가져오기 (null 안전)
String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
String id = request.getParameter("id") != null ? request.getParameter("id").trim() : "";
String nickname = request.getParameter("nickname") != null ? request.getParameter("nickname").trim() : "";
String birth = request.getParameter("birth") != null ? request.getParameter("birth").trim() : "";
String pass1 = request.getParameter("pass1") != null ? request.getParameter("pass1").trim() : "";
String pass2 = request.getParameter("pass2") != null ? request.getParameter("pass2").trim() : "";
String address = request.getParameter("address") != null ? request.getParameter("address").trim() : "";

// 비밀번호 확인
if (!pass1.equals(pass2)) {
    out.println("<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>");
    return;
}

// 생년월일 처리
java.sql.Date birthDate = null;
if (!birth.isEmpty()) {
    if (birth.matches("\\d{8}")) {
        String formatted = birth.substring(0,4) + "-" + birth.substring(4,6) + "-" + birth.substring(6,8);
        birthDate = java.sql.Date.valueOf(formatted);
    } else {
        out.println("<script>alert('생년월일을 8자리 숫자로 입력해주세요.'); history.back();</script>");
        return;
    }
}

String url = "jdbc:mysql://localhost:3306/memberjoin";
String dbUser = "root";
String dbPass = "mysql1234";

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

    // 아이디 중복 확인
    PreparedStatement checkIdPstmt = conn.prepareStatement("SELECT COUNT(*) FROM member WHERE id=?");
    checkIdPstmt.setString(1, id);
    ResultSet rsId = checkIdPstmt.executeQuery();
    if (rsId.next() && rsId.getInt(1) > 0) {
        out.println("<script>alert('이미 존재하는 아이디입니다.'); history.back();</script>");
        rsId.close(); checkIdPstmt.close(); conn.close();
        return;
    }
    rsId.close(); checkIdPstmt.close();

    // 닉네임 중복 확인
    PreparedStatement checkNickPstmt = conn.prepareStatement("SELECT COUNT(*) FROM member WHERE nickname=?");
    checkNickPstmt.setString(1, nickname);
    ResultSet rsNick = checkNickPstmt.executeQuery();
    if (rsNick.next() && rsNick.getInt(1) > 0) {
        out.println("<script>alert('이미 존재하는 닉네임입니다.'); history.back();</script>");
        rsNick.close(); checkNickPstmt.close(); conn.close();
        return;
    }
    rsNick.close(); checkNickPstmt.close();

    // member 테이블 INSERT
    PreparedStatement memberPstmt = conn.prepareStatement(
        "INSERT INTO member(name, id, nickname, birth, password, address) VALUES (?, ?, ?, ?, ?, ?)"
    );
    memberPstmt.setString(1, name);
    memberPstmt.setString(2, id);
    memberPstmt.setString(3, nickname);
    memberPstmt.setDate(4, birthDate);
    memberPstmt.setString(5, pass1); // 실제 서비스에서는 해시 처리 필요
    memberPstmt.setString(6, address);
    int result = memberPstmt.executeUpdate();
    memberPstmt.close();

    // users 테이블 INSERT (로그인용)
    if (result > 0) {
        PreparedStatement usersPstmt = conn.prepareStatement(
            "INSERT INTO users(id, name, password) VALUES (?, ?, ?)"
        );
        usersPstmt.setString(1, id);
        usersPstmt.setString(2, name);
        usersPstmt.setString(3, pass1);
        usersPstmt.executeUpdate();
        usersPstmt.close();

        out.println("<script>alert('회원가입 성공!'); window.location.href='login.jsp';</script>");
    } else {
        out.println("<script>alert('회원가입 실패'); history.back();</script>");
    }

    conn.close();

} catch (Exception e) {
    e.printStackTrace();
    out.println("<script>alert('DB 오류 발생'); history.back();</script>");
}
%>
</body>
</html>