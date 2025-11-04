<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>커뮤니티 게시판</title>
<link rel="stylesheet" href="./css/board.css">
<script type="text/javascript" src="./js/showMore.js" defer="defer"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet" />

</head>
<body>
	<%@ include file="../header.jsp" %>
	<div class="container">
		<div class="tripMainImage">
			<img alt="tripMainImage" src="./images/exam1.jpg">
		</div>
		<h1 style="margin-left: 100px;"><c:out value="${selectedSchedule.title}" /></h1>
		<div class="tripTitle">
			<h3>일정 선택</h3>
			<p>일정에 대한 의견을 자유롭게 나눠요😊</p>
		</div>
		<div class="communityMain">

		
			<div class="tripSchedule">
				<h2>일정 내용</h2>
				
				<c:if test="${not empty selectedSchedule}">
					<p>제목</p>
					<div class="dbContents">
						<span><c:out value="${selectedSchedule.title}" /></span>
					</div>
					<p>기간</p>
					<div class="dbContents">
						<span><c:out value="${selectedSchedule.startDate}" /> ~ <c:out value="${selectedSchedule.endDate}" /></span>
					</div>
					<p>지역</p>
					<div class="dbContents">
						<span><c:out value="${selectedSchedule.location}" /></span>
					</div>
					<p>여행일정</p>
					<div class="dbContents">
						<c:if test="${not empty selectedSchedule.description}">
							<span><c:out value="${selectedSchedule.description}" /></span>
						</c:if>
						<c:if test="${empty selectedSchedule.description}">
							<span>작성된 메모가 없습니다.</span>
						</c:if>
					</div>
					<p>인원 수</p>
					<div class="dbContents">
						<span><c:out value="${fn:length(selectedSchedule.travelBuddies)}" />명</span>
					</div>
					<p>동행인 아이디</p>
					<div class="dbContents">

						<c:forEach var="buddy" items="${selectedSchedule.travelBuddies}">
							<span><c:out value="${buddy}" /></span> <br>
						</c:forEach>
					</div>
					<p>메모</p>
					<div class="dbContents">
						
					</div>
				</c:if>
				
				<c:if test="${empty selectedSchedule}">
					<p>표시할 일정이 없습니다.</p>
				</c:if>
			</div>
			
			
			
			
			
			
			
			
			
			
			<div class="tripCommunity">
				<h2>여행 노트</h2>
				<%@include file="commentList.jsp" %>
			</div>
		</div>	
	</div>
	
	
	
	
	
	
	
	
	
	
	
	
	
	<div id="tripModal">
	    <div class="modal-content">
	    	<div class="modal-header"> 일정 목록
		        <span class="material-symbols-outlined btn-close">close</span>
	    	</div>
	      	<div class="modal-body">
	      		<ul>
	      			<c:forEach var="schedule" items="${userSchedules}">
	      				<li>
		      				<a href="${pageContext.request.contextPath}/community/board?id=${schedule.id}">
                            <c:out value="${schedule.title}" />
	      				</li>     			
	      			</c:forEach>
	      			<c:if test="${empty userSchedules}">
                        <li><p>일정이 없습니다.</p></li>
                    </c:if>
	      		</ul>
	      	</div>
	    </div>
  	</div>

  	<div id="imageModal">
    	<div class="modal-content">
      		<div class="modal-header">
        		이미지
        		<span class="material-symbols-outlined btn-close">close</span>
      		</div>
    	</div>
  	</div>
	<%@ include file="../footer.jsp" %>
</body>
</html>