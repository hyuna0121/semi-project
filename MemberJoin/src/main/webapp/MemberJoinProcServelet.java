package com.example.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MemberJoinProc")
public class MemberJoinProcServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 인코딩
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 폼 데이터 받기 (null-safe)
        String name = getParam(request, "name");
        String id = getParam(request, "id");
        String nickname = getParam(request, "nickname");
        String birth = getParam(request, "birth");
        String pass1 = getParam(request, "pass1");
        String pass2 = getParam(request, "pass2");
        String address = getParam(request, "address");

        // 비밀번호 일치 확인
        if (!pass1.equals(pass2)) {
            out.println("<script>alert('비밀번호가 일치하지 않습니다.'); history.back();</script>");
            return;
        }

        // 생년월일 처리
        java.sql.Date birthDate = null;
        if (!birth.isEmpty()) {
            if (birth.matches("\\d{8}")) {
                String formatted = birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
                birthDate = java.sql.Date.valueOf(formatted);
            } else {
                out.println("<script>alert('생년월일을 8자리 숫자로 입력해주세요.'); history.back();</script>");
                return;
            }
        }

        // DB 연결 정보
        String url = "jdbc:mysql://localhost:3306/memberjoin";
        String dbUser = "root";
        String dbPass = "test1234";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPass);

            // 아이디 중복 확인
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM member WHERE id=?");
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                out.println("<script>alert('이미 존재하는 아이디입니다.'); history.back();</script>");
                close(rs, pstmt, conn);
                return;
            }
            close(rs, pstmt, null);

            // 닉네임 중복 확인
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM member WHERE nickname=?");
            pstmt.setString(1, nickname);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                out.println("<script>alert('이미 존재하는 닉네임입니다.'); history.back();</script>");
                close(rs, pstmt, conn);
                return;
            }
            close(rs, pstmt, null);

            // member 테이블 INSERT
            pstmt = conn.prepareStatement(
                    "INSERT INTO member(name, id, nickname, birth, password, address) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            pstmt.setString(3, nickname);
            pstmt.setDate(4, birthDate);
            pstmt.setString(5, pass1); // 실제 서비스에서는 비밀번호 해싱 필요
            pstmt.setString(6, address);

            int result = pstmt.executeUpdate();
            pstmt.close();

            // users 테이블 INSERT
            if (result > 0) {
                pstmt = conn.prepareStatement("INSERT INTO users(id, name, password) VALUES (?, ?, ?)");
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, pass1);
                pstmt.executeUpdate();
                pstmt.close();

                out.println("<script>alert('회원가입 성공!'); window.location.href='login.jsp';</script>");
            } else {
                out.println("<script>alert('회원가입 실패'); history.back();</script>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('DB 오류 발생'); history.back();</script>");
        } finally {
            close(rs, pstmt, conn);
        }
    }

    // 파라미터 null-safe 처리
    private String getParam(HttpServletRequest req, String name) {
        String val = req.getParameter(name);
        return (val != null) ? val.trim() : "";
    }

    // 리소스 닫기
    private void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }
}
