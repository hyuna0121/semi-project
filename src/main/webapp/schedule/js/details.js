document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab-link');
    const itineraryBoard = document.getElementById('itinerary-board');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            
            const day = tab.getAttribute('data-day');
            loadItinerary(day);
        });
    });

    // 비동기 데이터 로드 함수
    async function loadItinerary(day) {
        itineraryBoard.innerHTML = '<tr><td colspan="4">로딩 중...</td></tr>';

        try {
            // (2) 백엔드 API에 데이터 요청
            // 실제로는 '/api/itinerary?day=1', '/api/itinerary?day=2' 같은 주소가 됩니다.
            // 여기서는 예시를 위해 가짜 데이터(dummyData)를 사용합니다.
            // const response = await fetch(`/api/itinerary?day=${day}`);
            // const data = await response.json();

            // --- 가짜 데이터 (테스트용) ---
            const dummyData = {
                "1": [
                    { id: 1, name: "더숨 초소책방", city: "서울 | Seoul", tag: "커피", tagColor: "brown", hours: "오전 8:00 ~ 오후 10:00" },
                    { id: 2, name: "남산타워", city: "서울 | Seoul", tag: "관광지", tagColor: "blue", hours: "오전 10:00 ~ 오후 11:00" },
                    { id: 3, name: "신라호텔 더 파크뷰", city: "서울 | Seoul", tag: "숙소", tagColor: "orange", hours: "체크인: 오후 3:00 / 체크아웃: 오전 11:00" }
                ],
                "2": [
                    { id: 4, name: "경복궁", city: "서울 | Seoul", tag: "문화체험", tagColor: "purple", hours: "오전 9:00 ~ 오후 6:00" },
                    { id: 5, name: "명동교자 본점", city: "서울 | Seoul", tag: "맛집", tagColor: "red", hours: "오전 10:30 ~ 오후 9:00" }
                ]
            };
            const data = dummyData[day] || []; // 해당 날짜 데이터가 없으면 빈 배열
            // --- 가짜 데이터 끝 ---
            
            // (3) 데이터 표시 함수 호출
            displayData(data);

        } catch (error) {
            console.error('데이터 로딩 중 오류 발생:', error);
            itineraryBoard.innerHTML = '<tr><td colspan="4">데이터를 불러오는 데 실패했습니다.</td></tr>';
        }
    }

    // 3. 데이터를 테이블에 렌더링하는 함수
    function displayData(items) {
        // (1) 테이블 내용 초기화
        itineraryBoard.innerHTML = '';

        // (2) 데이터가 없으면 메시지 표시
        if (items.length === 0) {
            itineraryBoard.innerHTML = '<tr><td colspan="4">일정이 없습니다.</td></tr>';
            return;
        }

        // (3) 각 아이템을 테이블 행(tr)으로 만들어 추가
        items.forEach(item => {
            const row = document.createElement('tr');
            
            // 태그 스타일을 동적으로 적용
            const tagHtml = `<span class="tag" style="background-color: var(--tag-${item.tagColor}, #eee);">${item.tag}</span>`;
            
            row.innerHTML = `
                <td>${item.name}</td>
                <td>${item.city}</td>
                <td>${tagHtml}</td>
                <td>${item.hours}</td>
            `;
            itineraryBoard.appendChild(row);
        });
    }

    // 4. 페이지 첫 로드 시 1일차 데이터 불러오기
    loadItinerary(1);
});