<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>일정 추가</title>

  <!-- Bootstrap 5 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <!-- Material Symbols (아이콘 글꼴) -->
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght@400" rel="stylesheet"/>
  <!-- daterangepicker -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css"/>

  <!-- 페이지 전용 스타일 -->
  <link rel="stylesheet" href="<%=ctx%>/schedule/css/schedule.css"/>
</head>
<body class="auth-like-bg">

  <main class="container min-vh-100 d-flex align-items-center justify-content-center py-5">
    <div class="schedule-card shadow-lg">
      <h1 class="schedule-title">일정 추가</h1>

      <form action="<%=ctx%>/processAddSchedule" method="post" enctype="multipart/form-data" class="mt-4">
		  <!-- 제목 -->
		  <div class="mb-3">
		    <label for="title" class="form-label">제목</label>
		    <input type="text" id="title" name="title" class="form-control form-control-lg soft-input" required>
		  </div>
		
		  <!-- 지역 -->
		  <div class="mb-3">
		    <label for="location" class="form-label">지역</label>
		    <input type="text" id="location" name="location" class="form-control form-control-lg soft-input" required>
		  </div>
		
		  <!-- 기간(보이는 창) -->
		  <div class="mb-3">
		    <label for="demoView" class="form-label">여행 기간</label>
		    <input type="text" id="demoView" class="form-control form-control-lg soft-input" placeholder="기간을 선택하세요" autocomplete="off" required>
		    <!-- ✅ 서블릿이 읽는 실제 값 -->
		    <input type="hidden" id="demoHidden" name="demo">
		    <!-- (옵션) 분해해서 쓰고 싶으면 -->
		    <input type="hidden" id="startDate" name="startDate">
		    <input type="hidden" id="endDate" name="endDate">
		  </div>
		
		  <!-- 공개/비공개 (기본: 공개 = 체크 해제 상태) -->
		  <div class="mb-3">
		    <label class="form-label d-block">공개 설정</label>
		    <label class="toggle-label">
		      <!-- ✅ 체크되면 파라미터 존재 → 비공개(N), 해제되면 파라미터 없음 → 공개(Y) -->
		      <input type="checkbox" id="visibility" name="visibility" value="N">
		      <span class="material-symbols-outlined" id="visibilityIcon">lock_open_right</span>
		      <span class="toggle-text ms-2" id="visibilityText">공개</span>
		    </label>
		  </div>
	
	  <!-- 설명 -->
	  <div class="mb-3">
	    <label for="description" class="form-label">설명</label>
	    <textarea id="description" name="description" rows="4" class="form-control soft-input"></textarea>
	  </div>
	
	  <!-- 대표 이미지 -->
	  <div class="mb-4">
	    <label for="mainImage" class="form-label">대표 이미지</label>
	    <input class="form-control form-control-lg soft-input" type="file" id="mainImage" name="mainImage" accept="image/*">
	  </div>
	
	  <!-- 동행인 추가 영역 -->
	  <div class="companion-section">
	    <label for="companionInput">동행인 추가</label>
	    <div class="companion-row">
	      <input type="text" id="companionInput" placeholder="아이디 또는 닉네임을 입력" />
	      <button type="button" id="companionAddBtn">추가</button>
	    </div>
	    <p id="companionMsg" class="companion-msg" style="display:none;"></p>
	
	    <!-- 추가된 동행인 리스트가 여기에 쌓임 -->
	    <ul id="companionList" class="companion-list"></ul>
	  </div>
	
	  <div class="d-flex gap-2">
	    <button type="submit" class="btn btn-dark btn-lg flex-grow-1">저장</button>
	    <a href="../mainpage/mainpage.jsp" class="btn btn-outline-secondary btn-lg flex-grow-1">취소</a>

	  </div>
	</form>


      <div class="text-center mt-3 small text-muted">
        목록으로 돌아가려면 상단 메뉴를 이용하세요
      </div>
    </div>
  </main>

  <!-- deps -->
  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/moment@2.29.4/moment.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>

  <!-- 페이지 전용 스크립트 -->
  <script>window.CTX = '<%=request.getContextPath()%>';</script>
  <script src="<%=request.getContextPath()%>/schedule/js/schedule.js"></script>
</body>
</html>
