package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.DBUtil;

@WebServlet("/IdCheck")
public class IdcheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String ID_PATTERN = "^[A-Za-z0-9_.@-]{4,20}$";
    private static final String SQL_EXISTS  = "SELECT 1 FROM users WHERE id = ? LIMIT 1";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        PrintWriter out = resp.getWriter();
        String id = req.getParameter("id");
        id = (id == null) ? "" : id.trim();

        if (id.isEmpty()) {
            out.print("<span style='color:#ef4444'>아이디를 입력해주세요 ⚠️</span>");
            return;
        }
        if (!id.matches(ID_PATTERN)) {
            out.print("<span style='color:#ef4444'>아이디는 4~20자, 영문/숫자/._-@ 만 가능합니다.</span>");
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                boolean exists = rs.next();
                if (exists) {
                    out.print("<span style='color:#ef4444'>이미 사용 중인 아이디입니다 ❌</span>");
                } else {
                    out.print("<span style='color:#16a34a'>사용 가능한 아이디입니다 ✅</span>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            out.print("<span style='color:#ef4444'>서버 오류 발생 ❌</span>");
        }
    }
}
