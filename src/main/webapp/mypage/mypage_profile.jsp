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
	// MemberDAO는 내부적으로 users 테이블을 조회하도록 수정되었어야 합니다.
	users = dao.getMemberById(userId);

	if (users == null) {
		// DB에 해당 ID가 없을 경우 DTO 객체를 비어있게 초기화 (NPE 방지)
		users = new MemberDTO();
		users.setId(userId);
		users.setName("정보 없음");
	}

} catch (SQLException e) {
	e.printStackTrace();
	// 🚨 DB 연결 및 테이블(users) 오류 확인 후 아래 경고를 띄웁니다.
	out.println("<script>alert('데이터베이스 오류가 발생했습니다. (테이블/연결 확인 필요)');</script>");
	return;
}

String cacheBuster = String.valueOf(System.currentTimeMillis());

String profileImgPath = users.getProfileImage() != null && !users.getProfileImage().isEmpty()
//🚨🚨🚨 images/를 image/로 수정 (폴더 이름 통일)
? "image/" + users.getProfileImage() + "?v=" + cacheBuster 
//🚨🚨🚨 images/를 image/로 수정
: "image/default_profile.png" + "?v=" + cacheBuster;
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
						<img src="<%=profileImgPath%>" class="profile-img" alt="프로필 사진">
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
							value="<%=profileImgPath%>">

						<div class="col-md-4 text-center">
							<img id="preview" src="<%=users.getProfileImage()%>" class="profile-img"
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
  // JSP 스크립트릿에서 설정된 현재 이미지 경로를 JavaScript 변수에 저장
  const originalSrc = "<%=profileImgPath%>"; 

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
    // 미리보기 이미지를 원본 이미지로 복원
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
      reader.readAsDataURL(file);
    } else {
      // 파일 선택 취소 시 원본 이미지로 복원
      preview.src = originalSrc;
    }
  });
</script>

</body>
</html>