<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일정 추가</title>
</head>
<body>
	<sql:setDataSource var="dataSource"
		url="jdbc:mysql://127.0.0.1:3306/traveldb"
	 	driver="com.mysql.cj.jdbc.Driver" user="root" password="test1234"/>
	<%
		String visibility = request.getParameter("visibility") == null ? "N" : "Y";
		request.setAttribute("visibility", visibility);
	%>  
	
	<sql:update var="result" dataSource="${dataSource}">
		INSERT INTO schedules(user_id, title, location, description, visibility, start_date, end_date, main_image, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW());
		<sql:param value="admin" />
		<sql:param value="${param.title}" />
		<sql:param value="${param.location}" />
		<sql:param value="${param.description}" />
		<sql:param value="${visibility}" />
		<sql:param value="${param.startDate}" />
		<sql:param value="${param.endDate}" />
		<sql:param value="${param.mainImage}" />
	</sql:update>
	
	<c:if test="${result >= 1}">
		<c:redirect url="../index.jsp"></c:redirect>
	</c:if>
</body>
</html>