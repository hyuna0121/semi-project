<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ page import="com.travel.dao.MemberDAO"%>
<%@ page import="com.travel.dto.MemberDTO"%>
<%@ page import="java.sql.SQLException"%>
<%
// 🔸 1. 로그인된 사용자 ID 가정 (실제로는 세션에서 가져와야 함)
// String userId = (String) session.getAttribute("userId");
String userId = "admin"; // 🚨 테스트용 ID 설정 (실제 로그인 시 변경 필요)

if (userId == null) {
   // 실제 운영 환경: response.sendRedirect("../login/login.jsp");
   // return;
}

// 🔸 2. DB에서 회원 정보 조회
MemberDTO users = null;
try {
   MemberDAO dao = new MemberDAO();
   users = dao.getMemberById(userId);

   if (users == null) {
      users = new MemberDTO();
      users.setId(userId);
      users.setName("정보 없음");
   }

} catch (SQLException e) {
   e.printStackTrace();
   out.println("<script>alert('데이터베이스 오류가 발생했습니다. (테이블/연결 확인 필요)');</script>");
   return;
}

// 🚨 Base64 저장 방식으로 변경되었으므로, 파일 경로 대신 Data URL을 생성합니다.
String profileImgDataUrl = null;
String currentProfileImageBase64 = users.getProfileImage(); // DB에서 가져온 Base64 문자열

if (currentProfileImageBase64 != null && !currentProfileImageBase64.isEmpty()) {
   // Base64 문자열 앞에 Data URL 헤더를 붙여서 브라우저에 직접 출력
   // (Base64는 캐시 무효화 파라미터가 불필요합니다.)
   profileImgDataUrl = "data:image/png;base64," + currentProfileImageBase64;
} else {
   // DB에 Base64 값이 없을 경우, 기본 이미지 파일 경로를 사용합니다.
   profileImgDataUrl = "image/default_profile.png"; 
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>내 프로필 | 마이페이지</title>

<link
   href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
   rel="stylesheet">

<style>
body {
   background-color: #f8f9fa;
}

.main-container {
   display: flex;
   min-height: calc(100vh - 60px);
}

.sidebar {
   width: 250px;
   background-color: #343a40;
   color: #fff;
   padding: 20px;
}

.sidebar h5 {
   color: #ffc107;
   text-align: center;
   margin-bottom: 30px;
}

.sidebar a {
   display: block;
   color: #fff;
   padding: 10px 15px;
   border-radius: 8px;
   text-decoration: none;
   margin-bottom: 8px;
   transition: background 0.2s;
}

.sidebar a:hover, .sidebar a.active {
   background-color: #ffc107;
   color: #343a40;
}

.content {
   flex: 1;
   padding: 40px;
   background-color: #fff;
}

.profile-img {
   width: 130px;
   height: 130px;
   border-radius: 50%;
   border: 3px solid #ffc107;
   object-fit: cover;
   margin-bottom: 10px;
}
</style>
</head>

<body>

   <%@ include file="../header.jsp"%>

   <div class="main-container">
      <aside class="sidebar">
         <h5>My Page</h5>
         <a href="#" class="active">내 프로필</a> <a href="travel_schedule.jsp">여행
            일정</a> <a href="#">내 댓글</a> <a href="#">설정</a>
      </aside>

      <main class="content">
         <div class="container">
            <h3 class="mb-4 border-bottom pb-2">📇 내 프로필</h3>

            <div id="viewMode">
               <div class="text-center mb-4">
                  <img src="<%=profileImgDataUrl%>" class="profile-img" alt="프로필 사진">
               </div>

               <table class="table table-bordered">
                  <tr>
                     <th>이름</th>
                     <td><%=users.getName()%></td>
                  </tr>
                  <tr>
                     <th>주소</th>
                     <td><%=users.getAddress() != null ? users.getAddress() : "정보 없음"%></td>
                  </tr>
                  <tr>
                     <th>전화번호</th>
                     <td><%=users.getPhone() != null ? users.getPhone() : "정보 없음"%></td>
                  </tr>
                  <tr>
                     <th>이메일</th>
                     <td><%=users.getEmail() != null ? users.getEmail() : "정보 없음"%></td>
                  </tr>
                  <tr>
                     <th>성별</th>
                     <%
                     String genderText = "정보 없음";
                     if ("M".equals(users.getGender())) {
                        genderText = "남성";
                     } else if ("F".equals(users.getGender())) {
                        genderText = "여성";
                     }
                     %>
                     <td><%=genderText%></td>
                  </tr>
               </table>

               <div class="text-end">
                  <button class="btn btn-warning text-dark" id="editBtn">정보
                     수정하기</button>
               </div>
            </div>

            <div id="editMode" style="display: none;">
               <form action="ProfileUpdateServlet" method="post"
                  enctype="multipart/form-data" class="row g-4">

                  <input type="hidden" name="id" value="<%=users.getId()%>">
                  <input type="hidden" name="currentProfileImage"
                     value="<%=currentProfileImageBase64 != null ? currentProfileImageBase64 : ""%>">

                  <div class="col-md-4 text-center">
                     <img id="preview" src="<%=profileImgDataUrl%>" class="profile-img"
                        alt="프로필 사진"> <input type="file" id="profileImg"
                        name="profileImg" class="form-control mt-2" accept="image/*">
                  </div>

                  <div class="col-md-8">
                     <div class="mb-3">
                        <label class="form-label">이름</label> <input type="text"
                           name="name" class="form-control"
                           value="<%=users.getName() != null ? users.getName() : ""%>"
                           required>
                     </div>
                     <div class="mb-3">
                        <label class="form-label">주소</label> <input type="text"
                           name="address" class="form-control"
                           value="<%=users.getAddress() != null ? users.getAddress() : ""%>">
                     </div>
                     <div class="mb-3">
                        <label class="form-label">전화번호</label> <input type="text"
                           name="phone" class="form-control"
                           value="<%=users.getPhone() != null ? users.getPhone() : ""%>">
                     </div>
                     <div class="mb-3">
                        <label class="form-label">이메일</label> <input type="email"
                           name="email" class="form-control"
                           value="<%=users.getEmail() != null ? users.getEmail() : ""%>"
                           required>
                     </div>
                     <div class="mb-3">
                        <label class="form-label">새 비밀번호 (변경시에만 입력)</label> <input
                           type="password" name="newPassword" class="form-control"
                           placeholder="변경하지 않으려면 비워두세요">
                     </div>
                  </div>

                  <div class="text-end">
                     <button type="button" class="btn btn-secondary me-2"
                        id="cancelBtn">취소</button>
                     <button type="submit" class="btn btn-warning text-dark">저장하기</button>
                  </div>
               </form>
            </div>
         </div>
      </main>
   </div>

   <%@ include file="../footer.jsp"%>

   <script
      src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

   <script>
  const editBtn = document.getElementById('editBtn');
  const cancelBtn = document.getElementById('cancelBtn');
  const viewMode = document.getElementById('viewMode');
  const editMode = document.getElementById('editMode');
  const preview = document.getElementById("preview");
  const profileImgInput = document.getElementById("profileImg");
  
  // ✅ Data URL 변수를 JavaScript로 전달
  const originalSrc = "<%=profileImgDataUrl%>"; 

  // 🔹 수정 버튼 클릭 시 → 수정 모드로 전환
  editBtn.addEventListener('click', () => {
    viewMode.style.display = 'none';
    editMode.style.display = 'block';
  });

  // 🔹 취소 버튼 클릭 시 → 보기 모드로 복귀 및 파일 입력 초기화
  cancelBtn.addEventListener('click', () => {
    editMode.style.display = 'none';
    viewMode.style.display = 'block';
    
    // 파일 입력 필드 초기화
    profileImgInput.value = '';
    // 미리보기 이미지를 원본 이미지로 복원 (Data URL)
    preview.src = originalSrc; 
  });

  // 🔹 프로필 사진 미리보기
  profileImgInput.addEventListener("change", function(e) {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function(evt) {
        preview.src = evt.target.result;
      };
      // Base64 문자열로 읽어와 Data URL 형식으로 미리보기에 바로 표시
      reader.readAsDataURL(file);
    } else {
      // 파일 선택 취소 시 원본 이미지(Data URL)로 복원
      preview.src = originalSrc;
    }
  });
</script>

</body>
</html>