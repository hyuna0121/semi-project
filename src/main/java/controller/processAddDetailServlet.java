package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.dao.DetailDAO;
import com.travel.dto.DetailDTO;

@WebServlet("/processAddDetail")
@MultipartConfig(
	    fileSizeThreshold = 1 * 1024 * 1024,
	    maxFileSize = 10 * 1024 * 1024,
	    maxRequestSize = 50 * 1024 * 1024
	)
public class processAddDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<DetailDTO> detailList = new ArrayList<DetailDTO>();
		DetailDAO detailDAO = new DetailDAO();
		
		long scheduleId = Long.parseLong(request.getParameter("schedule_id"));
		String scheduleTime = request.getParameter("scheduleTime");
		
		String memo = request.getParameter("memo");
		String category = request.getParameter("category");
		String place = request.getParameter("placeName");
		
		Double latitude = Double.parseDouble(request.getParameter("latitude"));
		Double longitude = Double.parseDouble(request.getParameter("longitude"));
		
		String[] selectedDates = request.getParameterValues("selectedDates");
		
		for (String date : selectedDates) {
			DetailDTO detail = new DetailDTO();
			
			detail.setScheduleId(scheduleId);
			detail.setStartTime(scheduleTime);
			detail.setMemo(memo);
			detail.setCategory(category);
			detail.setPlace(place);
			detail.setLatitude(latitude);
			detail.setLongitude(longitude);
			detail.setDate(date);
			
			detailList.add(detail);
		}
		
		boolean success = false;
		
		try (Connection conn = DBUtil.getConnection()) {
			detailDAO.insertDetail(conn, detailList);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HttpSession session = request.getSession();
		
		if (!success) {
			session.setAttribute("flashMessage", "오류가 발생했습니다. 다시 시도해주세요.");
	        session.setAttribute("flashType", "danger");
		} 
		
		response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + scheduleId);
	}

}
