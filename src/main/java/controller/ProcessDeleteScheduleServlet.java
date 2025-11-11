package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import com.travel.dao.ScheduleDAO;
import com.travel.dto.ScheduleDTO;
import util.DBUtil;

@WebServlet("/processDeleteSchedule")
public class ProcessDeleteScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		String userId = (String) session.getAttribute("loginId");
		
		if (session == null || userId == null) {
		    response.sendRedirect(request.getContextPath() + "/login/login.jsp"); 
		    return; 
		}
		
		long scheduleId = Long.parseLong(request.getParameter("schedule_id"));
		ScheduleDAO scheduleDAO = new ScheduleDAO();
		
		try (Connection conn = DBUtil.getConnection()) {
            ScheduleDTO schedule = scheduleDAO.selectSchedule(conn, scheduleId);

            if (schedule == null) {
                throw new Exception("삭제할 일정이 없습니다.");
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

            int result = scheduleDAO.deleteSchedule(conn, scheduleId); 
            
            if (result > 0) {
            	response.setContentType("text/html;charset=UTF-8");
            	
            	PrintWriter out = response.getWriter();
            	
            	out.println("<script>");
            	out.println("alert('일정이 삭제되었습니다.');");
            	out.println("location.href='" + request.getContextPath() + "/mainpage/mainpage.jsp';");
            	out.println("</script>");
            	out.flush();
            	
            	return;
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
        }
	}
}