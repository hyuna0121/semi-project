<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일정 추가</title>
    	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
	
    <script type="text/javascript" src="./js/schedule.js" defer="defer"></script>
    <link rel="stylesheet" href="./css/schedule.css">
</head>
<body>
	<h2>일정 추가</h2>
	<div class="visibility">
		<label>
			<input type="checkbox" id="visibility" name="visibility">
			<span id="visibilityText">공개</span>
		</label>
	</div>
	<form action="Schedule_main_process.jsp" method="post" enctype="multipart/form-data">
    <div class="mb-3 row">
		    <label class="col-sm-2">일정 제목</label>
			<div class="col-sm-3">
				<input type="text" id="title" name="title" class="form-control">
			</div>
		</div>
      <div class="mb-3 row g-3">
				<label class="col-sm-2">여행 시작일</label>
				<div class="col-sm-3">
					<input type='date' name="startDate" class="form-control">
				</div>
				<label class="col-sm-2">여행 종료일</label>
				<div class="col-sm-3">
					<input type='date' name="endDate" class="form-control">
				</div>
			</div>

			<div class="mb-3 row mt-3">
				<label class="col-sm-2">여행 지역</label>
				<div class="col-sm-3">
					<input type="text" id="location" name="location" class="form-control">
				</div>
			</div>

        <div class="mb-3 row">
		    <label class="col-sm-2">여행 일정</label>
			<div class="col-sm-3">
				<input type="text" id="description" name="description" class="form-control">
			</div>
		</div>
		<div class="mb-3 row">
					<label class="col-sm-2">이미지</label>
					<div class="col-sm-5">
						<input type="file" name="main_image" class="form-control">
					</div>				
				</div>
		<div class="mb-3 row" id="btn_submit">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="submit" class="btn btn-primary" onclick="checkAddTrip(event)" >등록</button>
			</div>
		</div>
    </form>
</body>
</html>