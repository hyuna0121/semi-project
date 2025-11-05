package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.travel.dto.ScheduleDTO;
import com.travel.service.ScheduleService;

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
	private ScheduleService scheduleService = new ScheduleService(); 
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		String userId = (String) session.getAttribute("loginId");
		
		if (session == null || userId == null) {
		    response.sendRedirect(request.getContextPath() + "/login/login.jsp"); 
		    return; 
		}
		
		Part filePart = request.getPart("mainImage");
		
		ScheduleDTO schedule = new ScheduleDTO();
		schedule.setUserId(userId);
		schedule.setTitle(request.getParameter("title"));
		schedule.setLocation(request.getParameter("location"));
		schedule.setDescription(request.getParameter("description"));
		schedule.setVisibility(request.getParameter("visibility") == null ? "Y" : "N"); 
		System.out.println(request.getParameter("visibility"));
        schedule.setTravelBuddies(request.getParameterValues("travelBudies"));
        
        String date = request.getParameter("demo");
        String[] dates = date.split("~");
        schedule.setStartDate(dates[0].trim());
        schedule.setEndDate(dates[1].trim());
        
     // 업로드 실제 경로는 web.xml에서 읽음
        String uploadPath = getServletContext().getInitParameter("uploadBaseDir");
        if (uploadPath == null || uploadPath.isBlank()) {
            uploadPath = getServletContext().getRealPath("/upload"); // fallback
        }
        Files.createDirectories(Path.of(uploadPath));

        // 그대로 서비스 호출 (서비스가 파일 저장 + DB insert 수행)
        long scheduleId = 0;
		try {
			scheduleId = scheduleService.addSchedule(schedule, filePart, uploadPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        response.sendRedirect(request.getContextPath()+"/schedule/schedule.jsp?schedule_id="+scheduleId);
	
	}

}
