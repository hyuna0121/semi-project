<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ page import="java.net.URLDecoder" %>

<sql:setDataSource var="db" driver="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/traveldb"
    user="root" password="test1234"/>

<%
    String userId = request.getParameter("userId");

    if (userId != null) {
        userId = URLDecoder.decode(userId, "UTF-8");
    }
    
    pageContext.setAttribute("userId", userId);
%>

<sql:query var="rs" dataSource="${db}">
    SELECT id, name, email FROM users WHERE id = ?;
    <sql:param value="${userId}" />
</sql:query>

<c:choose>
    <c:when test="${rs.rowCount > 0}">
        <c:set var="user" value="${rs.rows[0]}" />
        {
            "userId": "${user.id}",
            "userName": "${user.name}",
            "email": "${user.email}"
        }
    </c:when>
    <c:otherwise>
        {} 
    </c:otherwise>
</c:choose>