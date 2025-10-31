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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID; 

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 15    // 15MB
)
// 🚨 mypage/mypage_profile.jsp에서 호출되므로, URL 패턴을 /mypage/ 기준으로 설정
@WebServlet("/mypage/ProfileUpdateServlet") 
public class ProfileUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // 🚨🚨🚨 파일 저장/조회/삭제의 기준 경로 (webapp/mypage/image) 🚨🚨🚨
        final String WEB_IMAGE_PATH = "/mypage/image";
        
        String userId = null;
        String name = null;
        String address = null;
        String phone = null;
        String email = null;
        String newPassword = null;
        String currentProfileImage = null; // 기존 DB에 저장된 파일명
        String newProfileImage = null;     // 새로 DB에 저장될 파일명 (또는 기존 파일명)
        Part profileImgPart = null;        

        // 1. 폼 데이터 추출
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
            // 2. 파일 업로드 처리 및 파일명 생성
            if (profileImgPart != null && profileImgPart.getSize() > 0) {
                
                // 🚨 웹 접근 가능한 실제 물리적 경로 획득
                String savePath = getServletContext().getRealPath(WEB_IMAGE_PATH);
                File saveDir = new File(savePath);
                if (!saveDir.exists()) {
                    saveDir.mkdirs(); // 폴더가 없으면 생성
                }

                String submittedFileName = profileImgPart.getSubmittedFileName();
                String fileExtension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
                
                // 🚨🚨🚨 고유 파일명 생성: 파일 유지 및 캐시 무효화의 핵심 🚨🚨🚨
                newProfileImage = userId + "_" + UUID.randomUUID().toString() + fileExtension;
                
                // 3. 파일 저장 (디스크에 쓰기)
                File file = new File(saveDir, newProfileImage);
                profileImgPart.write(file.getAbsolutePath());
            } else {
                // 업로드 파일이 없으면 기존 파일명을 유지
                newProfileImage = currentProfileImage; 
            }

            // 4. DB 업데이트 DTO 설정
            MemberDTO updatedUser = new MemberDTO();
            updatedUser.setId(userId);
            updatedUser.setName(name);
            updatedUser.setAddress(address);
            updatedUser.setPhone(phone);
            updatedUser.setEmail(email);
            updatedUser.setProfileImage(newProfileImage); 

            String hashedPassword = newPassword; 

            // 5. DAO 호출
            int result = dao.updateMember(updatedUser, hashedPassword);

            if (result > 0) {
                // 6. DB 업데이트 성공 시, 이전 파일 삭제 (새 파일이 업로드된 경우에만)
                if (newProfileImage != null && !newProfileImage.equals(currentProfileImage) && currentProfileImage != null && !currentProfileImage.isEmpty() && !currentProfileImage.equals("default_profile.png")) {
                    String deletePath = getServletContext().getRealPath(WEB_IMAGE_PATH); // 🚨 동일 경로 사용
                    File oldFile = new File(deletePath, currentProfileImage);
                    if (oldFile.exists()) {
                        oldFile.delete(); 
                    }
                }
                
                // 7. 성공 응답 및 리다이렉트
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