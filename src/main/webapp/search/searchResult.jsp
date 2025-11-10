<%@page import="java.util.List"%>
<%@page import="util.DBUtil"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.travel.dto.ScheduleDTO"%>
<%@page import="com.travel.dao.ScheduleDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>검색된 일정</title>
<link rel="stylesheet" href="./css/search.css">
</head>
<body>
	<%@ include file="../header.jsp" %>
	
	<div class="main">
		<div class="title">
			<h1>검색 결과</h1>
		</div>
		<div class="center">
			<div class="sc-container">
				<%
					String keyword = request.getParameter("keyword");
				
					ScheduleDAO scheduleDAO = new ScheduleDAO();	
					List<ScheduleDTO> searchResults = null;
					
					try (Connection conn = DBUtil.getConnection()) {
						searchResults = scheduleDAO.searchSchedule(conn, keyword);
					} catch (Exception e) {
				        e.printStackTrace();
				    }
					
					if (keyword == "" || searchResults == null || searchResults.isEmpty()) {
						if (keyword == null) keyword = "";
				%>
						<h3> 
							"<%= keyword %>"로 검색된 결과가 없습니다.
						</h3>
				<%
					} else {
						for (ScheduleDTO sc : searchResults) {
				%>
							<div class="sc">
								<a href="<%= request.getContextPath() %>/schedule/schedule.jsp?schedule_id=<%= sc.getId() %>" class="card-link">
									<div class="card">
		                            	<%
		                            		String mainImage = sc.getMainImage();
		                            		if (mainImage == null || mainImage.isEmpty()) {
		                            	%>
				                                <img src="<%= request.getContextPath() %>/schedule/image/basic.png" alt="여행 대표 이미지">
		                            	<%
		                            		} else {
		                            	%>
				                                <img src="/upload/<%= mainImage %>" alt="여행 대표 이미지">
		                            	<%
		                            		}
		                            	%>									
									  	<div class="card-content">
									  		<div class="visibility">
										  		<h3><%= sc.getTitle() %></h3>
										  		<%
										  			if("N".equals(sc.getVisibility())) {		
												%>
													<span class="material-symbols-outlined lockcolor">lock</span>
												<%
													}													
												%>
											</div>
									    	<p><%= sc.getStartDate() %></p>
									  	</div>
									</div>
								</a>
							</div>
				<%
						}
					}
				%>
            </div>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>
</body>
</html>