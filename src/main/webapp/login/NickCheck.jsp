<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.sql.*"%>
<%
request.setCharacterEncoding("UTF-8");
String nickname = request.getParameter("nickname");

String url = "jdbc:mysql://localhost:3306/memberjoin";
String user = "root";
String password = "test1234";
String sql = "SELECT nickname FROM member WHERE nickname = ?";

boolean exists = false;

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, nickname);
    ResultSet rs = pstmt.executeQuery();

    if (rs.next()) {
        exists = true;
    }

    rs.close();
    pstmt.close();
    conn.close();
} catch (Exception e) {
    e.printStackTrace();
}

if (exists) {
    out.print("<span style='color:red;'>이미 사용 중인 닉네임입니다 ❌</span>");
} else {
    out.print("<span style='color:green;'>사용 가능한 닉네임입니다 ✅</span>");
}
%>