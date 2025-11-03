<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
		<div class="tripTitle">
			<h3>일정 선택</h3>
			<p>일정에 대한 의견을 자유롭게 나눠요😊</p>
			<div class="tripList">
				<a href="#">
					<img alt="exam2" src="./images/exam2.jpg">
					<p>일정 제목</p>
				</a>
				<a href="#">
					<img alt="exam3" src="./images/exam3.jpg">
					<p>일정 제목</p>
				</a>
				<a href="#">
					<img alt="exam4" src="./images/exam4.jpg">
					<p>일정 제목</p>
				</a>
				<a href="#">
					<img alt="exam5" src="./images/exam5.jpg">
					<p>일정 제목</p>
				</a>
			</div>
			<div class="tripShowMore">
				<button type="button" class="showMore">더보기</button>
			</div>
		</div>
		<div class="communityMain">
			<div class="tripSchedule">
				<h2>일정 내용</h2>
				<p>제목</p>
				<div class="dbContents">
					<span>샘플 여행 제목</span>
				</div>
				<p>기간</p>
				<div class="dbContents">
					<span>샘플 여행 기간</span>
				</div>
				<p>지역</p>
				<div class="dbContents">
					<span>샘플 여행 지역</span>
				</div>
				<p>인원 수</p>
				<div class="dbContents">
					<span>2명(샘플)</span>
				</div>
				<p>동행인 아이디</p>
				<div class="dbContents">
					<span>샘플 동행인 아이디 1</span> <br>
					<span>샘플 동행인 아이디 2</span>
				</div>
				<p>메모</p>
				<div class="dbContents">
					<span>입력된 메모 내용</span>
				</div>
			</div>
			<div class="tripCommunity">
				<h2>여행 노트</h2>
			</div>
		</div>	
	</div>
	<div id="modal">
	    <div class="modal-content">
	    	<div class="modal-header"> 일정 목록
		        <span class="material-symbols-outlined btn-close">close</span>
	    	</div>
	      	<div class="modal-body">
	      		<ul>
	      			<li><a href="#">샘플 일정 제목 1</a></li>
	      			<li><a href="#">샘플 일정 제목 2</a></li>
	      			<li><a href="#">샘플 일정 제목 3</a></li>
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