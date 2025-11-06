// travel_schedule.js

document.addEventListener('DOMContentLoaded', function() {
    
    // 🚨 JSP에서 전역 변수 window.jsonEventsData에 JSON 배열이 저장되어 있다고 가정합니다.
    const eventsData = window.jsonEventsData || [];

    // 모달 관련 요소 정의
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
      
      // 🚨🚨🚨 JSP에서 가져온 DB 데이터를 사용 🚨🚨🚨
      events: eventsData,

      eventClick: function(info) {
        // 캘린더 이벤트 클릭 시 모달 데이터 채우기
        titleEl.textContent = info.event.title;
        startEl.textContent = info.event.startStr;
        
        // FullCalendar는 종료일을 하루 뒤로 설정하여 전달하므로, 다일 일정인 경우 endStr이 존재합니다.
        endEl.textContent = info.event.endStr ? info.event.endStr : "당일 일정";
        
        // extendedProps에서 추가 정보 가져오기
        locationEl.textContent = info.event.extendedProps.location || "-";
        descEl.textContent = info.event.extendedProps.description || "-";
        
        modal.show();
      }
    });
    
    calendar.render();

    // 🔸 테이블 클릭 시 모달 표시 (DB 데이터의 data-* 속성 사용)
    document.querySelectorAll('tbody tr').forEach(row => {
      row.addEventListener('click', () => {
        titleEl.textContent = row.dataset.title;
        startEl.textContent = row.dataset.start;
        // 종료일이 "-"일 경우 시작일로 대체
        endEl.textContent = row.dataset.end !== '-' ? row.dataset.end : row.dataset.start; 
        locationEl.textContent = row.dataset.location;
        descEl.textContent = row.dataset.desc;
        modal.show();
      });
    });
});