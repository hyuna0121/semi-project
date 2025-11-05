package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

import com.travel.dao.ScheduleDAO;
import com.travel.dto.ScheduleDTO;
import util.DBUtil; 

@WebServlet("/processEditSchedule") 

@MultipartConfig 
public class ProcessEditScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		String userId = (String) session.getAttribute("loginId");
		
		if (session == null || userId == null) {
		    response.sendRedirect(request.getContextPath() + "/login/login.jsp"); 
		    return; 
		}
		
		Connection conn = null;
		ScheduleDAO dao = new ScheduleDAO();
		
		try {

            long scheduleId = Long.parseLong(request.getParameter("scheduleId"));

            conn = DBUtil.getConnection();
            ScheduleDTO schedule = dao.selectSchedule(conn, scheduleId);

            if (schedule == null) {
                throw new Exception("수정할 일정이 없습니다.");
            }

            boolean flag = false;
            for (String buddy : schedule.getTravelBuddies()) {
                if (buddy.equals(userId)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                 response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
                 return;
            }
            schedule.setTitle(request.getParameter("title"));
            schedule.setLocation(request.getParameter("location"));
            schedule.setDescription(request.getParameter("description"));
            schedule.setVisibility(request.getParameter("visibility") == null ? "Y" : "N"); 



            Part filePart = request.getPart("mainImage");
            String fileName = filePart.getSubmittedFileName();

            if (fileName != null && !fileName.isEmpty()) {

                String uploadPath = "D:/GDJ94/workspace/upload"; // 실제 경로 확인
    
            
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                filePart.write(uploadPath + File.separator + newFileName);
                
                schedule.setMainImage(newFileName);
            }
            
           
            dao.updateSchedule(conn, schedule); 


            response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + scheduleId);

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
        } finally {
            DBUtil.close(conn, null, null);
        }
	}
}