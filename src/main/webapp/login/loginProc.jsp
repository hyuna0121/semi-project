<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="util.DBUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인 처리</title>
</head>
<body>
<%
	request.setCharacterEncoding("UTF-8");
	
	String id  = request.getParameter("id")  != null ? request.getParameter("id").trim()  : "";
	String pwd = request.getParameter("pwd") != null ? request.getParameter("pwd").trim() : "";
	
	String ctx = request.getContextPath();
	if (id.isEmpty() || pwd.isEmpty()) {
	    out.println("<script>alert('아이디와 비밀번호를 입력해주세요.'); location.href='" + ctx + "/login.jsp';</script>");
	    return;
	}
	
	// DB 연결: DBUtil
	try (Connection conn = DBUtil.getConnection()) {
	
	    if (conn == null) {
	        out.println("<script>alert('DB 연결 실패: 설정을 확인하세요.'); location.href='" + ctx + "/login.jsp';</script>");
	        return;
	    }
	
	    // 평문 비밀번호 사용 버전 (해시 사용 시 아래 주석 참고)
	    String sql = "SELECT name FROM users WHERE id = ? AND password = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, id);
	        pstmt.setString(2, pwd);
	
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                String name = rs.getString("name");
	                // 세션 저장
	                session.setAttribute("loginId", id);
	                session.setAttribute("loginName", name);
	                // 메인으로 이동
	                response.sendRedirect("../mainpage/mainpage.jsp");
	                return;
	            } else {
	                out.println("<script>alert('로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.'); location.href='" + ctx + "/login/login.jsp';</script>");
	                return;
	            }
	        }
	    }
	
	} catch (Exception e) {
	    e.printStackTrace();
	    out.println("<script>alert('DB 오류가 발생했습니다.'); location.href='" + ctx + "/login.jsp';</script>");
	}
%>
</body>
</html>
