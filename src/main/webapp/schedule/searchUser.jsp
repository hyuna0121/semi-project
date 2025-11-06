<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sql" uri="jakarta.tags.sql" %>
<%@ page import="java.net.URLDecoder" %>

<sql:setDataSource var="db" driver="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://192.168.8.141:3306/traveldb?serverTimezone=UTC"
    user="traveldb" password="mysql1234"/>

<%
    String query = request.getParameter("query");

    if (query != null) { 
        query = URLDecoder.decode(query, "UTF-8");
    } else {
        query = "";
    }
    
    pageContext.setAttribute("query", query);
%>

<sql:query var="rs" dataSource="${db}">
    SELECT id FROM users WHERE id LIKE ? LIMIT 3;
    <sql:param value="${query}%" />
</sql:query>

<%-- 4. 결과를 JSON 배열로 수동 변환 --%>
[
<c:forEach var="row" items="${rs.rows}" varStatus="status">
    "${row.id}"<c:if test="${!status.last}">,</c:if>
</c:forEach>
]