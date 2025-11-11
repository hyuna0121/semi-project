package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.DBUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.dao.DetailDAO;
import com.travel.dao.ScheduleDAO;
import com.travel.dto.DetailDTO;
import com.travel.dto.ScheduleDTO;


@WebServlet("/copySchedule")
public class CopyScheduleServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        Connection conn = null;

        try {
        	long scheduleIdToCopy = Long.parseLong(request.getParameter("schedule_id_to_copy"));
        	String currentUserId = (String) request.getSession().getAttribute("loginId");
        	
        	if(currentUserId == null) {
        		response.setContentType("text/html; charset=UTF-8");
        		PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('로그인이 필요합니다.');");
                out.println("location.href = '" + request.getContextPath() + "/login/login.jsp';"); // "로그인" "페이지"로
                out.println("</script>");
                out.close();
                return; 
        	}
        	
        	conn = DBUtil.getConnection();
        	conn.setAutoCommit(false);
        	ScheduleDAO scheduleDAO = new ScheduleDAO();
        	DetailDAO detailDAO = new DetailDAO();
        	
        	ScheduleDTO originalSchedule = scheduleDAO.selectSchedule(conn, scheduleIdToCopy);
        	
        	ScheduleDTO newSchedule = new ScheduleDTO();
        	newSchedule.setUserId(currentUserId);
        	newSchedule.setTitle(originalSchedule.getTitle() + " (복사본)");
        	newSchedule.setLocation(originalSchedule.getLocation());
        	newSchedule.setStartDate(originalSchedule.getStartDate());
        	newSchedule.setEndDate(originalSchedule.getEndDate());
        	newSchedule.setDescription(originalSchedule.getDescription());
        	newSchedule.setVisibility("N");
        	
        	long newScheduleId = scheduleDAO.insertSchedule(conn, newSchedule);
        	
        	List<DetailDTO> originalDetails = detailDAO.selectDetails(conn, scheduleIdToCopy);
        	List<DetailDTO> newDetailsList = new ArrayList<DetailDTO>();
        	if(originalDetails != null) {
        		for(DetailDTO detailDTO : originalDetails) {
        			DetailDTO newDetail = new DetailDTO();
                    newDetail.setScheduleId(newScheduleId); 
                    newDetail.setDate(detailDTO.getDate()); 
                    newDetail.setPlace(detailDTO.getPlace());
                    newDetail.setLatitude(detailDTO.getLatitude());
                    newDetail.setLongitude(detailDTO.getLongitude());
                    newDetail.setStartTime(detailDTO.getStartTime()); 
                    newDetail.setCategory(detailDTO.getCategory());
                    newDetail.setMemo(detailDTO.getMemo());

                    newDetailsList.add(newDetail);
        		}
        	}
        	
        	if(!newDetailsList.isEmpty()) {
        		detailDAO.insertDetailCopy(conn, newDetailsList);
        	}
        	
        	scheduleDAO.insertMembers(conn, newScheduleId, currentUserId, null);
        	
        	conn.commit();
        	
        	response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + newScheduleId);
        } catch (Exception e) {
            e.printStackTrace();
            
            if(conn != null) {
            	try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
            }
           
        } finally {
        	
        	if(conn != null) {
        		try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
			DBUtil.close(conn);
		}

    }

 
}
