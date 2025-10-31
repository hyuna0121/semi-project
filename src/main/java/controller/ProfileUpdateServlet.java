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
import java.util.UUID; // 파일 이름 중복 방지를 위해 UUID 사용

// 🚨 URL 패턴을 mypage 폴더 내에서 호출 가능하도록 설정
@WebServlet("/mypage/ProfileUpdateServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 5 * 1024 * 1024,   // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class ProfileUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 1. 파일 저장 경로 설정 (webapp/images)
        // mypage 폴더 기준으로 상대 경로를 설정하는 것이 아니라, 서버의 실제 경로를 구합니다.
        String savePath = request.getServletContext().getRealPath("/images");
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }

        String id = "";
        String name = "";
        String address = "";
        String phone = "";
        String email = "";
        String newPassword = null;
        String currentProfileImage = null;
        String profileImage = null; // 최종 DB에 저장될 파일명

        // 2. Part를 순회하며 텍스트 데이터 및 파일 처리
        try {
            for (Part part : request.getParts()) {
                String partName = part.getName();
                
                // 텍스트 필드 처리 (Part API에서 텍스트 값은 별도로 처리해야 함)
                if (part.getSubmittedFileName() == null && part.getContentType() == null) {
                    String value = request.getParameter(partName); // 또는 IOUtils 등으로 읽어야 하나, getParameter로 처리합니다.
                    
                    switch (partName) {
                        case "id": id = value; break;
                        case "name": name = value; break;
                        case "address": address = value; break;
                        case "phone": phone = value; break;
                        case "email": email = value; break;
                        case "newPassword": newPassword = value; break;
                        case "currentProfileImage": currentProfileImage = value; break;
                    }
                } else if ("profileImg".equals(partName)) {
                    // 파일 필드 처리
                    String submittedFileName = part.getSubmittedFileName();
                    
                    if (submittedFileName != null && !submittedFileName.isEmpty()) {
                        // 파일 이름 중복 방지: UUID를 파일명 앞에 붙입니다.
                        String uniqueFileName = UUID.randomUUID().toString() + "_" + submittedFileName;
                        profileImage = uniqueFileName;
                        
                        // 4. 파일 저장
                        part.write(savePath + File.separator + profileImage);
                        
                        // 5. 기존 파일 삭제 로직
                        if (currentProfileImage != null && !currentProfileImage.isEmpty()) {
                            File oldFile = new File(savePath + File.separator + currentProfileImage);
                            if (oldFile.exists()) {
                                oldFile.delete();
                            }
                        }
                    }
                }
            }
            
            // 파일이 새로 업로드되지 않았다면, 기존 파일명을 사용
            if (profileImage == null) {
                profileImage = currentProfileImage;
            }

            // 6. DTO에 데이터 설정 및 DAO 호출
            MemberDTO member = new MemberDTO();
            member.setId(id);
            member.setName(name);
            member.setAddress(address);
            member.setPhone(phone);
            member.setEmail(email);
            member.setProfileImage(profileImage);
            
            // 🚨 비밀번호 암호화 로직은 여기에 추가되어야 합니다.
            // String hashedPwd = (newPassword != null && !newPassword.isEmpty()) ? EncryptionUtil.hash(newPassword) : null;
            
            MemberDAO dao = new MemberDAO();
            int result = dao.updateMember(member, newPassword); // newPassword 대신 hashedPwd 전달 필요

            if (result > 0) {
                // 성공 시 마이페이지로 스크립트 리다이렉트
                response.getWriter().println("<script>alert('프로필 정보가 성공적으로 수정되었습니다.'); location.href='mypage_profile.jsp';</script>");
            } else {
                // DB 업데이트 실패
                response.getWriter().println("<script>alert('프로필 수정에 실패했습니다. (DB 오류)'); history.back();</script>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('데이터베이스 오류가 발생했습니다.'); history.back();</script>");
        } catch (Exception e) {
             e.printStackTrace();
             response.getWriter().println("<script>alert('업로드 처리 중 오류가 발생했습니다.'); history.back();</script>");
        }
    }
}