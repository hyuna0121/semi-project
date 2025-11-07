package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.travel.dto.DetailDTO;
import com.travel.service.DetailService;

@WebServlet("/getDetailsForDay")
public class GetDetailsServlet extends HttpServlet {
	private DetailService detailService;
	private Gson gson;
    
	@Override
	public void init() throws ServletException {
		this.detailService = new DetailService();
		this.gson = new Gson();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String dayKey = request.getParameter("day");
			Long scheduleId = Long.parseLong(request.getParameter("scheduleId"));
			
			Map<String, List<DetailDTO>> detailsMap = detailService.getGroupedDetails(scheduleId);
			
			List<DetailDTO> detailsForDay = detailsMap.get(dayKey);
			
			if (detailsForDay == null) {
				detailsForDay = Collections.emptyList();
			}
			
			response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = gson.toJson(detailsForDay);

            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
			
		} catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "일정 조회 중 오류 발생");
        }
	}

}
