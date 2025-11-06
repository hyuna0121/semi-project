<%@page import="java.time.temporal.ChronoUnit"%>
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
<title>Insert title here</title>
<link rel="stylesheet" href="./css/map.css">
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=8aaef2cb5fdf5a54c0607c5d2c9935c1&libraries=services"></script>
<script type="text/javascript" src="./js/map.js" defer></script>
<script type="text/javascript" src="./js/details.js" defer></script>
</head>
<body>
	<%@ include file="../header.jsp" %>
	
	<%
		Connection conn = null;
		ScheduleDTO schedule = null;
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
		
		String visibility = schedule.getVisibility();
		boolean flag = false;
		
		if ("N".equals(visibility)) {
			// if (userId.equals(schedule.getUserId())) flag = true;
			
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
			history.back();
	</script>
	<%
				return;
			}
		}
	%>
	
	<div class="container">
		<div class="left">
			<h1>여행 친구</h1>
		</div>

		<div class="right">
			<div class="trip_main_image">
				<img src="/upload/<%= schedule.getMainImage() %>" alt="여행 대표 이미지">
			</div>

			<div class="trip_content">
				<div class="trip_title" style="height: 300px;">
					<h1>
						<%= schedule.getTitle() %>
						<%
							if ("N".equals(schedule.getVisibility())) {
						%>
								<span class="material-symbols-outlined">lock</span>
						<%
							}
						%> 
					</h1>
					<%
						String startStr = schedule.getStartDate();
						String endStr = schedule.getEndDate();
									
						LocalDate startDate = LocalDate.parse(startStr);
						LocalDate endDate = LocalDate.parse(endStr);
	
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
					%>
					<p>
						<span><%= schedule.getLocation() %></span>
						<%= startDate.format(formatter) %> ~ <%= endDate.format(formatter) %>
					</p>
					<p class="desc"><%= schedule.getDescription() %></p>
				</div>

				<div class="details">
					<%
						long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
					%>
					<h1>일정 타임라인</h1>

					<nav class="tabs">
						<ul>
                  		<%
                  			for (int day = 1; day <= totalDays; day++) {
                  				String activeClass = (day == 1) ? "active" : "";
		                %>
                        	<li class="tab-link <%= activeClass %>" data-day="<%= day %>">
                        <%= day %>일차
                        	</li>
                  		<%
                      		} 
                  		%>
              			</ul>
						<button type="button" class="new-item-btn modal_btn">일정추가 +</button>
					</nav>

					<div class="table-container">
							<table>
									<thead>
											<tr>
													<th>여행지</th>
													<th>도시</th>
													<th>태그</th>
													<th>운영시간</th>
											</tr>
									</thead>
									<tbody id="itinerary-board">
											</tbody>
							</table>
					</div>
				</div>

				<div class="modal">
					<div id="menu_wrap" class="bg_white" style="width:40%; height: 70%;">
						<div class="btn-wrap">
							<button type="submit" class="close_btn material-symbols-outlined">close</button> 
						</div>
						<div class="option">
							<div>
								<form onsubmit="searchPlaces(); return false;" class="search" style="width: 250px;">
									<input type="text" value="<%= schedule.getLocation() %>" id="keyword" size="15"> 
									<button type="submit" class="material-symbols-outlined">search</button> 
								</form>
							</div>
						</div>
						<hr>
						<ul id="placesList"></ul>
						<div id="pagination"></div>
					</div>
				</div>
				
				<div class="modal_map">
					<div id="menu_wrap" class="bg_white" style="width:40%; height: 70%;">
								<div class="btn-wrap">
									<button type="button" class="close_map_btn material-symbols-outlined">close</button> 
								</div>
									<div id="map" style="width:80%;height:80%;"></div>
									<div id="map_info"></div>
									<div>
							<button type="button" class="add_schedule_btn">일정에 추가</button>
							<button type="button" class="close_map_btn">취소하기</button>
									</div>
							</div>
					</div>

					<div class="modal_add">
							<div class="bg_white" style="width:40%; height: 70%;">
									<div>
								<form action="">
								<div>
									<input type="hidden" value="<%= scheduleId %>">
								</div>
								<div>
									<%													
										LocalDate currentDate = startDate;
													
										while (!currentDate.isAfter(endDate)) {
											String fullDateStr = currentDate.toString();
											String displayDateStr = currentDate.format(formatter);
											String uniqueId = "date_" + fullDateStr;
									%>
											<input type="checkbox" id="<%= uniqueId %>" 
													name="selectedDates" value="<%= fullDateStr %>" />
											<label for="<%= uniqueId %>"><span><%= displayDateStr %></span></label>
									<%	
														// 현재 날짜를 하루 증가
											currentDate = currentDate.plusDays(1);
											}
									%>
								</div>
								<div>
									<select name="category">
										<option value="관광지" selected="selected">관광지</option>
										<option value="식당">식당</option>
										<option value="카페">카페</option>
										<option value="숙소">숙소</option>
									</select>
								</div>
								<div>
									<textarea placeholder="memo"></textarea>
								</div>
								<div>
									<button type="submit">일정 등록</button>
									<button type="button" class="close_add_btn">취소 하기</button>
								</div>
							</form>
									</div>
							</div>
					</div>

			</div>
		</div>

	</div>
    
    <%@ include file="../footer.jsp" %>
</body>
</html>