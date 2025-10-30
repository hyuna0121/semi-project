<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인 처리</title>
</head>
<body>
<%
request.setCharacterEncoding("UTF-8");

String id = request.getParameter("id") != null ? request.getParameter("id").trim() : "";
String pwd = request.getParameter("pwd") != null ? request.getParameter("pwd").trim() : "";

if(id.isEmpty() || pwd.isEmpty()) {
    out.println("<script>alert('아이디와 비밀번호를 입력해주세요.'); window.location.href='login.jsp';</script>");
    return;
}

String url = "jdbc:mysql://localhost:3306/memberjoin";
String dbUser = "root";
String dbPass = "test1234";

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

    PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM users WHERE id=? AND password=?");
    pstmt.setString(1, id);
    pstmt.setString(2, pwd);
    ResultSet rs = pstmt.executeQuery();

    if(rs.next()) {
        // 로그인 성공
        String name = rs.getString("name");
        session.setAttribute("loginId", id);
        session.setAttribute("loginName", name);
        rs.close();
        pstmt.close();
        conn.close();
        response.sendRedirect("MainPage.jsp");
    } else {
        // 로그인 실패
        rs.close();
        pstmt.close();
        conn.close();
        out.println("<script>alert('로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.'); window.location.href='login.jsp';</script>");
    }

} catch(Exception e) {
    e.printStackTrace();
    out.println("<script>alert('DB 오류 발생'); window.location.href='login.jsp';</script>");
}
%>
</body>
</html>
