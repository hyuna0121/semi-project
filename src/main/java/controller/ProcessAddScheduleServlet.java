package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.travel.dto.ScheduleDTO;
import com.travel.service.ScheduleService;

@WebServlet("/processAddSchedule")
@MultipartConfig(
    fileSizeThreshold = 1 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class ProcessAddScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ScheduleService scheduleService = new ScheduleService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("loginId") : null;
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login/login.jsp");
            return;
        }

        // 폼 파라미터 매핑
        ScheduleDTO schedule = new ScheduleDTO();
        schedule.setUserId(userId);
        schedule.setTitle(nvl(request.getParameter("title")));
        schedule.setLocation(nvl(request.getParameter("location")));
        schedule.setDescription(nvl(request.getParameter("description")));

        // 체크박스: 체크됨=비공개(N), 체크안됨=공개(Y)
        schedule.setVisibility(request.getParameter("visibility") == null ? "Y" : "N");

        // 동행인 배열
        String[] companions = request.getParameterValues("companions[]");  
        schedule.setTravelBuddies(companions);

        // 날짜(예: "2025-11-05 ~ 2025-11-07")
        String date = request.getParameter("demo");
        if (date != null && date.contains("~")) {
            String[] arr = date.split("~");
            if (arr.length >= 2) {
                schedule.setStartDate(arr[0].trim());
                schedule.setEndDate(arr[1].trim());
            }
        }

        Part filePart = request.getPart("mainImage");

        // ✅ 업로드 경로 선택 우선순위:
        // 1) web.xml 의 uploadBaseDir
        // 2) 웹앱 내부 /upload (realPath)
        // 3) 폴백: D:/GDJ94/workspace/upload
        String uploadPath = "D:/GDJ94/workspace/upload";

        long scheduleId;
        try {
            scheduleId = scheduleService.addSchedule(schedule, filePart, uploadPath);
            System.out.println("[ProcessAddSchedule] saved scheduleId = " + scheduleId);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "스케줄 저장 중 오류: " + e.getMessage());
            request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/schedule/schedule.jsp?schedule_id=" + scheduleId);
    }

    private static String nvl(String s) {
        return (s == null) ? "" : s.trim();
    }

}
