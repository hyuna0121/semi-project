package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
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
		
		Connection conn = null;
		ScheduleDAO dao = new ScheduleDAO();
		
		try {

            long scheduleId = Long.parseLong(request.getParameter("scheduleId"));


            conn = DBUtil.getConnection();
            ScheduleDTO schedule = dao.selectSchedule(conn, scheduleId);

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

                 response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
                 return;
            }


            dao.deleteSchedule(conn, scheduleId); 
            response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
        } finally {
            DBUtil.close(conn, null, null);
        }
	}
}