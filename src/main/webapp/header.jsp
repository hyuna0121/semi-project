<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>여행 일정 공유</title>
<style>
  /* 전체 레이아웃 */
  body {
    margin: 0;
    font-family: 'Noto Sans KR', sans-serif;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  main {
    flex: 1; /* 남은 공간을 차지하도록 */
  }

  /* 헤더 스타일 */
  header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
    background-color: #fff;
    box-sizing: border-box;
  }

  .logo {
    font-weight: 700;
    font-size: 20px;
    letter-spacing: 2px;
    color: #000;
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: 25px;
    font-size: 14px;
    color: #666;
    cursor: pointer;
  }

  .nav-right .reservation {
    color: #000;
    font-weight: 500;
  }

  .menu-icon {
    width: 20px;
    height: 15px;
    position: relative;
    cursor: pointer;
  }

  .menu-icon span {
    background-color: #000;
    display: block;
    height: 2px;
    width: 100%;
    border-radius: 1px;
    position: absolute;
    left: 0;
  }

  .menu-icon span:nth-child(1) { top: 0; }
  .menu-icon span:nth-child(2) { top: 6px; }
  .menu-icon span:nth-child(3) { top: 12px; }

  .notification-dot {
    position: absolute;
    top: 5px;
    right: 0;
    width: 7px;
    height: 7px;
    background-color: #ff385c;
    border-radius: 50%;
    box-shadow: 0 0 2px rgba(255, 56, 92, 0.5);
  }
</style>
</head>
<body>
  <%@ include file="/header.jspf" %>
</body>
</html>
