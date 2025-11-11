document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab-link');
    const itineraryBoard = document.getElementById('itinerary-board');
    const scheduleId = document.getElementById('schedule-id-input').value;

    if (!scheduleId) {
        console.error('스케줄 ID를 찾을 수 없습니다.');
        itineraryBoard.innerHTML = '<tr><td colspan="4">오류: 스케줄 ID를 찾을 수 없습니다.</td></tr>';
        return; 
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            
            const day = tab.getAttribute('data-day');
            loadItinerary(day, scheduleId);
        });
    });

    // 비동기 데이터 로드 함수
    async function loadItinerary(day, scheduleId) {
        itineraryBoard.innerHTML = '<tr><td colspan="4">로딩 중...</td></tr>';

        try {
            const response = await fetch(`${CONTEXT_PATH}/getDetailsForDay?scheduleId=${scheduleId}&day=${day}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            displayData(data);
        } catch (error) {
            console.error('데이터 로딩 중 오류 발생:', error);
            itineraryBoard.innerHTML = '<tr><td colspan="4">데이터를 불러오는 데 실패했습니다.</td></tr>';
        }
    }

    function displayData(items) {
        itineraryBoard.innerHTML = '';

        if (items.length === 0) {
            itineraryBoard.innerHTML = '<tr><td colspan="4">일정이 없습니다.</td></tr>';
            return;
        }

        items.forEach(item => {
            const row = document.createElement('tr');
            
            // 태그 스타일을 동적으로 적용
            const tagHtml = `<span class="tag" data-category="${item.category}">${item.category}</span>`;
			
			let deleteBtn = '<td></td>'
			
			if (userCanDelete) {
				deleteBtn = `<td><a href="${CONTEXT_PATH}/DeleteDetails?detail_id=${item.id}" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a></td>`;
			}
            
            row.innerHTML = `
                <td>${item.startTime}</td>
                <td>${item.place}</td>
                <td>${tagHtml}</td>
                <td>${item.memo}</td>
				${deleteBtn}
            `;
            itineraryBoard.appendChild(row);
        });
    }

    // 4. 페이지 첫 로드 시 1일차 데이터 불러오기
    loadItinerary(1, scheduleId);
	
	// 폼 제출 유효성 검사
	const form = document.getElementById('schedule-form');
	
	if (form) {
		const scheduleTimeInput = document.getElementById('schedule-time');
		
		form.addEventListener('submit', function(event) {
			const checkedDates = document.querySelectorAll('input[name="selectedDates"]:checked');
			const scheduleTimeValue = scheduleTimeInput.value.trim();
			
			if (checkedDates.length === 0) {
				alert('해당 일정을 추가할 날짜를 선택해주세요.');
				event.preventDefault();
				return;
			}
			
			if (scheduleTimeValue === '') {
				alert('일정 시작 시간을 입력해주세요.');
				event.preventDefault();
				scheduleTimeInput.focus();
				return;
			}
		});
		
		if (scheduleTimeInput) {
			scheduleTimeInput.addEventListener('keydown', function(e) {
				if (e.key === 'Enter') {
					e.preventDefault();
				}
			});
		}
	}
});