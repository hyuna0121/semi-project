<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	
	//DB연결(예시)
	String url = "jdbc:mysql://localhost:3306/memberjoin";
	String user="root"; 
	String password ="test1234";
	String sql = "SELECT id FROM member WHERE id = ?"; 
	
	boolean exists = false; 
	
	try{
		Class.forName("com.mysql.cj.jdbc.Driver"); 
		Connection conn = DriverManager.getConnection(url,user,password); 
		PreparedStatement pstmt = conn.prepareStatement(sql); 
		pstmt.setString(1,id);
		ResultSet rs = pstmt.executeQuery();
		
		if(rs.next()) {
			
			exists=true;
		}
		
		rs.close();
		pstmt.close();
		conn.close();
	} catch(Exception e) {
		e.printStackTrace();
	}
	
    if (exists) {
        out.print("<span style='color:red;'>이미 사용 중인 아이디입니다 ❌</span>");
    } else {
        out.print("<span style='color:green;'>사용 가능한 아이디입니다 ✅</span>");
    }
	
%>

</body>
</html>