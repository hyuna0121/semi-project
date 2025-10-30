package com.example.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/NickCheck")
public class NickCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 요청 인코딩 및 응답 타입 설정
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String nickname = request.getParameter("nickname");
        PrintWriter out = response.getWriter();

        // 닉네임이 비어 있으면 처리 중단
        if (nickname == null || nickname.trim().isEmpty()) {
            out.print("<span style='color:red;'>닉네임을 입력해주세요 ⚠️</span>");
            return;
        }

        // DB 연결 정보
        String url = "jdbc:mysql://localhost:3306/memberjoin";
        String dbUser = "root";
        String dbPass = "test1234";
        String sql = "SELECT nickname FROM member WHERE nickname = ?";

        boolean exists = false;

        try (
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("<span style='color:red;'>서버 오류 발생 ❌</span>");
            return;
        }

        // 결과 출력 (AJAX 응답)
        if (exists) {
            out.print("<span style='color:red;'>이미 사용 중인 닉네임입니다 ❌</span>");
        } else {
            out.print("<span style='color:green;'>사용 가능한 닉네임입니다 ✅</span>");
        }
    }
}
