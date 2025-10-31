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
import java.util.Base64; // Base64 인코딩/디코딩 사용

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, 
    maxFileSize = 1024 * 1024 * 10,      
    maxRequestSize = 1024 * 1024 * 15   
)
@WebServlet("/mypage/ProfileUpdateServlet") 
public class ProfileUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        String userId = null;
        String name = null;
        String address = null;
        String phone = null;
        String email = null;
        String newPassword = null;
        String currentProfileImage = null; // Base64 String 또는 NULL
        String newProfileImageBase64 = null; // 새로 DB에 저장될 Base64 String
        Part profileImgPart = null;        

        // 1. 폼 데이터 추출 (기존 로직 유지)
        try {
            for (Part part : request.getParts()) {
                String partName = part.getName();
                
                if (part.getSubmittedFileName() == null) {
                    String value = request.getParameter(partName);
                    
                    switch (partName) {
                        case "id": userId = value; break;
                        case "name": name = value; break;
                        case "address": address = value; break;
                        case "phone": phone = value; break;
                        case "email": email = value; break;
                        case "newPassword": newPassword = value; break;
                        case "currentProfileImage": currentProfileImage = value; break;
                    }
                } else {
                    if (partName.equals("profileImg") && part.getSize() > 0) {
                        profileImgPart = part;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('폼 데이터 처리 중 오류 발생.'); history.back();</script>");
            return;
        }

        MemberDAO dao = new MemberDAO();
        try {
            // 2. 파일 업로드 처리 및 Base64 문자열 생성
            if (profileImgPart != null && profileImgPart.getSize() > 0) {
                
                // 🚨🚨🚨 이미지 파일을 Base64 문자열로 변환 🚨🚨🚨
                try (InputStream input = profileImgPart.getInputStream()) {
                    byte[] imageBytes = input.readAllBytes();
                    // Java 8 표준 Base64 인코더 사용
                    newProfileImageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                } 
                // 🚨🚨🚨 파일 시스템 저장/삭제 로직은 완전히 제거됩니다. 🚨🚨🚨
                
            } else {
                // 업로드 파일이 없으면 기존 Base64 문자열을 유지
                newProfileImageBase64 = currentProfileImage; 
            }

            // 3. DB 업데이트 DTO 설정
            MemberDTO updatedUser = new MemberDTO();
            updatedUser.setId(userId);
            updatedUser.setName(name);
            updatedUser.setAddress(address);
            updatedUser.setPhone(콜);
            updatedUser.setEmail(email);
            updatedUser.setProfileImage(newProfileImageBase64); // Base64 문자열 저장

            String hashedPassword = newPassword; 
            int result = dao.updateMember(updatedUser, hashedPassword);

            if (result > 0) {
                // 4. 성공 응답 및 리다이렉트
                response.getWriter().println("<script>alert('프로필 정보가 성공적으로 수정되었습니다.'); location.href='mypage_profile.jsp';</script>");
            } else {
                response.getWriter().println("<script>alert('프로필 업데이트에 실패했습니다. (DB 오류)'); history.back();</script>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('데이터베이스 처리 중 오류가 발생했습니다.'); history.back();</script>");
        }
    }
}