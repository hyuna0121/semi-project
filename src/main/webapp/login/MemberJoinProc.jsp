<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="util.DBUtil" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입 처리</title>
</head>
<body>
<%
	request.setCharacterEncoding("UTF-8");
	
	String name     = request.getParameter("name")     == null ? "" : request.getParameter("name").trim();
	String id       = request.getParameter("id")       == null ? "" : request.getParameter("id").trim();
	String nickname = request.getParameter("nickname") == null ? "" : request.getParameter("nickname").trim();
	String birth    = request.getParameter("birth")    == null ? "" : request.getParameter("birth").trim();
	String pass1    = request.getParameter("pass1")    == null ? "" : request.getParameter("pass1").trim();
	String pass2    = request.getParameter("pass2")    == null ? "" : request.getParameter("pass2").trim();
	String address  = request.getParameter("address")  == null ? "" : request.getParameter("address").trim();
	
	// 1) 필수/비밀번호 확인
	if (!pass1.equals(pass2)) {
	    out.println("<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>");
	    return;
	}
	if (name.isEmpty() || id.isEmpty() || pass1.isEmpty()) {
	    out.println("<script>alert('이름/아이디/비밀번호는 필수입니다.'); history.back();</script>");
	    return;
	}
	
	// 2) 생년월일 'YYYYMMDD' -> DATE 변환
	java.sql.Date birthDate = null;
	if (!birth.isEmpty()) {
	    if (birth.matches("\\d{8}")) {
	        String fmt = birth.substring(0,4) + "-" + birth.substring(4,6) + "-" + birth.substring(6,8);
	        try { birthDate = java.sql.Date.valueOf(fmt); }
	        catch (IllegalArgumentException iae) {
	            out.println("<script>alert('유효하지 않은 생년월일입니다.'); history.back();</script>");
	            return;
	        }
	    } else {
	        out.println("<script>alert('생년월일을 8자리 숫자(YYYYMMDD)로 입력해주세요.'); history.back();</script>");
	        return;
	    }
	}
	
	// traveldb 연결 (DBUtil이 내부에 URL/USER/PASS 포함)
	String ctx = request.getContextPath();
	try (Connection conn = DBUtil.getConnection()) {
	    if (conn == null) {
	        out.println("<script>alert('DB 연결 실패: 설정을 확인하세요.'); location.href='" + ctx + "/memberjoin.jsp';</script>");
	        return;
	    }
	
	    // 3) 아이디/닉네임 중복 체크 (users 테이블)
	    // id 중복
	    String sqlId = "SELECT 1 FROM users WHERE id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sqlId)) {
	        ps.setString(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                out.println("<script>alert('이미 존재하는 아이디입니다.'); history.back();</script>");
	                return;
	            }
	        }
	    }
	
	    // nickname 중복 (닉네임이 비어있지 않을 때만 체크)
	    if (!nickname.isEmpty()) {
	        String sqlNick = "SELECT 1 FROM users WHERE nickname = ?";
	        try (PreparedStatement ps = conn.prepareStatement(sqlNick)) {
	            ps.setString(1, nickname);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    out.println("<script>alert('이미 존재하는 닉네임입니다.'); history.back();</script>");
	                    return;
	                }
	            }
	        }
	    }
	
	    // 4) INSERT (traveldb.users)
	    // 주 스키마: users(id, name, password, phone, email, address, gender, profile_image, nickname, birth, ...)
	    // email NOT NULL 제약이 있으므로 폼이 없을 때 "" 넣어줍니다.
	    String insert = "INSERT INTO users " +
	                    "(id, name, password, phone, email, address, gender, profile_image, nickname, birth) " +
	                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement ps = conn.prepareStatement(insert)) {
	        ps.setString(1, id);
	        ps.setString(2, name);
	        ps.setString(3, pass1);              // 실제 서비스: BCrypt 등 해시 적용 권장
	        ps.setString(4, "");                  // phone
	        ps.setString(5, "");                  // email (NOT NULL이므로 빈문자)
	        ps.setString(6, address);
	        ps.setNull(7, java.sql.Types.CHAR);   // gender (없으면 NULL)
	        ps.setString(8, "");                  // profile_image
	        ps.setString(9, nickname);
	        if (birthDate != null) ps.setDate(10, birthDate);
	        else                   ps.setNull(10, java.sql.Types.DATE);
	
	        int result = ps.executeUpdate();
	        if (result > 0) {
	            out.println("<script>alert('회원가입 성공!'); location.href='" + ctx + "/login/login.jsp';</script>");
	        } else {
	            out.println("<script>alert('회원가입 실패'); history.back();</script>");
	        }
	    }
	
	} catch (Exception e) {
	    e.printStackTrace();
	    out.println("<script>alert('DB 오류 발생'); history.back();</script>");
	}
%>
</body>
</html>
