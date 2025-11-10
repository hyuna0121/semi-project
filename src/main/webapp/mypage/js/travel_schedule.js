// travel_schedule.js

document.addEventListener('DOMContentLoaded', function() {
    
    // JSP에서 선언된 전역 컨텍스트 경로 변수를 사용합니다.
    const CONTEXT_PATH = window.CTX; 
    
    // 🚨 JSP에서 전역 변수 window.jsonEventsData에 JSON 배열이 저장되어 있다고 가정합니다.
    let eventsData = [];
    const jsonString = window.jsonEventsData; 
    
    // 🚨🚨🚨 상세 정보 조회 URL 정의 (오타 수정됨) 🚨🚨🚨
    const DETAIL_URL = CONTEXT_PATH + '/schedule/details'; 


    if (jsonString && typeof jsonString === 'string') {
        try {
            // JSON 문자열을 객체로 파싱 시도. 실패 시 catch 블록으로 이동.
            eventsData = JSON.parse(jsonString);
        } catch (e) {
            console.error('FullCalendar JSON 파싱 오류: 데이터가 유효하지 않습니다.', e);
        }
    } else if (Array.isArray(jsonString)) {
        eventsData = jsonString;
    }
    
    // 🚨🚨🚨 모달 관련 요소 정의 🚨🚨🚨
    const modalElement = document.getElementById('eventModal'); // 모달 요소 자체
    const modal = new bootstrap.Modal(modalElement);
    const titleEl = document.getElementById('modalTitle');
    const startEl = document.getElementById('modalStart');
    const endEl = document.getElementById('modalEnd');
    const locationEl = document.getElementById('modalLocation');
    const descEl = document.getElementById('modalDesc');
    const detailListArea = document.getElementById('detailedScheduleList'); // 상세 일정 목록 영역
    
    // 🚨🚨🚨 [핵심]: 상세 일정 보기 버튼 요소 (DOMContentLoaded 시점에 찾음) 🚨🚨🚨
    const goToDetailsBtn = document.getElementById('goToDetailsBtn');


    // 🚨🚨🚨 [최종 해결 로직]: 모달이 완전히 표시된 후 리스너를 붙여 안정성 확보 🚨🚨🚨
    modalElement.addEventListener('shown.bs.modal', function () {
        
        // 이전에 리스너가 연결되지 않았을 때만 연결하여 중복 방지
        if (goToDetailsBtn && !goToDetailsBtn.hasAttribute('data-listener-attached')) {
            goToDetailsBtn.addEventListener('click', function() {
                const scheduleId = this.getAttribute('data-schedule-id');
                if (scheduleId) {
                    // 페이지 이동
                    window.location.href = `${CONTEXT_PATH}/schedule/schedule.jsp?schedule_id=${scheduleId}`;
                } else {
                    console.warn('일정 ID를 가져올 수 없습니다.');
                }
            });
            // 리스너가 연결되었음을 표시
            goToDetailsBtn.setAttribute('data-listener-attached', 'true');
        }
    });

    
    // 🔸 캘린더 설정
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: 'dayGridMonth',
      locale: 'ko',
      height: 650,
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        // 🚨🚨🚨 [수정]: 'timeGridWeek'와 'listWeek' 제거 🚨🚨🚨
        right: 'dayGridMonth,listMonth' 
      },
      events: eventsData,

      // 🚨 캘린더 이벤트 클릭 시 모달 표시 및 상세 정보 로드
      eventClick: function(info) {
        const scheduleId = info.event.id;
        
        // 🚨🚨🚨 [ID 유효성 체크]: scheduleId가 없으면 경고 후 조용히 종료 🚨🚨🚨
        if (!scheduleId) {
             console.warn("경고: FullCalendar 초기 이벤트에서 유효한 일정 ID를 가져올 수 없습니다. 작업을 건너뜁니다.");
             return; 
        }

        // 1. 기본 정보 표시
        titleEl.textContent = info.event.title;
        startEl.textContent = moment(info.event.start).format('YYYY-MM-DD'); 

        // 🚨🚨🚨 [종료일 -1일 처리] 🚨🚨🚨
        let displayEndDate = '';
        if (info.event.end) {
            // FullCalendar의 end 날짜에서 하루를 뺌
            displayEndDate = moment(info.event.end).subtract(1, 'days').format('YYYY-MM-DD');
        } else {
            // 종료일이 없는 경우 (하루짜리 일정) 시작일을 표시
            displayEndDate = moment(info.event.start).format('YYYY-MM-DD');
        }
        endEl.textContent = displayEndDate; 
        
        locationEl.textContent = info.event.extendedProps.location || "-";
        descEl.textContent = info.event.extendedProps.description || "-";
        
        // 🚨 [핵심]: 버튼에 scheduleId 설정
        if (goToDetailsBtn) {
            goToDetailsBtn.setAttribute('data-schedule-id', scheduleId);
        }

        
        // 2. AJAX로 상세 일정 조회
        fetchDetails(scheduleId);
        
        modal.show(); // 모달 띄우기
      }
    });
    
    calendar.render(); // 캘린더를 화면에 렌더링

    // 🔸 테이블 클릭 시 모달 표시 및 상세 정보 로드
    document.querySelectorAll('tbody tr').forEach(row => {
      row.style.cursor = 'pointer'; 
      
      row.addEventListener('click', () => {
        const scheduleId = row.dataset.scheduleId; 

        // 🚨🚨🚨 [ID 유효성 체크]: scheduleId가 없으면 경고 후 조용히 종료 🚨🚨🚨
        if (!scheduleId) {
             console.warn("경고: 테이블 이벤트에서 유효한 일정 ID를 가져올 수 없습니다. 작업을 건너뜁니다.");
             return; 
        }

        // 1. 기본 정보 표시
        titleEl.textContent = row.dataset.title;
        startEl.textContent = row.dataset.start;
        
        // 테이블 데이터는 원본이므로, 그대로 표시
        endEl.textContent = row.dataset.end && row.dataset.end !== '-' ? row.dataset.end : row.dataset.start; 
        
        locationEl.textContent = row.dataset.location;
        descEl.textContent = row.dataset.desc;
        
        // 🚨 [핵심]: 버튼에 scheduleId 설정
        if (goToDetailsBtn) {
            goToDetailsBtn.setAttribute('data-schedule-id', scheduleId);
        }
        
        // 2. AJAX로 상세 일정 조회
        fetchDetails(scheduleId);
        
        modal.show(); // 모달 띄우기
      });
    });
    
    // 🚨🚨🚨 [핵심 함수 1] 일정 상세 정보 조회 및 렌더링 🚨🚨🚨
    function fetchDetails(id) {
        detailListArea.innerHTML = '<p class="text-center text-muted mt-4">상세 정보를 불러오는 중입니다...</p>';
        
        // Fetch API 호출 (GET 요청)
        fetch(`${DETAIL_URL}?id=${id}`) 
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(groupedData => {
                // data는 DetailService에서 그룹화된 Map<DayString, List<DetailDTO>> 형태를 가정
                detailListArea.innerHTML = renderGroupedDetails(groupedData);
            })
            .catch(error => {
                detailListArea.innerHTML = `<p class="text-danger mt-4">일정 상세 정보 로드 실패. (서버 콘솔 확인)</p>`;
                console.error('Fetching Schedule Details Error:', error);
            });
    }

    // 🚨🚨🚨 [핵심 함수 2] HTML 렌더링 함수 (Map 데이터를 HTML로 변환) 🚨🚨🚨
    function renderGroupedDetails(groupedData) {
        let html = '<div class="list-group">';
        
        if (Object.keys(groupedData).length === 0) {
            return '<p class="text-center text-muted mt-4">등록된 세부 일정이 없습니다.</p>';
        }

        for (const dayCount in groupedData) {
            const detailsList = groupedData[dayCount];
            
            // Day 1, Day 2 형식의 헤더
            html += `<div class="list-group-item list-group-item-action active bg-info text-white mt-3">
                        <h6 class="mb-0">Day ${dayCount}</h6>
                    </div>`;

            if (detailsList.length === 0) {
                 html += `<div class="list-group-item">세부 일정 없음</div>`;
            } else {
                detailsList.forEach(detail => {
                    html += `<div class="list-group-item d-flex justify-content-between align-items-center">
                                <div>
                                    <strong class="text-primary">${detail.place}</strong>
                                    <small class="text-muted"> (${detail.category})</small><br>
                                    <small>${detail.memo || ''}</small>
                                </div>
                                <span class="badge bg-secondary rounded-pill">${detail.startTime}</span>
                             </div>`;
                });
            }
        }
        html += '</div>';
        return html;
    }
});