<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="util.DBUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>닉네임 중복확인</title>
</head>
<body>
<%
    request.setCharacterEncoding("UTF-8");
    String nickname = request.getParameter("nickname");
    boolean exists = false;

    if (nickname != null && !nickname.trim().isEmpty()) {
        String sql = "SELECT nickname FROM users WHERE nickname = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname.trim());

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
        out.print("<span style='color:red;'>이미 사용 중인 닉네임입니다 ❌</span>");
    } else {
        out.print("<span style='color:green;'>사용 가능한 닉네임입니다 ✅</span>");
    }
%>
</body>
</html>
