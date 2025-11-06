package controller;

import com.travel.dao.MemberDAO;
import com.travel.dto.MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 15)
@WebServlet("/mypage/ProfileUpdateServlet")
public class ProfileUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ProfileUpdateServlet.class.getName());

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String userId = null;
		String name = null;
		String address = null;
		String phone = null;
		String email = null;
		String gender = null;
		String newPassword = null;
		String newPasswordConfirm = null;
		String currentPassword = null;
		String currentProfileImage = null;
		String newProfileImageBase64 = null;
		Part profileImgPart = null;

		// 1. 폼 데이터 추출
		try {
			for (Part part : request.getParts()) {
				String partName = part.getName();

				if (part.getSubmittedFileName() == null) {
					String value = request.getParameter(partName);

					switch (partName) {
					case "id":
						userId = value;
						break;
					case "name":
						name = value;
						break;
					case "address":
						address = value;
						break;
					case "phone":
						phone = value;
						break;
					case "email":
						email = value;
						break;
					case "gender":
						gender = value;
						break;
					case "newPassword":
						newPassword = value;
						break;
					case "newPasswordConfirm":
						newPasswordConfirm = value;
						break;
					case "currentPassword":
						currentPassword = value;
						break;
					case "currentProfileImage":
						currentProfileImage = value;
						break;
					}
				} else {
					if (partName.equals("profileImg") && part.getSize() > 0) {
						profileImgPart = part;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Form data processing error", e);
			response.getWriter().println("<script>alert('폼 데이터 처리 중 오류 발생.'); history.back();</script>");
			return;
		}

		MemberDAO dao = new MemberDAO();
		try {
			// 0. 현재 사용자 정보 조회 (비밀번호 횟수 확인용)
			MemberDTO currentUser = dao.getMemberById(userId);
			if (currentUser == null) {
				response.getWriter().println("<script>alert('사용자 정보를 찾을 수 없습니다.'); history.back();</script>");
				return;
			}

			// 1. 현재 비밀번호 검증
			if (currentPassword != null) {
				currentPassword = currentPassword.trim();
			}

			if (currentPassword == null || currentPassword.isEmpty()) {
				response.getWriter().println("<script>alert('정보 수정을 위해 현재 비밀번호를 입력해주세요.'); history.back();</script>");
				return;
			}

			// DB에서 저장된 평문 비밀번호를 가져와 검증
			String storedPassword = dao.getPasswordHash(userId);
			if (storedPassword != null) {
				storedPassword = storedPassword.trim(); // DB에 공백이 있을 경우 대비
			}

			if (storedPassword == null || !storedPassword.equals(currentPassword)) {
				// 🚨🚨 비밀번호 불일치 (평문 비교)
				response.getWriter().println("<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>");
				return;
			}

			String finalPasswordToSave = null;

			// 2. 새 비밀번호 처리 및 횟수 제한 검사
			if (newPassword != null && !newPassword.isEmpty()) {

				// 🚨🚨🚨 비밀번호 수정 횟수 제한 로직 🚨🚨🚨
				if (currentUser.getPasswordUpdateCount() >= 3) {
					response.getWriter().println(
							"<script>alert('비밀번호는 최대 3회만 수정 가능합니다. 수정되지 않았습니다.'); location.href='mypage_profile.jsp';</script>");
					return;
				}

				// 새 비밀번호 일치 확인 (JS에서 했지만 서버에서 한 번 더 확인)
				if (!newPassword.equals(newPasswordConfirm)) {
					response.getWriter()
							.println("<script>alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.'); history.back();</script>");
					return;
				}

				// DB에 저장될 새 비밀번호 공백 제거 및 할당
				finalPasswordToSave = newPassword.trim();

			} else {
				// 새 비밀번호를 입력하지 않았으므로 null 유지
				finalPasswordToSave = null;
			}

			// 3. 프로필 이미지 처리 (Base64 유지)
			if (profileImgPart != null && profileImgPart.getSize() > 0) {

				try (InputStream input = profileImgPart.getInputStream()) {
					byte[] imageBytes = input.readAllBytes();
					newProfileImageBase64 = Base64.getEncoder().encodeToString(imageBytes);
				}

			} else {
				newProfileImageBase64 = currentProfileImage;
			}

			// 4. DB 업데이트 DTO 설정
			MemberDTO updatedUser = new MemberDTO();
			updatedUser.setId(userId);
			updatedUser.setName(name);
			updatedUser.setAddress(address);
			updatedUser.setPhone(phone);
			updatedUser.setEmail(email);
			updatedUser.setGender(gender);
			updatedUser.setProfileImage(newProfileImageBase64);

			// 5. DAO 호출 (finalPasswordToSave가 null이 아니면 password와 count가 증가됨)
			int result = dao.updateMember(updatedUser, finalPasswordToSave);

			if (result > 0) {
				response.getWriter().println(
						"<script>alert('프로필 정보가 성공적으로 수정되었습니다.'); location.href='mypage_profile.jsp';</script>");
			} else {
				response.getWriter().println("<script>alert('프로필 업데이트에 실패했습니다. (DB 오류)'); history.back();</script>");
			}

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Database error during profile update for user: " + userId, e);
			response.getWriter().println("<script>alert('데이터베이스 처리 중 오류가 발생했습니다.'); history.back();</script>");
		}
	}
}