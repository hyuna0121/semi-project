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


import com.travel.dao.ChatDAO;
import com.travel.dto.ChatDTO;

import com.travel.dao.ScheduleDAO;
import com.travel.dto.ScheduleDTO;


@WebServlet("/commentAction") 
public class CommentActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginId") == null) {
            response.sendRedirect(request.getContextPath() + "/login/login.jsp");
            return;
        }
        String userId = (String) session.getAttribute("loginId");


        int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
  
        String action = request.getParameter("action");
        
        ChatDAO dao = new ChatDAO();
        
        try {
            if ("insert".equals(action)) {
            
                String content = request.getParameter("content");
                
                boolean isBuddy = false;
   
                try (Connection conn = DBUtil.getConnection()) {
                	ScheduleDAO scheduledao = new ScheduleDAO();
                	ScheduleDTO schedule = scheduledao.selectSchedule(conn, scheduleId);
                
                	
                	ChatDTO dto = new ChatDTO();
                	dto.setSchedule_id(scheduleId);
                	dto.setUser_id(userId); 
                	dto.setcomment(content); 

                
                
					
					if(schedule != null) {
							if(userId.equals(schedule.getUserId())) {
							isBuddy = true;
							}
					if(isBuddy == false) {
						for(String buddy : schedule.getTravelBuddies()) {
							if(buddy.equals(userId)) {
								isBuddy = true;
								break;
							}		
						}
					}
				}
           }
                
           if(isBuddy) {
           ChatDTO dto = new ChatDTO();
           dto.setcomment(content);
           dto.setSchedule_id(scheduleId);
           dto.setUser_id(userId);

           dao.insertComment(dto);
           }

            } else if ("delete".equals(action)) {
                int commentId = Integer.parseInt(request.getParameter("commentId"));

                ChatDTO comment = dao.getCommentById(commentId);
                if (comment != null && comment.getUser_id().equals(userId)) {
                    dao.deleteComment(commentId);
                }

            } else if ("update".equals(action)) {
                int commentId = Integer.parseInt(request.getParameter("commentId"));
                String content = request.getParameter("content");

                ChatDTO comment = dao.getCommentById(commentId);
                if (comment != null && comment.getUser_id().equals(userId)) {
                    dao.updateComment(commentId, content);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }


        response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + scheduleId + "#comment-section");
    }
}