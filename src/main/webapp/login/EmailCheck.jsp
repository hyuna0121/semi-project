<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="util.DBUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>이메일 중복확인</title>
</head>
<body>
<%
    request.setCharacterEncoding("UTF-8");
    String email = request.getParameter("email");

    boolean exists = false; // 이메일 존재 여부

    if (email != null && !email.trim().isEmpty()) {
        String sql = "SELECT email FROM users WHERE email = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    if (exists) {
        out.print("<span style='color:red;'>이미 사용 중인 이메일입니다 ❌</span>");
    } else {
        out.print("<span style='color:green;'>사용 가능한 이메일입니다 ✅</span>");
    }
%>
</body>
</html>
