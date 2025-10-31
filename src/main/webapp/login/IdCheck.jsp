<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="util.DBUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>아이디 중복확인</title>
</head>
<body>
<%
    request.setCharacterEncoding("UTF-8");
    String id = request.getParameter("id");

    boolean exists = false; // 아이디 존재 여부

    if (id != null && !id.trim().isEmpty()) {
        String sql = "SELECT id FROM users WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id.trim());
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
        out.print("<span style='color:red;'>이미 사용 중인 아이디입니다 ❌</span>");
    } else {
        out.print("<span style='color:green;'>사용 가능한 아이디입니다 ✅</span>");
    }
%>
</body>
</html>
