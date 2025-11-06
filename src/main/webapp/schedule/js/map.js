const modal = document.querySelector('.modal');
const modalOpen = document.querySelector('.modal_btn');
const modalClose = document.querySelector('.close_btn');

const mapModal = document.querySelector('.modal_map');
const mapModalClose = document.querySelector('.close_map_btn');
const mapModalInfo = document.querySelector('#map_info');

const addCancel = document.querySelector('.close_add_btn');

modalOpen.addEventListener('click', function () {
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
});
modalClose.addEventListener('click', function () {
    modal.classList.remove('show');
    document.body.style.overflow = '';
});

mapModalClose.addEventListener('click', function () {
    mapModal.classList.remove('show');
    removeCurrentMarker();
});

addCancel.addEventListener('click', function() {
    modal.classList.remove('show');
    document.body.style.overflow = '';
    mapModal.classList.remove('show');
    removeCurrentMarker();
}) 


var currentMarker = null;

var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
    mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
        level: 2 // 지도의 확대 레벨
    };  

// 지도를 생성합니다    
var map = new kakao.maps.Map(mapContainer, mapOption); 

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();

var searchOptions = {
    size: 5
};  

// 검색 결과 목록이나 마커를 클릭했을 때 장소명을 표출할 인포윈도우를 생성합니다
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

// 키워드로 장소를 검색합니다
searchPlaces();

// 키워드 검색을 요청하는 함수입니다
function searchPlaces() {
    var keyword = document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다(비동기식 동작)
    ps.keywordSearch(keyword, placesSearchCB, searchOptions); 
}

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        // 정상적으로 검색이 완료됐으면
        // 검색 목록과 마커를 표출합니다
        displayPlaces(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
        return;
    } else if (status === kakao.maps.services.Status.ERROR) {
        alert('검색 결과 중 오류가 발생했습니다.');
        return;
    }
}

// 검색 결과 목록과 마커를 표출하는 함수입니다
function displayPlaces(places) {
    var listEl = document.getElementById('placesList'), 
    menuEl = document.getElementById('menu_wrap'),
    fragment = document.createDocumentFragment(), 
    bounds = new kakao.maps.LatLngBounds(), 
    listStr = '';
    
    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeCurrentMarker();
    
    for (let i = 0; i < places.length; i++) {
        const place = places[i];

        // 마커를 생성하고 지도에 표시합니다
        const placePosition = new kakao.maps.LatLng(place.y, place.x);
        const itemEl = getListItem(i, place); // 검색 결과 항목 Element를 생성합니다

        itemEl.onclick = function () {
            document.getElementById('modalPlaceName').value = place.place_name;
            document.getElementById('modalLatitude').value = place.y;       // 위도
            document.getElementById('modalLongitude').value = place.x;

			mapModalInfo.innerHTML = ''; 
			var clonedItem = this.cloneNode(true); 
			mapModalInfo.appendChild(clonedItem);				
				
            removeCurrentMarker();
            currentMarker = addMarker(placePosition, i);
            mapModal.classList.add('show');
            map.relayout();
                
            map.setCenter(placePosition);
            map.setLevel(2, {animate: true});
            displayInfowindow(currentMarker, place.place_name);
        };

        fragment.appendChild(itemEl);
    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {
    var el = document.createElement('li'),
    itemStr = '<div class="info">' + 
                '   <h3>' + places.place_name + '</h3>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + ' (' + places.address_name + ')' + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>'; 
    }

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, idx, title) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
        imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
        imgOptions =  {
            spriteSize : new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
            marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage 
        });

    marker.setMap(map); // 지도 위에 마커를 표출합니다

    return marker;
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeCurrentMarker() {
    if (currentMarker) {
        currentMarker.setMap(null);
        currentMarker = null;
    }
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i; 

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }
	
	var lastPage = Math.min(pagination.last, 5);

    for (i=1; i<=lastPage; i++) {
        var el = document.createElement('a');
        el.href = "javascript:void(0)";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

// 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수입니다
// 인포윈도우에 장소명을 표시합니다
function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

    infowindow.setContent(content);
    infowindow.open(map, marker);
}

 // 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {   
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}