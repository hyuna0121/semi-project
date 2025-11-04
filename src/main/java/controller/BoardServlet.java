package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.travel.dao.ScheduleDAO;
import com.travel.dto.ScheduleDTO;
import util.DBUtil;

@WebServlet("/community/board")
public class BoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		String userId = (String) session.getAttribute("loginId");
		
		if (session == null || userId == null) {
		  response.sendRedirect(request.getContextPath() + "/login/login.jsp"); 
		  return;
		}
		

		List<ScheduleDTO> userSchedules = new ArrayList<>();
		ScheduleDTO selectedSchedule = null; 
		Connection conn = null;


		String selectedScheduleIdStr = request.getParameter("id");
		
		try {
			conn = DBUtil.getConnection();
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            

            userSchedules = scheduleDAO.getSchedulesByUserId(conn, userId);
            System.out.println("user : "+ userSchedules);

           
            if (selectedScheduleIdStr != null && !selectedScheduleIdStr.isEmpty()) {
            	long scheduleId = Long.parseLong(selectedScheduleIdStr);
            
            	selectedSchedule = scheduleDAO.selectSchedule(conn, scheduleId);
            } 

            else if (!userSchedules.isEmpty()) {
            	selectedSchedule = userSchedules.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace(); 
        } finally {
            if (conn != null) {
            	DBUtil.close(conn, null, null); 
            }
        }
		
		
		request.setAttribute("userSchedules", userSchedules);
		request.setAttribute("selectedSchedule", selectedSchedule);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/community/board.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}