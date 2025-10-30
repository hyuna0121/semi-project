package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import util.DBUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Servlet implementation class ProcessAddScheduleServlet
 */
@WebServlet("/processAddSchedule")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 1, 
		maxFileSize = 1024 * 1024 * 10,
		maxRequestSize = 1024 * 1024 * 50
)
public class ProcessAddScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String userId = "admin";
        String title = request.getParameter("title");
        String location = request.getParameter("location");
        String description = request.getParameter("description");
        String visibility = request.getParameter("visibility") == null ? "N" : "Y"; // Y : 공개, N : 비공개
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        // ==== 파일 업로드 처리 ====
        Part filePart = request.getPart("mainImage");
        String originalFileName = null;
        String fileName = null;
        String ext = null;
        
        if (filePart != null && filePart.getSize() > 0) {
        	originalFileName = filePart.getSubmittedFileName();
        	
        	int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex > 0) {
                ext = originalFileName.substring(dotIndex);
            }
            
            String baseName = originalFileName.substring(0, dotIndex);
        	String uuid = UUID.randomUUID().toString().substring(0, 8);
        	fileName = baseName + "_" + uuid + ext;
        	
        	// 외부 업로드 폴더 사용
        	String uploadPath = "D:/GDJ94/workspace/upload";
        	
        	File uploadDir = new File(uploadPath);
        	if (!uploadDir.exists()) {
        		uploadDir.mkdirs();
        	}
        	
        	filePart.write(uploadPath + File.separator + fileName);
        }
        
    	String sql = "INSERT INTO schedules(user_id, title, location, description, visibility, start_date, end_date, main_image, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
    	
    	try (Connection conn = DBUtil.getConnection();
    		 PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, userId);
	   		pstmt.setString(2, title);
	   		pstmt.setString(3, location);
	   		pstmt.setString(4, description);
	   		pstmt.setString(5, visibility);
	   		pstmt.setString(6, startDate);
	   		pstmt.setString(7, endDate);
	   		pstmt.setString(8, fileName);
	   		pstmt.executeUpdate();
   		} catch (SQLException e) {
   			e.printStackTrace();
   		} 
           
        response.sendRedirect("index.jsp");
	}

}
