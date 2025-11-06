<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.travel.dao.MemberDAO"%>
<%@ page import="com.travel.dto.MemberDTO"%>
<%@ page import="java.sql.SQLException"%>
<%
// 🔸 1. 로그인된 사용자 ID 가정 (실제로는 세션에서 가져와야 함)
String userId = (String) session.getAttribute("loginId");
/* String userId = "fasdf"; */ // 🚨 테스트용 ID 설정 (실제 로그인 시 변경 필요)


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

String profileFileName = users.getProfileImage();

String cacheBuster = String.valueOf(System.currentTimeMillis());

String profileImgPath = "mypage/image/";

String contextPath = request.getContextPath();
String fullProfileImgPath = contextPath + "/mypage/image/";

String profileImgUrl = profileFileName != null && !profileFileName.isEmpty()
		? fullProfileImgPath + profileFileName + "?v=" + cacheBuster
		: fullProfileImgPath + "default_profile.png" + "?v=" + cacheBuster;


String currentProfileFileName = profileFileName != null ? profileFileName : "";


int passwordUpdateCount = users.getPasswordUpdateCount();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>내 프로필 | 마이페이지</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">

<link rel="stylesheet" href="css/mypage_profile.css">

</head>

<body>

	<%@ include file="../header.jsp"%>

	<div class="main-container" data-user-id="<%=users.getId()%>">
		<aside class="sidebar">
			<h5>My Page</h5>
			<a href="#" class="active">내 프로필</a> <a href="travel_schedule.jsp">여행
				일정</a> <a href="#">내 댓글</a>
		</aside>

		<main class="content">
			<div class="container">
				<h3 class="section-title">📇 내 프로필</h3>


				<div id="viewMode">
					<div class="profile-header">
						<h4><%=users.getName()%>
							프로필
						</h4>
						<p><%=users.getEmail() != null ? users.getEmail() : "이메일 정보 없음"%></p>
					</div>

					<div class="profile-img-area">
						<img src="<%=profileImgUrl%>" class="profile-img" alt="프로필 사진">
					</div>

					<div class="info-field-group">
						<label class="info-field-label">이름</label> <span
							class="info-value-text"><%=users.getName()%></span>
					</div>

					<div class="info-field-group">
						<label class="info-field-label">주소</label> <span
							class="info-value-text"><%=users.getAddress() != null ? users.getAddress() : "정보 없음"%></span>
					</div>

					<div class="info-field-group">
						<label class="info-field-label">전화번호</label> <span
							class="info-value-text"><%=users.getPhone() != null ? users.getPhone() : "정보 없음"%></span>
					</div>

					<div class="info-field-group">
						<label class="info-field-label">이메일</label> <span
							class="info-value-text"><%=users.getEmail() != null ? users.getEmail() : "정보 없음"%></span>
					</div>

					<div class="info-field-group">
						<label class="info-field-label">성별</label>
						<%
						String genderText = "정보 없음";
						if ("M".equals(users.getGender())) {
							genderText = "남성";
						} else if ("F".equals(users.getGender())) {
							genderText = "여성";
						}
						%>
						<span class="info-value-text"><%=genderText%></span>
					</div>

					<div class="btn-custom-group">
						<button type="button" class="btn-custom btn-primary-custom"
							id="editBtn">정보 수정하기</button>
					</div>
				</div>


				<div id="editMode" style="display: none;">
					<form action="ProfileUpdateServlet" method="post"
						enctype="multipart/form-data">
						<input type="hidden" name="id" value="<%=users.getId()%>">
						<input type="hidden" name="currentProfileImage"
							value="<%=currentProfileFileName%>">

						<div class="profile-img-area">
							<img id="preview" src="<%=profileImgUrl%>" class="profile-img"
								alt="프로필 사진"> <label for="profileImgInput"
								class="camera-icon"> <span class="material-icons">photo_camera</span>
							</label> <input type="file" id="profileImgInput" name="profileImg"
								class="profile-file-input" accept="image/*">
						</div>

						<div class="info-field-group" style="display: none;">
							<label for="currentPasswordHidden" class="info-field-label">현재
								비밀번호 확인</label> <input type="hidden" id="currentPasswordHidden"
								name="currentPassword" class="form-control">
						</div>

						<div class="info-field-group">
							<label for="nameInput" class="info-field-label">이름</label> <input
								type="text" id="nameInput" name="name" class="form-control"
								value="<%=users.getName() != null ? users.getName() : ""%>"
								required>
						</div>

						<div class="info-field-group">
							<label for="addressInput" class="info-field-label">주소</label> <input
								type="text" id="addressInput" name="address"
								class="form-control"
								value="<%=users.getAddress() != null ? users.getAddress() : ""%>">
						</div>

						<div class="info-field-group">
							<label for="phoneInput" class="info-field-label">전화번호</label> <input
								type="text" id="phoneInput" name="phone" class="form-control"
								value="<%=users.getPhone() != null ? users.getPhone() : ""%>">
						</div>

						<div class="info-field-group">
							<label for="emailInput" class="info-field-label">이메일</label> <input
								type="email" id="emailInput" name="email" class="form-control"
								value="<%=users.getEmail() != null ? users.getEmail() : ""%>">
						</div>


						<div class="info-field-group">
							<label for="genderSelect" class="info-field-label">성별</label> <select
								id="genderSelect" name="gender" class="form-control">
								<option value="">선택 안 함</option>
								<option value="M"
									<%="M".equals(users.getGender()) ? "selected" : ""%>>남성</option>
								<option value="F"
									<%="F".equals(users.getGender()) ? "selected" : ""%>>여성</option>
							</select>
						</div>

						<div id="newPasswordGroup">
							<div class="info-field-group">
								<label for="newPasswordInput" class="info-field-label">새
									비밀번호</label> <input type="password" id="newPasswordInput"
									name="newPassword" class="form-control"
									placeholder="변경할 새 비밀번호를 입력해주세요">
							</div>

							<div class="info-field-group">
								<label for="newPasswordConfirm" class="info-field-label">새
									비밀번호 확인</label> <input type="password" id="newPasswordConfirm"
									name="newPasswordConfirm" class="form-control"
									placeholder="새 비밀번호를 다시 한 번 입력해주세요">
								<div id="newPasswordMismatch" class="form-text text-danger"
									style="display: none;">새 비밀번호가 일치하지 않습니다.</div>
							</div>
						</div>


						<div class="btn-custom-group">
							<button type="button" class="btn-custom btn-secondary-custom"
								id="cancelBtn">취소</button>
							<button type="submit" class="btn-custom btn-primary-custom">저장하기</button>
						</div>
					</form>
				</div>
		</main>
	</div>

	<div class="modal fade" id="passwordCheckModal" tabindex="-1"
		aria-labelledby="passwordCheckModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="passwordCheckModalLabel">본인 확인</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p>개인정보 수정을 위해 현재 비밀번호를 입력해주세요.</p>
					<div class="mb-3">
						<label for="currentPassword" class="form-label info-field-label">현재
							비밀번호</label> <input type="password" class="form-control"
							id="currentPassword">
						<div id="passwordFeedback" class="form-text text-danger"
							style="display: none;">비밀번호가 일치하지 않습니다.</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn-custom btn-secondary-custom"
						data-bs-dismiss="modal">취소</button>
					<button type="button" class="btn-custom btn-primary-custom"
						id="confirmPasswordBtn">확인</button>
				</div>
			</div>
		</div>
	</div>
	<%@ include file="../footer.jsp"%>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


	<script>
    // 🚨🚨 JS 변수 전달 구문 오류 수정 및 통일 🚨🚨
    const originalSrc = "<%=profileImgUrl%>";
    const currentUserId = "<%=users.getId()%>";
    const passwordUpdateCount = <%=users.getPasswordUpdateCount()%>;
	</script>

	<script src="js/mypage_profile.js"></script>
</body>
</html>