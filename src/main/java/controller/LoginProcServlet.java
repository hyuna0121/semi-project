package controller;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DBUtil;

@WebServlet("/LoginProc")
public class LoginProcServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 파라미터 가져오기
        String id = getParam(request, "id");
        String pwd = getParam(request, "pwd");

        // 입력값 검증
        if (id.isEmpty() || pwd.isEmpty()) {
            out.println("<script>alert('아이디와 비밀번호를 입력해주세요.'); window.location.href='Login';</script>");
            return;
        }


    

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT name FROM users WHERE id=? AND password=?");
            ) {
                pstmt.setString(1, id);
                pstmt.setString(2, pwd);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");

                        HttpSession session = request.getSession();
                        session.setAttribute("loginId", id);
                        session.setAttribute("loginName", name);

                        response.sendRedirect("MainPage.jsp");
                        return;
                    } else {
                        out.println("<script>alert('로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.'); window.location.href='Login';</script>");
                        return;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('DB 오류 발생'); window.location.href='Login';</script>");
        }
    }

    // null-safe 파라미터 처리
    private String getParam(HttpServletRequest req, String name) {
        String val = req.getParameter(name);
        return (val != null) ? val.trim() : "";
    }
}
