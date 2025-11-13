document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab-link');
    const itineraryBoard = document.getElementById('itinerary-board');
    const scheduleId = document.getElementById('schedule-id-input').value;

    let kakaoMap = null;
    let markers = [];
    let infowindow = new kakao.maps.InfoWindow({
        zIndex:1,
        content: ''
    });

    if (!scheduleId) {
        console.error('스케줄 ID를 찾을 수 없습니다.');
        itineraryBoard.innerHTML = '<tr><td colspan="4">오류: 스케줄 ID를 찾을 수 없습니다.</td></tr>';
        return; 
    }

    const mapContainer = document.getElementById('marker_map');
    const mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567),
        level: 3
    };

    kakaoMap = new kakao.maps.Map(mapContainer, mapOption);

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

    function clearMarkers() {
        for (let i = 0; i < markers.length; i++) {
            markers[i].setMap(null);
        }
        markers = [];
        infowindow.close();
    }

    function displayData(items) {
        itineraryBoard.innerHTML = '';
        clearMarkers();

        const bounds = new kakao.maps.LatLngBounds();

        const markerSpriteImageUrl = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png';
        const markerImageSize = new kakao.maps.Size(36, 37);

        const markerImageOptions = {
            spriteSize: new kakao.maps.Size(36, 691), // 스프라이트 이미지 전체 크기
            spriteOrigin: null, // 잘라낼 영역의 좌상단 좌표 (루프 내에서 계산)
            offset: new kakao.maps.Point(13, 37) // 마커 중심 좌표에 일치시킬 이미지 내 좌표
        };

        if (items.length === 0) {
            itineraryBoard.innerHTML = '<tr><td colspan="4">일정이 없습니다.</td></tr>';

            kakaoMap.setCenter(new kakao.maps.LatLng(37.566826, 126.9786567));
            kakaoMap.setLevel(3);

            return;
        }

        items.forEach((item, index) => {
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

            const lat = parseFloat(item.latitude);
            const lng = parseFloat(item.longitude);

            if (!isNaN(lat) && !isNaN(lng)) {
                const markerPosition = new kakao.maps.LatLng(lat, lng);

                const spriteOriginY = (index * 46) + 10;
                markerImageOptions.spriteOrigin = new kakao.maps.Point(0, spriteOriginY);

                const markerImage = new kakao.maps.MarkerImage(
                    markerSpriteImageUrl, 
                    markerImageSize, 
                    markerImageOptions
                );

                const marker = new kakao.maps.Marker({
                    position: markerPosition,
                    image: markerImage
                });

                marker.setMap(kakaoMap);

                kakao.maps.event.addListener(marker, 'click', () => {
                    kakaoMap.setLevel(5); 
                    kakaoMap.panTo(markerPosition);

                    const content = `<div style="padding: 5px; white-space: nowrap;">${item.place}</div>`;
                    infowindow.setContent(content);
                    infowindow.open(kakaoMap, marker);
                });

                row.addEventListener('click', () => {
                    kakaoMap.setLevel(5);
                    kakaoMap.panTo(markerPosition);

                    const content = `<div style="padding: 5px; white-space: nowrap;">${item.place}</div>`;
                    infowindow.setContent(content);

                    infowindow.open(kakaoMap, marker);
                });

                markers.push(marker);
                bounds.extend(markerPosition);
            }
        });

        if (markers.length > 0) {
            if (markers.length === 1) {
                kakaoMap.setCenter(markers[0].getPosition());
                kakaoMap.setLevel(5);
            } else {
                kakaoMap.setBounds(bounds);
            }
        }
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