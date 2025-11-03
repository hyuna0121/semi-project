package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.DBUtil;

@WebServlet("/NickCheck")
public class NickCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SQL_EXISTS = "SELECT 1 FROM users WHERE nickname = ? LIMIT 1";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        PrintWriter out = resp.getWriter();
        String nickname = req.getParameter("nickname");
        nickname = (nickname == null) ? "" : nickname.trim();

        if (nickname.isEmpty()) {
            out.print("<span style='color:#ef4444'>닉네임을 입력해주세요 ⚠️</span>");
            return;
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            out.print("<span style='color:#ef4444'>닉네임은 2~20자로 입력하세요.</span>");
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                boolean exists = rs.next();
                if (exists) {
                    out.print("<span style='color:#ef4444'>이미 사용 중인 닉네임입니다 ❌</span>");
                } else {
                    out.print("<span style='color:#16a34a'>사용 가능한 닉네임입니다 ✅</span>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            out.print("<span style='color:#ef4444'>서버 오류 발생 ❌</span>");
        }
    }
}
