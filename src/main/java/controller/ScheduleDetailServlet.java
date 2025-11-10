package controller;

import com.travel.service.DetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;
import com.travel.dto.DetailDTO;
// 🚨 JSON 변환을 위해 GSON 라이브러리 사용을 가정합니다. (라이브러리 추가 필요)
// GSON을 사용하지 않으려면 수동으로 JSON 문자열을 생성해야 합니다.
import com.google.gson.Gson; 

@WebServlet("/schedule/details")
public class ScheduleDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        long scheduleId = 0;
        try {
            scheduleId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            out.print("{\"error\":\"Invalid schedule ID\"}");
            return;
        }

        DetailService service = new DetailService();
        
        // 1. Service를 통해 상세 일정을 날짜별로 그룹화하여 조회
        Map<String, List<DetailDTO>> groupedDetails = service.getGroupedDetails(scheduleId);
        
        // 2. 결과를 JSON으로 변환하여 클라이언트에 전송
        String jsonResponse = gson.toJson(groupedDetails);
        out.print(jsonResponse);
    }
}