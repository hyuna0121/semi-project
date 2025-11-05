<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.travel.dto.ScheduleDTO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>전체 일정</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/search/css/search.css">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet" />
</head>
<body>
    <%@ include file="../header.jsp" %>

    <%
        // 제네릭 캐스팅을 피해서 JSP 파서 오류 방지
        List list = (List) request.getAttribute("list");
        Integer pageVal  = (Integer) request.getAttribute("page");   if(pageVal==null) pageVal=1;
        Integer pagesVal = (Integer) request.getAttribute("pages");  if(pagesVal==null) pagesVal=1;
    %>

    <div class="main">
        <div class="title">
            <h1>전체 일정</h1>
        </div>

        <div class="center">
            <div class="sc-container">
                <%
                if (list == null || list.isEmpty()) {
                %>
                    <h3>표시할 일정이 없습니다.</h3>
                <%
                } else {
                    for (Object obj : list) {
                        ScheduleDTO sc = (ScheduleDTO) obj;
                %>
                    <div class="sc">
                        <a href="<%= request.getContextPath() %>/schedule/schedule.jsp?schedule_id=<%= sc.getId() %>" class="card-link">
                            <div class="card">
                            <%
								String file = sc.getMainImage();
								if (file != null && !file.isBlank()) {
								  int ps = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));
								  if (ps >= 0) file = file.substring(ps + 1); // 혹시 경로가 섞여 들어와도 안전
								}
								String imgSrc = (file==null || file.isBlank())
								  ? request.getContextPath()+"/image/placeholder.jpg"
								  : request.getContextPath()+"/upload/"+java.net.URLEncoder.encode(file,"UTF-8").replace("+","%20");	
							%>
                                <img src="<%= imgSrc %>" alt="여행 대표 이미지">
                                <div class="card-content">
                                    <div class="visibility">
                                        <h3><%= sc.getTitle() %></h3>
                                        <% if ("N".equals(sc.getVisibility())) { %>
                                            <span class="material-symbols-outlined lockcolor">lock</span>
                                        <% } %>
                                    </div>
                                    <p><%= sc.getStartDate() %> ~ <%= sc.getEndDate() %></p>
                                </div>
                            </div>
                        </a>
                    </div>
                <%
                    } // for
                %>

                <!-- 페이지네이션 -->
                <div style="width:100%;display:flex;justify-content:center;gap:8px;margin-top:10px;">
                    <%
                        for (int i=1; i<=pagesVal; i++) {
                            String href = request.getContextPath()+"/schedules?page="+i;
                    %>
                        <a href="<%=href%>"
                           style="border:1px solid #ddd;padding:8px 12px;border-radius:8px;text-decoration:none;<%= (i==pageVal) ? "background:#2563eb;color:#fff;border-color:#2563eb;" : "color:#111;" %>">
                           <%=i%>
                        </a>
                    <% } %>
                </div>

                <%
                } // else
                %>
            </div>
        </div>
    </div>

    <%@ include file="../footer.jsp" %>
</body>
</html>
