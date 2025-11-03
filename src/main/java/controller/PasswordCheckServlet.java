package controller;

import com.travel.dao.MemberDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson; 
import java.util.logging.Logger; // 🚨 Logger import
import java.util.logging.Level;

@WebServlet("/mypage/PasswordCheckServlet")
public class PasswordCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PasswordCheckServlet.class.getName()); // 🚨 Logger 객체

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String userId = request.getParameter("userId");
        String enteredPassword = request.getParameter("password");
        
        boolean isMatch = false; 
        Gson gson = new Gson();

        if (userId == null || enteredPassword == null || userId.isEmpty()) {
            out.print("{\"isMatch\": false}");
            return;
        }

        // 🚨 공백 제거
        enteredPassword = enteredPassword.trim();

        MemberDAO dao = new MemberDAO();
        try {
            String storedPassword = dao.getPasswordHash(userId); 

            // 🚨 DB 값 공백 제거
            if (storedPassword != null) {
                storedPassword = storedPassword.trim();
            }
            
            // 🚨🚨 디버그 로그: 서버가 읽은 값과 입력된 값 확인 (Logger 사용)
            logger.info("CHECK: DB Password: [" + storedPassword + "], Entered Password: [" + enteredPassword + "]"); 
            

            // 비밀번호 비교
            if (storedPassword != null && storedPassword.equals(enteredPassword)) {
                 isMatch = true;
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during password check.", e);
        }

        Map<String, Boolean> result = new HashMap<>();
        result.put("isMatch", isMatch);
        out.print(gson.toJson(result));
        out.flush();
    }
}