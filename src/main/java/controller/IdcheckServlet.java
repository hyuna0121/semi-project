package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DBUtil;

@WebServlet("/IdCheck")
public class IdcheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 인코딩 및 응답 타입 설정
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        PrintWriter out = response.getWriter();
        String id = request.getParameter("id");

        // 입력값 검증
        if (id == null || id.trim().isEmpty()) {
            out.print("<span style='color:red;'>아이디를 입력해주세요 ⚠️</span>");
            return;
        }

  

        boolean exists = false;

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
            ) {
                pstmt.setString(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("<span style='color:red;'>서버 오류 발생 ❌</span>");
            return;
        }

        // 결과 출력
        if (exists) {
            out.print("<span style='color:red;'>이미 사용 중인 아이디입니다 ❌</span>");
        } else {
            out.print("<span style='color:green;'>사용 가능한 아이디입니다 ✅</span>");
        }
    }
}
