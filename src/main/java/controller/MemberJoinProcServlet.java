package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DBUtil;

@WebServlet("/MemberJoinProc")
public class MemberJoinProcServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 인코딩
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 파라미터
        String name     = getParam(request, "name");
        String id       = getParam(request, "id");
        String nickname = getParam(request, "nickname");
        String birth    = getParam(request, "birth");
        String pass1    = getParam(request, "pass1");
        String pass2    = getParam(request, "pass2");
        String address  = getParam(request, "address");

        // 비밀번호 확인
        if (!pass1.equals(pass2)) {
            jsBack(out, "비밀번호가 일치하지 않습니다.");
            return;
        }

        // 생년월일 파싱(선택 입력 허용)
        java.sql.Date birthDate = null;
        if (!birth.isEmpty()) {
            // 숫자만 추출(전각/공백/구분자 제거)
            String digits = birth.replaceAll("\\s+", "").replaceAll("[^0-9]", "");
            if (digits.length() != 8) {
                jsBack(out, "생년월일은 8자리 숫자(YYYYMMDD)로 입력하세요.");
                return;
            }
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("uuuuMMdd")
                        .withResolverStyle(ResolverStyle.STRICT);
                LocalDate ld = LocalDate.parse(digits, f);
                birthDate = java.sql.Date.valueOf(ld);
            } catch (Exception e) {
                jsBack(out, "유효하지 않은 생년월일입니다.");
                return;
            }
        }

        // DB 작업: DBUtil.getConnection() 사용
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 아이디 중복
            if (exists(conn, "SELECT COUNT(*) FROM member WHERE id=?", id)) {
                jsBack(out, "이미 존재하는 아이디입니다.");
                conn.rollback();
                return;
            }
            // 닉네임 중복
            if (exists(conn, "SELECT COUNT(*) FROM member WHERE nickname=?", nickname)) {
                jsBack(out, "이미 존재하는 닉네임입니다.");
                conn.rollback();
                return;
            }

            // member INSERT
            String sqlMember = "INSERT INTO member(name, id, nickname, birth, password, address) "
                             + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlMember)) {
                ps.setString(1, name);
                ps.setString(2, id);
                ps.setString(3, nickname);
                if (birthDate != null) ps.setDate(4, birthDate); else ps.setNull(4, Types.DATE);
                ps.setString(5, pass1); // 실서비스: 반드시 해싱(BCrypt 등)
                ps.setString(6, address);
                ps.executeUpdate();
            }

            // users INSERT (동시 유지 필요 시)
            String sqlUsers = "INSERT INTO users(id, name, password) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlUsers)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, pass1); // 해싱 필요
                ps.executeUpdate();
            }

            conn.commit();
            out.println("<script>alert('회원가입 성공!'); location.href='login.jsp';</script>");

        } catch (SQLException e) {
            e.printStackTrace();
            // 제약조건 위반 등 케이스 메시지 보강 가능
            out.println("<script>alert('DB 오류가 발생했습니다.'); history.back();</script>");
        }
    }

    // 공통: 파라미터 null-safe
    private String getParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return (v != null) ? v.trim() : "";
    }

    // 공통: 존재여부 체크
    private boolean exists(Connection conn, String sql, String value) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // 공통: alert + back
    private void jsBack(PrintWriter out, String msg) {
        out.println("<script>alert('" + escape(msg) + "'); history.back();</script>");
    }

    // 간단 이스케이프
    private String escape(String s) {
        return s.replace("'", "\\'");
    }
}
