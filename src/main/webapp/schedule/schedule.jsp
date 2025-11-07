<%@page import="java.util.List"%>
<%@page import="com.travel.dao.ChatDAO"%>
<%@page import="com.travel.dto.ChatDTO"%>
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
    
<%@page import="java.text.SimpleDateFormat"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="./css/map.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<!-- <link rel="stylesheet" href="./css/chatSchedule.css"> -->

<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://npmcdn.com/flatpickr/dist/l10n/ko.js"></script>
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=8aaef2cb5fdf5a54c0607c5d2c9935c1&libraries=services"></script>
<script type="text/javascript" src="./js/map.js" defer></script>
<script type="text/javascript" src="./js/details.js" defer></script>
</head>

<script>
	const CONTEXT_PATH = "<%= request.getContextPath() %>";
    flatpickr("#schedule-time", {
        enableTime: true,   // 시간 선택 활성화
        noCalendar: true,   // 캘린더(날짜) 비활성화
        dateFormat: "H:i",  // 시간 형식 (24시간제, 예: 14:30)
        time_24hr: true,    // 24시간제로 표시
        locale: "ko"        // (선택) 한국어 설정
    });

/* 		document.getElementById('schedule-form').addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            
            // Enter 키를 누른 요소(element)를 확인
            const target = event.target;

            // 1. 만약 'textarea' 또는 'button'에서 Enter를 눌렀다면,
            //    기본 동작(줄바꿈 또는 클릭)을 허용합니다.
            if (target.tagName.toLowerCase() === 'textarea' || 
                target.tagName.toLowerCase() === 'button' ||
                target.type === 'submit' ||
                target.type === 'button') 
            {
                return; // 아무것도 막지 않음
            }

            // 2. 그 외의 모든 요소(예: #schedule-time 입력창)에서
            //    Enter를 누르면 폼 제출을 막습니다.
            event.preventDefault();
        }
    }); */
</script>

<body>
	<%@ include file="../header.jsp" %>
	
	<%
		Connection conn = null;
		ScheduleDTO schedule = null;
		List<ChatDTO> comments = null;
		String errorMessage = null;
		long scheduleId = 0;
		
		try {
			String idParam = request.getParameter("schedule_id");
			
			if (idParam != null && !idParam.isEmpty()) {
				scheduleId = Long.parseLong(idParam);
				
				conn = DBUtil.getConnection(); 
				ScheduleDAO dao = new ScheduleDAO();
				
				schedule = dao.selectSchedule(conn, scheduleId);
				if(schedule != null){
					ChatDAO chatdao = new ChatDAO();
					
					comments = chatdao.getCommentsByScheduleId((int) scheduleId);
				}			
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
		
		ChatDAO profileDao = new ChatDAO();
		String currentUserProfileImg = profileDao.getProfileImageByUserId(userId);

		
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
													<th>시간</th>
													<th>여행지</th>
													<th>태그</th>
													<th>메모</th>
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
						</div>
						
						<div class="input-container" style="width:70%; margin: 0 auto; margin-top: 35px; text-align: center;">
							<form action="${pageContext.request.contextPath}/processAddDetail?schedule_id=<%= scheduleId %>" method="post" id="schedule-form">
								<button type="reset" class="close_map_btn material-symbols-outlined">close</button> 
								<h3>일정 등록</h3>
								<div style="display: none;">
									<input type="hidden" id="schedule-id-input" name="schedule_id" value="<%= scheduleId %>">
									<input type="hidden" id="modalPlaceName" name="placeName">
                  <input type="hidden" id="modalLatitude" name="latitude">
                  <input type="hidden" id="modalLongitude" name="longitude">
								</div>
								<div>
									<div class="date-selector-container">
										<%                    
											LocalDate currentDate = startDate;
											
											DateTimeFormatter dateShorthand = DateTimeFormatter.ofPattern("MM.dd");
											DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("E", java.util.Locale.KOREAN);
											int dayCount = 1;
													
											while (!currentDate.isAfter(endDate)) {
												String fullDateStr = currentDate.toString();
												String displayDateStr = currentDate.format(dateShorthand); 
												String dayOfWeek = currentDate.format(dayOfWeekFormatter);
												String uniqueId = "date_" + fullDateStr;
										%>
											<input type="checkbox" id="<%= uniqueId %>" 
															name="selectedDates" value="<%= fullDateStr %>" class="date-checkbox-hidden" />
											
											<label for="<%= uniqueId %>" class="date-button">
												<span class="day-count">day<%= dayCount %></span>
												<span class="day-date"><%= displayDateStr %>/<%= dayOfWeek %></span>
											</label>
										<%    
											// 다음 날짜로 이동
											currentDate = currentDate.plusDays(1);
											dayCount++;
											}
										%>
									</div>
								</div>

								<div>
									<div class="category-selector-container">
		
										<input type="radio" id="cat-tour" name="category" value="관광지" 
												class="category-radio-hidden" checked>
										<label for="cat-tour" id="cat-tour-label" class="category-button">관광지</label>

										<input type="radio" id="cat-shopping" name="category" value="쇼핑" 
												class="category-radio-hidden" checked>
										<label for="cat-shopping" id="cat-shopping-label" class="category-button">쇼핑</label>

										<input type="radio" id="cat-food" name="category" value="식당" 
												class="category-radio-hidden">
										<label for="cat-food" id="cat-food-label" class="category-button">식당</label>

										<input type="radio" id="cat-cafe" name="category" value="카페" 
												class="category-radio-hidden">
										<label for="cat-cafe" id="cat-cafe-label" class="category-button">카페</label>

										<input type="radio" id="cat-stay" name="category" value="숙소" 
												class="category-radio-hidden">
										<label for="cat-stay" id="cat-stay-label" class="category-button">숙소</label>
		
									</div>
								</div>

								<div class="time-container">
                  <label for="schedule-time" style="font-weight: bold; margin-right: 10px;">시작 시간</label>
                  <input type="text" id="schedule-time" name="scheduleTime" 
													class="input-time" placeholder="HH:MM (예: 09:30 또는 14:00)">
                </div>

								<div class="memo-container">
									<textarea class="input-memo" name="memo" placeholder="MEMO"></textarea>
								</div>

								<div class="button-container">
									<button type="submit" class="add_schedule">일정등록</button>
									<button type="reset" class="close_add_btn">취소하기</button>
								</div>
							</form>
						</div>

						<div id="map_info" style="width:70%; margin: 0 auto;"></div>
						<div id="map" style="width:70%;height:25%; margin: 0 auto; border-radius: 5px;"></div>

						<div style="display: none;">
							<button type="button" class="add_schedule_btn">일정에 추가</button>
							<button type="button" class="close_map_btn">취소하기</button>
						</div>
					</div>
				</div>

			</div>
						<div class="chat" id="comment-section">
				<h3>댓글 목록 (<%= comments != null ? comments.size() : 0 %>)</h3>
	
				<div id="comment-List">
				    <% 
				    
					    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				    	if (comments != null && !comments.isEmpty()) {
					    for (ChatDTO c : comments) {    
				    %>
				            <div class="comment-item">
				                <div class="comment-left">
				                	<%
				                		String profileImg = c.getProfile_image();
				                	
				                		if(profileImg != null && !profileImg.isEmpty()){
				                	%>
				                	<img alt="profileimg" 
				            				 src="<%= request.getContextPath() %>/mypage/image/<%= profileImg %>" 
				            				 class="profileImg">				                	<%
				                		}else{
				                	%>
				                	<span class="material-symbols-outlined profile-icon">account_circle</span>				                		

									<%
				                		}
									%>				                
				                
				                </div>
				                <div class="comment-right">
					                <strong><%= c.getUser_id() %></strong>
					                
					                <div class="comment-content-text">
					                	<%= c.getcomment() %>
					                </div>
					                
					                <div class="subContent">
										<div class="comment-timestamp">
								        	<small>(작성: <%= sdf.format(c.getCreatedAt()) %>)</small>
								        </div>
								        <div class="comment-actions">
											<% if (userId != null && userId.equals(c.getUser_id())){ %>
								            <form action="<%= request.getContextPath() %>/commentAction" method="post" style="display:inline;">
								            	<input type="hidden" name="action" value="delete" />
								                <input type="hidden" name="commentId" value="<%= c.getComment_id()  %>" />
								                <input type="hidden" name="scheduleId" value="<%= scheduleId %>" />
								                <button type="submit" class="btn-gray" onclick="return confirm('정말 삭제할까요?');">삭제</button>
								            </form>
								            <% } %>
							            </div> 
							    	</div> 
						    	</div> 
				            </div>
				    <%  }
				      } else { %>
				        <p>등록된 댓글이 없습니다.</p>
				    <% } %>
				</div>
				
				<hr>
				
				<h3>댓글 등록</h3>
				<form class="comment-form-new" action="<%= request.getContextPath() %>/commentAction" method="post">
				    <input type="hidden" name="action" value="insert">
				    <input type="hidden" name="scheduleId" value="<%= scheduleId %>">
				    
				    <div class="form-left">
				    	<%
				                		
				                	
				                		if(currentUserProfileImg != null && !currentUserProfileImg.isEmpty()){
				                	%>
				                	<img alt="profileimg" 
				            				 src="<%= request.getContextPath() %>/mypage/image/<%= currentUserProfileImg %>" 
				            				 class="profileImg">				                	<%
				                		}else{
				                	%>
				                	<span class="material-symbols-outlined profile-icon">account_circle</span>				                		

									<%
				                		}
									%>
				    </div>
				    
				    <div class="form-right">
				    	<div class="form-user-id"><%= userId %></div>
				    	<textarea name="content" rows="3" placeholder="댓글을 입력하세요."></textarea>
					    <div class="btn-gray-submit">
					    	<button type="submit" class="btn-gray">등록</button>
					    </div>
				    </div>
				</form>					
			</div>
		</div>

	</div>
    
    <%@ include file="../footer.jsp" %>
</body>
</html>