<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.travel.dao.ScheduleDAO"%>
<%@ page import="com.travel.dto.ScheduleDTO"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.io.PrintWriter"%>
<%
// 🔸 1. 로그인된 사용자 ID 확인 (세션에서 가져와야 함)
String userId = (String) session.getAttribute("loginId");

// DB에서 일정 정보를 조회할 리스트 선언
List<ScheduleDTO> myScheduleList = new ArrayList<>();
List<ScheduleDTO> joinedScheduleList = new ArrayList<>();

SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
StringBuilder jsonEvents = new StringBuilder("[");
boolean isFirstEvent = true;

try {
	ScheduleDAO dao = new ScheduleDAO();

	myScheduleList = dao.getMySchedules(userId);
	joinedScheduleList = dao.getJoinedSchedulesByUserId(userId);

	// 🔸 캘린더 JSON 데이터 생성 - 1. 내가 만든 일정
	for (ScheduleDTO schedule : myScheduleList) {
		if (!isFirstEvent) {
	jsonEvents.append(",");
		}

		String startDate = schedule.getStartDate();
		String endDate = schedule.getEndDate();
		String calendarEndDate = null; // FullCalendar에 전달할 +1된 종료일

		// 🚨 FullCalendar end 날짜 계산 로직: DB 종료일에 하루를 더합니다. (다일 일정인 경우에만)
		if (endDate != null && !endDate.isEmpty() && !startDate.equals(endDate)) {
	try {
		Date date = dateFormat.parse(endDate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1); // 하루 더하기
		calendarEndDate = dateFormat.format(c.getTime()); // +1된 날짜 포맷
	} catch (java.text.ParseException e) {
		// 날짜 형식 오류 발생 시 (e.g. DB 포맷 불일치), 오류를 기록하고 end 속성 추가를 건너뜀
		System.err.println("날짜 파싱 오류(My Schedule ID: " + schedule.getId() + "): " + e.getMessage());
		// calendarEndDate는 null 상태로 유지되어 end 속성 추가가 스킵됨
	}
		}

		jsonEvents.append("{");
		jsonEvents.append("title: '").append(schedule.getTitle()).append("',");
		jsonEvents.append("start: '").append(startDate).append("',");

		if (calendarEndDate != null) { // ✅ 유효한 +1된 종료일이 있을 경우에만 추가
	jsonEvents.append("end: '").append(calendarEndDate).append("',");
		}

		jsonEvents.append("id: ").append(schedule.getId()).append(",");
		jsonEvents.append("color: '#ffc107',");
		jsonEvents.append("extendedProps: {");
		jsonEvents.append("location: '").append(schedule.getLocation() != null ? schedule.getLocation() : "")
		.append("',");
		jsonEvents.append("description: '").append(schedule.getDescription() != null ? schedule.getDescription() : "")
		.append("',");
		jsonEvents.append("isCreator: true,");
		// String.join()이 null을 반환할 수 있으므로 방어 코드 추가
		jsonEvents.append("buddies: '")
		.append(schedule.getTravelBuddies() != null ? String.join(",", schedule.getTravelBuddies()) : "")
		.append("'");
		jsonEvents.append("}");
		jsonEvents.append("}");
		isFirstEvent = false;
	}

	// 🔸 캘린더 JSON 데이터 생성 - 2. 참여 중인 일정 추가
	for (ScheduleDTO schedule : joinedScheduleList) {
		if (!isFirstEvent) {
	jsonEvents.append(",");
		}

		String startDate = schedule.getStartDate();
		String endDate = schedule.getEndDate();
		String calendarEndDate = null; // FullCalendar에 전달할 +1된 종료일

		// 🚨 FullCalendar end 날짜 계산 로직: DB 종료일에 하루를 더합니다.
		if (endDate != null && !endDate.isEmpty() && !startDate.equals(endDate)) {
	try {
		Date date = dateFormat.parse(endDate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1); // 하루 더하기
		calendarEndDate = dateFormat.format(c.getTime());
	} catch (java.text.ParseException e) {
		System.err.println("날짜 파싱 오류(Joined Schedule ID: " + schedule.getId() + "): " + e.getMessage());
	}
		}

		jsonEvents.append("{");
		jsonEvents.append("title: '").append(schedule.getTitle()).append(" (참여)',");
		jsonEvents.append("start: '").append(startDate).append("',");

		if (calendarEndDate != null) { // ✅ 유효한 +1된 종료일이 있을 경우에만 추가
	jsonEvents.append("end: '").append(calendarEndDate).append("',");
		}

		jsonEvents.append("id: ").append(schedule.getId()).append(",");
		jsonEvents.append("color: '#0d6efd',");
		jsonEvents.append("extendedProps: {");
		jsonEvents.append("location: '").append(schedule.getLocation() != null ? schedule.getLocation() : "")
		.append("',");
		jsonEvents.append("description: '").append(schedule.getDescription() != null ? schedule.getDescription() : "")
		.append("',");
		jsonEvents.append("isCreator: false,");
		jsonEvents.append("creatorId: '").append(schedule.getUserId()).append("',");
		jsonEvents.append("buddies: '")
		.append(schedule.getTravelBuddies() != null ? String.join(",", schedule.getTravelBuddies()) : "")
		.append("'");
		jsonEvents.append("}");
		jsonEvents.append("}");
		isFirstEvent = false;
	}

	jsonEvents.append("]");

} catch (Exception e) {
	e.printStackTrace(new PrintWriter(System.err)); // 서버 로그에 상세 오류 기록
	out.println("<script>alert('일정 정보를 불러오는 중 데이터베이스 또는 서버 오류가 발생했습니다. 자세한 내용은 콘솔을 확인해주세요.');</script>");
	myScheduleList = new ArrayList<>();
	joinedScheduleList = new ArrayList<>();
	jsonEvents = new StringBuilder("[]");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>여행 일정 | 마이페이지</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link
	href='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/main.min.css'
	rel='stylesheet' />
<script
	src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js'></script>

<link rel="stylesheet" href="css/travel_schedule.css">

</head>
<body>

	<%@ include file="../header.jsp"%>

	<div class="main-container">
		<aside class="sidebar">
			<h5>My Page</h5>
			<a href="mypage_profile.jsp">내 프로필</a> <a href="#" class="active">여행
				일정</a> <a href="#">내 댓글</a>
		</aside>

		<main class="content">
			<div class="container-fluid">
				<h3 class="mb-3 border-bottom pb-2">📆 여행 일정</h3>
				<div id="calendar"></div>

				<ul class="nav nav-tabs mt-5" id="scheduleTabs" role="tablist">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="my-schedule-tab"
							data-bs-toggle="tab" data-bs-target="#my-schedule" type="button"
							role="tab" aria-controls="my-schedule" aria-selected="true">
							내가 만든 일정</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="joined-schedule-tab"
							data-bs-toggle="tab" data-bs-target="#joined-schedule"
							type="button" role="tab" aria-controls="joined-schedule"
							aria-selected="false">참여 중인 일정</button>
					</li>
				</ul>

				<div class="tab-content mt-3" id="scheduleTabsContent">

					<div class="tab-pane fade show active" id="my-schedule"
						role="tabpanel" aria-labelledby="my-schedule-tab">
						<table class="table table-bordered align-middle">
							<thead class="table-warning">
								<tr>
									<th>제목</th>
									<th>시작일</th>
									<th>종료일</th>
									<th>지역</th>
									<th>등록일</th>
									<th>공개여부</th>
								</tr>
							</thead>
							<tbody>
								<%
								if (!myScheduleList.isEmpty()) {
								%>
								<%
								for (ScheduleDTO schedule : myScheduleList) {
								%>
								<tr data-schedule-id="<%=schedule.getId()%>"
									data-title="<%=schedule.getTitle()%>"
									data-start="<%=schedule.getStartDate()%>"
									data-end="<%=schedule.getEndDate()%>"
									data-location="<%=schedule.getLocation() != null ? schedule.getLocation() : ""%>"
									data-desc="<%=schedule.getDescription() != null ? schedule.getDescription() : ""%>">
									<td><%=schedule.getTitle()%></td>
									<td><%=schedule.getStartDate()%></td>
									<td><%=schedule.getEndDate() != null ? schedule.getEndDate() : "-"%></td>
									<td><%=schedule.getLocation() != null ? schedule.getLocation() : "-"%></td>
									<td><%=schedule.getCreatedAt()%></td>
									<td><%="Y".equals(schedule.getVisibility()) ? "공개" : "비공개"%></td>
								</tr>
								<%
								}
								%>
								<%
								} else {
								%>
								<tr>
									<td colspan="6" class="text-center text-muted">등록된 여행 일정이
										없습니다.</td>
								</tr>
								<%
								}
								%>
							</tbody>
						</table>
					</div>

					<div class="tab-pane fade" id="joined-schedule" role="tabpanel"
						aria-labelledby="joined-schedule-tab">
						<table class="table table-bordered align-middle">
							<thead class="table-primary">
								<tr>
									<th>제목</th>
									<th>시작일</th>
									<th>종료일</th>
									<th>지역</th>
									<th>작성자</th>
								</tr>
							</thead>
							<tbody>
								<%
								if (!joinedScheduleList.isEmpty()) {
								%>
								<%
								for (ScheduleDTO schedule : joinedScheduleList) {
								%>
								<tr data-schedule-id="<%=schedule.getId()%>"
									data-title="<%=schedule.getTitle()%>"
									data-start="<%=schedule.getStartDate()%>"
									data-end="<%=schedule.getEndDate()%>"
									data-location="<%=schedule.getLocation() != null ? schedule.getLocation() : ""%>"
									data-desc="<%=schedule.getDescription() != null ? schedule.getDescription() : ""%>">
									<td><%=schedule.getTitle()%></td>
									<td><%=schedule.getStartDate()%></td>
									<td><%=schedule.getEndDate() != null ? schedule.getEndDate() : "-"%></td>
									<td><%=schedule.getLocation() != null ? schedule.getLocation() : "-"%></td>
									<td><%=schedule.getUserId()%></td>
								</tr>
								<%
								}
								%>
								<%
								} else {
								%>
								<tr>
									<td colspan="5" class="text-center text-muted">참여 중인 여행
										일정이 없습니다.</td>
								</tr>
								<%
								}
								%>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</main>
	</div>

	<%@ include file="../footer.jsp"%>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

	<script>
		// JSON 데이터를 JavaScript 전역 변수에 저장하여 외부 JS 파일에서 접근 가능하게 합니다.
		window.jsonEventsData =
	<%=jsonEvents.toString()%>
		;
	</script>

	<script src="js/travel_schedule.js"></script>

	<div class="modal fade" id="eventModal" tabindex="-1"
		aria-labelledby="eventModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header bg-warning text-dark">
					<h5 class="modal-title" id="eventModalLabel">일정 상세보기</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="닫기"></button>
				</div>
				<div class="modal-body">
					<p>
						<strong>제목:</strong> <span id="modalTitle"></span>
					</p>
					<p>
						<strong>시작일:</strong> <span id="modalStart"></span>
					</p>
					<p>
						<strong>종료일:</strong> <span id="modalEnd"></span>
					</p>
					<p>
						<strong>지역:</strong> <span id="modalLocation"></span>
					</p>
					<p>
						<strong>설명:</strong> <span id="modalDesc"></span>
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>

</body>
</html>