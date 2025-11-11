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
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.UUID;

import com.travel.dao.ScheduleDAO;
import com.travel.dto.ScheduleDTO;
import com.travel.service.ScheduleService;

import util.DBUtil; 

@WebServlet("/processEditSchedule") 

@MultipartConfig 
public class ProcessEditScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ScheduleService scheduleService = new ScheduleService();
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		String userId = (String) session.getAttribute("loginId");
		
		if (session == null || userId == null) {
		    response.sendRedirect(request.getContextPath() + "/login/login.jsp"); 
		    return; 
		}
		
		long scheduleId = Long.parseLong(request.getParameter("schedule_id"));
		ScheduleDAO dao = new ScheduleDAO();
		
		try (Connection conn = DBUtil.getConnection()) {
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
            	response.setContentType("text/html;charset=UTF-8");

                PrintWriter out = response.getWriter();

                out.println("<script>");
                out.println("alert('일정을 삭제할 권한이 없습니다.');");
                out.println("location.href='" + request.getContextPath() + "/mainpage/mainpage.jsp';");
                out.println("</script>");
                out.flush();

                return;
            }
            
            schedule.setTitle(nvl(request.getParameter("title")));
            schedule.setLocation(nvl(request.getParameter("location")));
            schedule.setDescription(nvl(request.getParameter("description")));
            schedule.setVisibility(request.getParameter("visibility") == null ? "Y" : "N"); 
            
            String date = request.getParameter("demo");
            if (date != null && date.contains("~")) {
                String[] arr = date.split("~");
                if (arr.length >= 2) {
                    schedule.setStartDate(arr[0].trim());
                    schedule.setEndDate(arr[1].trim());
                }
            }
            
            String[] companions = request.getParameterValues("companions[]");  
            schedule.setTravelBuddies(companions);
            
            Part filePart = request.getPart("mainImage");
            String uploadPath = "D:/GDJ94/workspace/upload"; 
            
            scheduleService.editSchedule(schedule, filePart, uploadPath);

            System.out.println(schedule);

            response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + scheduleId);

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
        } 
	}
	
	private static String nvl(String s) {
        return (s == null) ? "" : s.trim();
    }
}