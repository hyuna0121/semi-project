package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.travel.dao.DetailDAO;

@WebServlet("/DeleteDetails")
public class DeleteDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		HttpSession session = request.getSession(false);
		String userId = (String) session.getAttribute("loginId");
				
		if (session == null || userId == null) {
		    response.sendRedirect(request.getContextPath() + "/login/login.jsp"); 
		    return; 
		}
		
		String id = request.getParameter("detail_id");
		if (id == null) {
			response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp"); 
			return; 
		}
		
		DetailDAO detailDAO = new DetailDAO();
		
		try (Connection conn = DBUtil.getConnection()) {
			Long detailId = Long.parseLong(id);
			
			long scheduleId = detailDAO.deleteDetail(conn, detailId);
			
			response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + scheduleId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/mainpage/mainpage.jsp");
		}
	}

}
