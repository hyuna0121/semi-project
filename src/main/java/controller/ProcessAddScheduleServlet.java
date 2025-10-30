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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String[] travelBuddies = request.getParameterValues("travelBudies");
        
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
        
    	String sqlSchedule = "INSERT INTO schedules(user_id, title, location, description, visibility, start_date, end_date, main_image, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
    	String sqlMember = "INSERT INTO members(schedule_id, user_id) VALUES (?, ?)";
    	
    	Connection conn = null; 
    	PreparedStatement pstmtSchedule = null; 
    	PreparedStatement pstmtMembers = null;
    	ResultSet rs = null;
    	long scheduleId = 0;
    	
    	try {
    		conn = DBUtil.getConnection();
    		conn.setAutoCommit(false);
    		
    		pstmtSchedule = conn.prepareStatement(sqlSchedule, Statement.RETURN_GENERATED_KEYS);
    		
    		pstmtSchedule.setString(1, userId);
    		pstmtSchedule.setString(2, title);
    		pstmtSchedule.setString(3, location);
    		pstmtSchedule.setString(4, description);
    		pstmtSchedule.setString(5, visibility);
    		pstmtSchedule.setString(6, startDate);
    		pstmtSchedule.setString(7, endDate);
    		pstmtSchedule.setString(8, fileName);
	   		
	   		int result = pstmtSchedule.executeUpdate();
	   		
	   		if (result > 0) {
	   			rs = pstmtSchedule.getGeneratedKeys();
	   			
	   			if (rs.next()) {
	   				scheduleId = rs.getLong(1); 
	   			} else {
	   				throw new SQLException("Creating schedule failed, no ID obtained.");
	   			}
	   		}
	   		
	   		if (scheduleId > 0) {
	   			pstmtMembers = conn.prepareStatement(sqlMember); 
	   			
	   			pstmtMembers.setLong(1, scheduleId);
	   			pstmtMembers.setString(2, userId);
	   			pstmtMembers.addBatch(); 
	   			
	   			if (travelBuddies != null) {
	   				for (String buddyId : travelBuddies) {
	   					pstmtMembers.setLong(1, scheduleId);
	   					pstmtMembers.setString(2, buddyId);
	   					pstmtMembers.addBatch();
	   				}
	   			}
	   			
	   			pstmtMembers.executeBatch();
	   	    } else {
	   	    	throw new SQLException("No schedule ID generated, member insert aborted.");
	   	    }
	   		
	   		conn.commit(); 
   		} catch (SQLException e) {
   			e.printStackTrace();
   			
   			try {
   		        if (conn != null) {
   		            conn.rollback(); 
   		        }
   		    } catch (SQLException se) {
   		        se.printStackTrace();
   		    }
   		} finally {
   			try {
   		        if (rs != null) rs.close();
   		        if (pstmtSchedule != null) pstmtSchedule.close();
   		        if (pstmtMembers != null) pstmtMembers.close();
   		        if (conn != null) {
   		            conn.setAutoCommit(true); 
   		            conn.close();
   		        }
   		    } catch (SQLException e) {
   		        e.printStackTrace();
   		    }
   		}
           
        response.sendRedirect("index.jsp");
	}

}
