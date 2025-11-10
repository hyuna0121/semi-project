<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Footer</title>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700;800&display=swap" rel="stylesheet">

<style>
  :root{
    --brand:#6ea3f7;
    --brand-strong:#5c8fe0;
    --bg-light:#f4f7ff;
    --text:#0f172a;
    --muted:#64748b;
    --line:#e2e8f0;
  }

  /* ===== 푸터 ===== */
  footer {
    background: var(--bg-light);
    border-top: 1px solid var(--line);
    padding: 28px 0;
    font-family: 'Noto Sans KR', system-ui, sans-serif;
    width: 100%;
    box-shadow: 0 -4px 10px rgba(15,23,42,.04);
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
    color: var(--muted);
  }

  .footer-content p {
    margin: 0;
    font-weight: 500;
  }

  /* 소셜 아이콘 */
  .social-icons a {
    text-decoration: none;
    font-size: 20px;
    margin-left: 16px;
    color: var(--muted);
    transition: color .25s ease, transform .2s;
    display:inline-flex; align-items:center; justify-content:center;
    width:32px;height:32px;
    border-radius:50%;
    background:rgba(255,255,255,.8);
    box-shadow:0 2px 6px rgba(15,23,42,.08);
  }

  .social-icons a:hover {
    color: var(--brand-strong);
    background:#fff;
    transform:translateY(-2px);
  }

  /* 브랜드 로고 느낌 */
  .footer-brand{
    font-weight:800;
    color:var(--brand-strong);
  }

  /* 모바일 대응 */
  @media (max-width: 600px) {
    .footer-content {
      flex-direction: column;
      gap: 12px;
      text-align: center;
    }
    .social-icons a {
      margin-left: 10px;
      font-size: 22px;
    }
  }
</style>
</head>

<body>
  <!-- 푸터 -->
  <footer>
    <div class="footer-container">
      <div class="footer-content">
        <p>© 2025 <span class="footer-brand">TRAVEL</span>. All rights reserved.</p>
        <div class="social-icons">
          <a href="#" aria-label="Facebook">🌐</a>
          <a href="#" aria-label="Instagram">📸</a>
          <a href="#" aria-label="Twitter">🐦</a>
        </div>
      </div>
    </div>
  </footer>
</body>
</html>
