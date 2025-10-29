<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
  /* 푸터 스타일 */
  footer {
    background-color: #f8f8f8;
    border-top: 1px solid #ddd;
    padding: 20px 0;
    font-family: 'Noto Sans KR', sans-serif;
    width: 100%;
  }

  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    color: #666;
  }

  .footer-content p {
    margin: 0;
  }

  .social-icons a {
    text-decoration: none;
    font-size: 18px;
    margin-left: 15px;
    color: #666;
    transition: color 0.3s;
  }

  .social-icons a:hover {
    color: #000;
  }

  /* 모바일 대응 */
  @media (max-width: 600px) {
    .footer-content {
      flex-direction: column;
      gap: 10px;
      text-align: center;
    }

    .social-icons a {
      margin-left: 10px;
      font-size: 20px;
    }
  }
</style>
</head>
<body>
  <!-- 푸터 -->
  <footer>
    <div class="footer-container">
      <div class="footer-content">
        <p>© 2025 TRIPLE. All rights reserved.</p>
        <div class="social-icons">
          <a href="#" aria-label="Facebook">🔵</a>
          <a href="#" aria-label="Instagram">📸</a>
          <a href="#" aria-label="Twitter">🐦</a>
        </div>
      </div>
    </div>
  </footer>
</body>
</html>