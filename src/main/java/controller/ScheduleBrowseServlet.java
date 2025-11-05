package controller;

import com.travel.dao.ScheduleListDAO;
import com.travel.dto.ScheduleDTO;
import util.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/schedules")
public class ScheduleBrowseServlet extends HttpServlet {
    private static final int PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
        int offset = (page - 1) * PAGE_SIZE;

        try (Connection conn = DBUtil.getConnection()) {
            ScheduleListDAO dao = new ScheduleListDAO();
            int total = dao.countAll(conn);
            List<ScheduleDTO> list = dao.findAll(conn, offset, PAGE_SIZE);

            req.setAttribute("list", list);
            req.setAttribute("page", page);
            req.setAttribute("pages", (int)Math.ceil(total / (double) PAGE_SIZE));

            req.getRequestDispatcher("/schedule/browse.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
