document.addEventListener('DOMContentLoaded', function() {
    
    const CONTEXT_PATH = window.CTX; 
    let eventsData = [];
    const jsonString = window.jsonEventsData; 
    const DETAIL_URL = CONTEXT_PATH + '/schedule/details'; 


    if (jsonString && typeof jsonString === 'string') {
        try {
            eventsData = JSON.parse(jsonString);
        } catch (e) {
            console.error('FullCalendar JSON 파싱 오류: 데이터가 유효하지 않습니다.', e);
        }
    } else if (Array.isArray(jsonString)) {
        eventsData = jsonString;
    }
    
    const modalElement = document.getElementById('eventModal');
    const modal = new bootstrap.Modal(modalElement);
    const titleEl = document.getElementById('modalTitle');
    const startEl = document.getElementById('modalStart');
    const endEl = document.getElementById('modalEnd');
    const locationEl = document.getElementById('modalLocation');
    const descEl = document.getElementById('modalDesc');
    
    const scheduleTabs = document.getElementById('scheduleTabs');
    const scheduleTabContent = document.getElementById('scheduleTabContent'); 
    
    const goToDetailsBtn = document.getElementById('goToDetailsBtn');


    modalElement.addEventListener('shown.bs.modal', function () {
        if (goToDetailsBtn && !goToDetailsBtn.hasAttribute('data-listener-attached')) {
            goToDetailsBtn.addEventListener('click', function() {
                const scheduleId = this.getAttribute('data-schedule-id');
                if (scheduleId) {
                    window.location.href = `${CONTEXT_PATH}/schedule/schedule.jsp?schedule_id=${scheduleId}`;
                } else {
                    console.warn('일정 ID를 가져올 수 없습니다.');
                }
            });
            goToDetailsBtn.setAttribute('data-listener-attached', 'true');
        }
    });

    
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: 'dayGridMonth',
      locale: 'ko',
      height: 650,
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,listMonth' 
      },
      events: eventsData,

      eventClick: function(info) {
        const scheduleId = info.event.id;
        if (!scheduleId) { console.warn("ID 없음."); return; }

        titleEl.textContent = info.event.title;
        startEl.textContent = moment(info.event.start).format('YYYY-MM-DD'); 
        let displayEndDate = info.event.end ? moment(info.event.end).subtract(1, 'days').format('YYYY-MM-DD') : moment(info.event.start).format('YYYY-MM-DD');
        endEl.textContent = displayEndDate; 
        
        locationEl.textContent = info.event.extendedProps.location || "-";
        descEl.textContent = info.event.extendedProps.description || "-";
        
        if (goToDetailsBtn) { goToDetailsBtn.setAttribute('data-schedule-id', scheduleId); }
        
        fetchDetails(scheduleId);
        modal.show(); 
      }
    });
    
    calendar.render(); 

    document.querySelectorAll('tbody tr').forEach(row => {
      row.style.cursor = 'pointer'; 
      row.addEventListener('click', () => {
        const scheduleId = row.dataset.scheduleId; 
        if (!scheduleId) { console.warn("ID 없음."); return; }

        titleEl.textContent = row.dataset.title;
        startEl.textContent = row.dataset.start;
        endEl.textContent = row.dataset.end && row.dataset.end !== '-' ? row.dataset.end : row.dataset.start; 
        locationEl.textContent = row.dataset.location;
        descEl.textContent = row.dataset.desc;
        
        if (goToDetailsBtn) { goToDetailsBtn.setAttribute('data-schedule-id', scheduleId); }
        
        fetchDetails(scheduleId);
        modal.show(); 
      });
    });
    
    function fetchDetails(scheduleId) {
        if (!scheduleTabs || !scheduleTabContent) {
            console.error("오류: scheduleTabs 또는 scheduleTabContent 요소를 찾을 수 없습니다.");
            return;
        }

        scheduleTabs.innerHTML = '';
        scheduleTabContent.innerHTML = '<p class="text-center text-muted mt-4">세부 일정 로딩 중...</p>';
        
        fetch(`${DETAIL_URL}?id=${scheduleId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(groupedDetails => {
                renderTabStructure(groupedDetails);
            })
            .catch(error => {
                console.error('Error fetching schedule details:', error);
                scheduleTabContent.innerHTML = '<p class="text-center text-danger mt-4">세부 일정을 불러오는 데 실패했습니다. (서버 콘솔 확인)</p>';
            });
    }


    function renderTabStructure(groupedData) {
        
        if (!scheduleTabs || !scheduleTabContent) {
            console.error("오류: renderTabStructure 함수 내에서 scheduleTabs 또는 scheduleTabContent 요소를 찾을 수 없습니다.");
            return;
        }

        scheduleTabs.innerHTML = '';
        scheduleTabContent.innerHTML = ''; 
        
        const dayKeys = Object.keys(groupedData);

        if (dayKeys.length === 0) {
            scheduleTabContent.innerHTML = '<p class="text-center text-muted mt-4">등록된 세부 일정이 없습니다.</p>';
            return;
        }

        dayKeys.forEach((dayCount, index) => {
            const isActive = index === 0;
            const tabId = `day-${dayCount}-tab`;
            const paneId = `day-${dayCount}-pane`;
            
            const tabItem = document.createElement('li');
            tabItem.classList.add('nav-item');
            tabItem.setAttribute('role', 'presentation');
            tabItem.innerHTML = `
                <button class="nav-link ${isActive ? 'active' : ''}" 
                        id="${tabId}" 
                        data-bs-toggle="tab" 
                        data-bs-target="#${paneId}" 
                        type="button" 
                        role="tab" 
                        aria-controls="${paneId}" 
                        aria-selected="${isActive ? 'true' : 'false'}">
                    Day ${dayCount}
                </button>`;
            scheduleTabs.appendChild(tabItem);

            const tabPane = document.createElement('div');
            tabPane.classList.add('tab-pane', 'fade', 'pt-3');
            if (isActive) {
                tabPane.classList.add('show', 'active');
            }
            tabPane.setAttribute('id', paneId);
            tabPane.setAttribute('role', 'tabpanel');
            tabPane.setAttribute('aria-labelledby', tabId);
            
            tabPane.innerHTML = renderDayDetails(groupedData[dayCount]);

            scheduleTabContent.appendChild(tabPane);
            
            if (isActive) {
                 try {
                     const tabButton = tabItem.querySelector('.nav-link');
                     new bootstrap.Tab(tabButton).show();
                 } catch (e) {
                      console.warn("Bootstrap 탭 객체 활성화 실패:", e);
                 }
            }
        });
    }

    function renderDayDetails(detailsList) {
        let html = '<div class="list-group">';

        if (detailsList.length === 0) {
             html += `<div class="list-group-item text-muted">등록된 세부 일정이 없습니다.</div>`;
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

        html += '</div>';
        return html;
    }
});