<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>여행 일정 | 마이페이지</title>

<!-- ✅ Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

<!-- ✅ FullCalendar -->
<link href='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/main.min.css' rel='stylesheet' />
<script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js'></script>

<style>
  body {
    background-color: #f8f9fa;
  }
  .main-container {
    display: flex;
    min-height: calc(100vh - 60px);
  }
  .sidebar {
    width: 250px;
    background-color: #343a40;
    color: #fff;
    padding: 20px;
  }
  .sidebar h5 {
    color: #ffc107;
    text-align: center;
    margin-bottom: 30px;
  }
  .sidebar a {
    display: block;
    color: #fff;
    padding: 10px 15px;
    border-radius: 8px;
    text-decoration: none;
    margin-bottom: 8px;
    transition: background 0.2s;
  }
  .sidebar a:hover, .sidebar a.active {
    background-color: #ffc107;
    color: #343a40;
  }
  .content {
    flex: 1;
    padding: 40px;
    background-color: #fff;
  }
  #calendar {
    background: #fff;
    border-radius: 10px;
    padding: 10px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    min-height: 650px;
  }
  tr:hover { cursor: pointer; }
</style>
</head>
<body>

<!-- ✅ 헤더 JSP 포함 -->
<%@ include file="../header.jsp" %>

<div class="main-container">
  <!-- 왼쪽 사이드바 -->
  <aside class="sidebar">
    <h5>My Page</h5>
    <a href="mypage_profile.jsp">내 프로필</a>
    <a href="#" class="active">여행 일정</a>
    <a href="#">내 댓글</a>
    
  </aside>

  <!-- 오른쪽 콘텐츠 -->
  <main class="content">
    <div class="container-fluid">
      <h3 class="mb-3 border-bottom pb-2">📆 여행 일정</h3>
      <div id="calendar"></div>

      <!-- 📑 탭 메뉴 -->
      <ul class="nav nav-tabs mt-5" id="scheduleTabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button class="nav-link active" id="my-schedule-tab" data-bs-toggle="tab" data-bs-target="#my-schedule" type="button" role="tab" aria-controls="my-schedule" aria-selected="true">
            내가 만든 일정
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="joined-schedule-tab" data-bs-toggle="tab" data-bs-target="#joined-schedule" type="button" role="tab" aria-controls="joined-schedule" aria-selected="false">
            참여 중인 일정
          </button>
        </li>
      </ul>

      <!-- 📋 탭 내용 -->
      <div class="tab-content mt-3" id="scheduleTabsContent">

        <!-- 내가 만든 일정 -->
        <div class="tab-pane fade show active" id="my-schedule" role="tabpanel" aria-labelledby="my-schedule-tab">
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
              <tr data-title="제주도 힐링 여행" data-start="2025-10-10" data-end="2025-10-13" data-location="제주" data-desc="힐링 여행 일정입니다.">
                <td>제주도 힐링 여행</td>
                <td>2025-10-10</td>
                <td>2025-10-13</td>
                <td>제주</td>
                <td>2025-09-28</td>
                <td>공개</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 참여 중인 일정 -->
        <div class="tab-pane fade" id="joined-schedule" role="tabpanel" aria-labelledby="joined-schedule-tab">
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
              <tr data-title="서울 나들이" data-start="2025-11-02" data-end="2025-11-03" data-location="서울" data-desc="경복궁, 한강 피크닉 일정입니다.">
                <td>서울 나들이</td>
                <td>2025-11-02</td>
                <td>2025-11-03</td>
                <td>서울</td>
                <td>user01</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </main>
</div>

<!-- ✅ 푸터 JSP 포함 -->
<%@ include file="../footer.jsp" %>

<!-- ✅ Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- ✅ FullCalendar 동작 -->
<script>
  document.addEventListener('DOMContentLoaded', function() {
    const modal = new bootstrap.Modal(document.getElementById('eventModal'));
    const titleEl = document.getElementById('modalTitle');
    const startEl = document.getElementById('modalStart');
    const endEl = document.getElementById('modalEnd');
    const locationEl = document.getElementById('modalLocation');
    const descEl = document.getElementById('modalDesc');

    // 🔸 캘린더 설정
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: 'dayGridMonth',
      locale: 'ko',
      height: 650,
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,listWeek'
      },
      events: [
        { title: '제주도 힐링 여행', start: '2025-10-10', end: '2025-10-13', color: '#ffc107', location: '제주', description: '힐링 여행 일정입니다.' },
        { title: '서울 나들이', start: '2025-11-02', end: '2025-11-03', color: '#0d6efd', location: '서울', description: '경복궁, 한강 피크닉 일정입니다.' }
      ],
      eventClick: function(info) {
        titleEl.textContent = info.event.title;
        startEl.textContent = info.event.startStr;
        endEl.textContent = info.event.endStr || "당일 일정";
        locationEl.textContent = info.event.extendedProps.location || "-";
        descEl.textContent = info.event.extendedProps.description || "-";
        modal.show();
      }
    });
    calendar.render();

    // 🔸 테이블 클릭 시 모달 표시
    document.querySelectorAll('tbody tr').forEach(row => {
      row.addEventListener('click', () => {
        titleEl.textContent = row.dataset.title;
        startEl.textContent = row.dataset.start;
        endEl.textContent = row.dataset.end;
        locationEl.textContent = row.dataset.location;
        descEl.textContent = row.dataset.desc;
        modal.show();
      });
    });
  });
</script>

<!-- 🔹 일정 상세보기 모달 -->
<div class="modal fade" id="eventModal" tabindex="-1" aria-labelledby="eventModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header bg-warning text-dark">
        <h5 class="modal-title" id="eventModalLabel">일정 상세보기</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
      </div>
      <div class="modal-body">
        <p><strong>제목:</strong> <span id="modalTitle"></span></p>
        <p><strong>시작일:</strong> <span id="modalStart"></span></p>
        <p><strong>종료일:</strong> <span id="modalEnd"></span></p>
        <p><strong>지역:</strong> <span id="modalLocation"></span></p>
        <p><strong>설명:</strong> <span id="modalDesc"></span></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>

</body>
</html>