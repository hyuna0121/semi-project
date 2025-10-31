<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="./css/map.css">
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=8aaef2cb5fdf5a54c0607c5d2c9935c1&libraries=services"></script>
<script type="text/javascript" src="./js/map.js" defer></script>
</head>
<body>
	<button type="button" class="modal_btn">일정추가 +</button>
    <div class="modal">
        <div id="menu_wrap" class="bg_white" style="width:50%; height: 80%;">
			<button type="button" class="close_btn">X</button>
            <div class="option">
                <div>
                    <form onsubmit="searchPlaces(); return false;">
                        키워드 : <input type="text" value="잠실야구장" id="keyword" size="15"> 
                        <button type="submit">검색하기</button> 
                    </form>
                </div>
            </div>
            <hr>
            <ul id="placesList"></ul>
            <div id="pagination"></div>
        </div>
    </div>
    <div class="modal_map">
        <div class="bg_white" style="width:50%; height: 80%;">
            <button type="button" class="close_map_btn">X</button>
            <div id="map" style="width:80%;height:80%;"></div>
        </div>
    </div>
</body>
</html>