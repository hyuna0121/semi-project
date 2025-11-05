<%@page import="java.util.Arrays"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.travel.dao.ScheduleDAO"%>
<%@page import="com.travel.dto.ScheduleDTO"%>
<%@page import="util.DBUtil"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일정 수정</title>
<!-- <link rel="stylesheet" href="./css/map.css"> -->
<link rel="stylesheet" href="./css/editSchedule.css">
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=8aaef2cb5fdf5a54c0607c5d2c9935c1&libraries=services"></script>
<script type="text/javascript" src="./js/map.js" defer></script>
<script type="text/javascript" src="./js/editSchedule.js" defer></script>
</head>
<body>
	<%@ include file="../header.jsp" %>
	
	<%
		Connection conn = null;
		ScheduleDTO schedule = null;
		String[] members = null;
		String errorMessage = null;
		long scheduleId = 0;
		
		try {
			String idParam = request.getParameter("schedule_id");
			
			if (idParam != null && !idParam.isEmpty()) {
				scheduleId = Long.parseLong(idParam);
				
				conn = DBUtil.getConnection(); 
				ScheduleDAO dao = new ScheduleDAO();
				
				schedule = dao.selectSchedule(conn, scheduleId);
			} else {
				errorMessage = "유효한 schedule_id가 없습니다.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "데이터 조회 중 오류가 발생했습니다.";
		} finally {
			if (conn != null) conn.close(); 
		}
		
		String userId = (String) session.getAttribute("loginId");
		
		if (schedule == null) {
	%>
	<script type="text/javascript">
		alert("해당하는 일정이 없습니다.");
		location.href = "<%= request.getContextPath() %>/mainpage/mainpage.jsp";
	</script>
	<%
			return;
		}
		
		boolean flag = false;
		for (String buddy : schedule.getTravelBuddies()) {
			if (buddy.equals(userId)) {
				flag = true;
				break;
			}
		}
		
		if (flag == false) {
	%>
	<script type="text/javascript">
		alert("해당일정을 열람할 권한이 없습니다.");
		location.href = "<%= request.getContextPath() %>/mainpage/mainpage.jsp";
	</script>
	<%
			return;
		}
	%>
	
	<div class="container">
		<div class="left">
			<h1>여행 친구</h1>
		</div>

		<div class="right">
			<form action="<%= request.getContextPath() %>/processEditSchedule" method="post" enctype="multipart/form-data">
				<input type="hidden" name="scheduleId" value="<%= scheduleId %>">

				<div class="trip_main_image">
					<input type="file" id="imageInput" name="mainImage" style="display: none;">
					<label for="imageInput" style="cursor: pointer;">
					<img id="imagePreview" src="/upload/<%= schedule.getMainImage() %>" alt="현재 대표 이미지">					
					</label>
				</div>
				
				<div class="trip_content">
					<div class="trip_title" style="height: 300px;">
						<p>여행 제목:
						<input type="text" name="title" value="<%= schedule.getTitle() %>" style="height: 30px; border: 1px solid rgb(199, 199, 199); margin-bottom: 20px; padding-left: 10px;" >

						<input type="checkbox" name="visibility" id="visibility" value="<%= schedule.getVisibility() %>"
						<%= ("N".equals(schedule.getVisibility())) ? "checked" : " " %>
						style="display: none;">
						
						<label for="visibility" style="cursor: pointer; margin-left: 20px;" title="공개/비공개 전환" >
				            <span id="visibilityText" class="material-symbols-outlined" style="font-size: 2em; vertical-align: middle;">
				                <%= "N".equals(schedule.getVisibility()) ? "lock" : "lock_open_right" %>
				            </span>
				        </label>
				        </p>
						<%
							
							
							String startStr = schedule.getStartDate();
							String endStr = schedule.getEndDate();
							
							LocalDate startDate = LocalDate.parse(startStr);
							LocalDate endDate = LocalDate.parse(endStr);
							
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

							
						%>
						<p>
							<span class="region-label">여행 지역:</span>
							<input type="text" name="location" value="<%= schedule.getLocation() %>" style="height: 30px; border: 1px solid rgb(199, 199, 199); margin-bottom: 20px; padding-left: 10px;">
						</p>
						<p>
							<%= startDate.format(formatter) %> ~ <%= endDate.format(formatter) %>
						</p>
						<p class="desc">일정:</p>
						<textarea name="description" rows="5" style="border: 1px solid #c4c4c4; width: 90%; height: 50%; padding-left: 10px; padding-top: 10px;"><%= schedule.getDescription() %></textarea>
					</div>		
					<div style="text-align: right; margin-top: 50px; margin-right: 180px; margin-bottom: 50px;">
						
                      
                        <button type="submit" style="padding: 10px 20px; font-size: 1.2em;">수정 완료</button>
                        
                       
                        <form action="<%= request.getContextPath() %>/processDeleteSchedule" method="post" style="display: inline-block; margin-left: 10px;">
                            <input type="hidden" name="scheduleId" value="<%= scheduleId %>">
                            <button type="submit" style="padding: 10px 20px; font-size: 1.2em; color: red;">일정 삭제</button>
                        </form>

					</div>
				</div>
			</form>
			
		</div>
	</div>

	<%@ include file="../footer.jsp" %>
</body>
</html>
