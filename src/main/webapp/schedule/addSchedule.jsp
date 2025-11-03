<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일정 추가</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
<link rel="stylesheet" href="./css/schedule.css">
<script type="text/javascript" src="./js/schedule.js" defer="defer"></script>
<script type="text/javascript" src="./js/search.js" defer="defer"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/jquery/latest/jquery.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet" />

</head>
<body>
	<%@ include file="../header.jsp" %>
	
	<div class="container">
		<div class="row justify-content-center"> 
			<div class="col-md-8 col-lg-7" >
				<div class="d-flex justify-content-between align-items-center mb-4">
					<h2>일정 추가</h2>
				</div>
			<div class="formCss">
				<form action="${pageContext.request.contextPath}/processAddSchedule" method="post" enctype="multipart/form-data">
					<div class="visibility">
						<label>
							<input type="checkbox" id="visibility" name="visibility" value="Y">
							<span id="visibilityText" class="material-symbols-outlined">lock_open_right</span>
						</label>
					</div>
					<div class="mb-3 row justify-content-center">
						<label class="col-sm-2 ">일정 제목</label>
						<div class="col-sm-5">
							<input type="text" id="title" name="title" class="form-control">
						</div>
					</div>
					<div class="mb-3 row g-3 justify-content-center">
						<label class="col-sm-2">여행 날짜</label>
						<div class="col-sm-5">
							<input type="text" id="demo" name="demo" value="" class="form-control" />
						</div>
					</div>
					<div class="mb-3 row mt-3 justify-content-center">
						<label class="col-sm-2">여행 지역</label>
						<div class="col-sm-5">
							<input type="text" id="location" name="location" class="form-control">
						</div>
					</div>
					<div class="mb-3 row justify-content-center">
						<label class="col-sm-2">여행 일정</label>
						<div class="col-sm-5">
							<input type="text" id="description" name="description" class="form-control">
						</div>
					</div>
					<div class="mb-3 row justify-content-center">
						<label class="col-sm-2">이미지</label>
						<div class="col-sm-5">
							<input type="file" id="mainImage" name="mainImage" class="form-control">
						</div>				
					</div>
					<div class="mb-3 row justify-content-center" >
						<label class="col-sm-2">여행친구</label>
						<div class="col-sm-5 d-flex align-items-center gap-2">
							<input type="text" id="member" name="member" class="form-control">
							<button type="button" class="btn btn-primary" id="addButton">+</button>
							<div id="suggestionsBox"></div>
						</div>	
					</div>
					<div class="mb-3 row justify-content-center" >
						<label class="col-sm-2"></label>
            <div class="col-sm-5 d-flex flex-wrap gap-2" id="userList"></div>
					</div>													
					<div class="mb-3 row justify-content-center" id="btn_submit">
						<div class="col-sm-7 text-end">
							<button type="submit" class="btn btn-primary" onclick="checkAddTrip(event)" >등록</button>
							<a href="../index.jsp" class="btn btn-secondary" role="button">취소</a>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
	
<%@ include file="../footer.jsp" %>
</body>
</html>




