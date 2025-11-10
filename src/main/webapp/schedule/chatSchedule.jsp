<%@page import="com.travel.dao.ChatDAO"%>
<%@page import="com.travel.dto.ChatDTO"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDate"%>
<%@page import="com.travel.dao.ScheduleDAO"%>
<%@page import="com.travel.dto.ScheduleDTO"%>
<%@page import="util.DBUtil"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!-- 추가 -->
<%@page import="java.text.SimpleDateFormat"%>
<!-- 추가 끝 -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=8aaef2cb5fdf5a54c0607c5d2c9935c1&libraries=services"></script>
<script type="text/javascript" src="./js/map.js" defer></script>
<!-- 추가 -->
<link rel="stylesheet" href="./css/chatSchedule.css">
<!-- script type="text/javascript" src="./js/editComment.js" defer></script> -->
<!-- 추가 끝 -->
</head>
<body>
	<%@ include file="../header.jsp" %>
	
	<%
		Connection conn = null;
		ScheduleDTO schedule = null;
	
		/* 추가 */
		List<ChatDTO> comments = null;
		/* 추가 끝 */
		
		String errorMessage = null;
		long scheduleId = 0;
		
		try {
			String idParam = request.getParameter("schedule_id");
			
			if (idParam != null && !idParam.isEmpty()) {
				scheduleId = Long.parseLong(idParam);
				
				conn = DBUtil.getConnection(); 
				ScheduleDAO dao = new ScheduleDAO();
				
				schedule = dao.selectSchedule(conn, scheduleId);
				
				/* 추가 */
				if(schedule != null){
					ChatDAO chatdao = new ChatDAO();
					
					comments = chatdao.getCommentsByScheduleId((int) scheduleId);
				}
				/* 추가 끝 */
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
		
		/* 추가 */
		ChatDAO profileDao = new ChatDAO();
		String currentUserProfileImg = profileDao.getProfileImageByUserId(userId);
		/* 추가 끝 */
		
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

				<div>
					<button type="button" class="modal_btn">일정추가 +</button>
				</div>
					<div class="modal">
							<div id="menu_wrap" class="bg_white" style="width:50%; height: 80%;">
						<button type="button" class="close_btn">X</button>
									<div class="option">
											<div>
													<form onsubmit="searchPlaces(); return false;">
															키워드 : <input type="text" value="경복궁" id="keyword" size="15"> 
															<button type="submit">검색하기</button> 
													</form>
											</div>
									</div>
									<hr>
									<ul id="placesList"></ul>
									<div id="pagination"></div>
							</div>
					</div>

					<div class="modal_map">
							<div class="bg_white" style="width:50%; height: 80%;">
									<div id="map" style="width:80%;height:80%;"></div>
									<div id="map_info"></div>
									<div>
							<button type="button" class="add_schedule_btn">일정에 추가</button>
							<button type="button" class="close_map_btn">취소하기</button>
									</div>
							</div>
					</div>

					<div class="modal_add">
							<div class="bg_white" style="width:50%; height: 80%;">
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
			
			<!-- 추가 -->
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
			<!-- 추가 끝 -->
		</div>
	</div>
    
    <%@ include file="../footer.jsp" %>
</body>
</html>