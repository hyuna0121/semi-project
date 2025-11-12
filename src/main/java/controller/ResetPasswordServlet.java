package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DBUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
		} catch (Exception e) {
			System.out.println("json을 읽는 도중 오류 발생");
			e.printStackTrace();
			sendJsonResponse(response, "error", "잘못된 요청 형식입니다.", null);
			return;
		}
		
		Gson gson = new Gson();
		JsonObject jsonRequest = gson.fromJson(jb.toString(), JsonObject.class);
		
		String id = jsonRequest.get("id").getAsString();
		String email = jsonRequest.get("email").getAsString();
		
		boolean userExists = false;
		String sql = "SELECT COUNT(*) FROM users WHERE id = ? AND email = ?";
		
		try(Connection conn = DBUtil.getConnection()) {			
			try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, id);
				pstmt.setString(2, email);
				
				ResultSet rs = pstmt.executeQuery();
				
				if (rs.next()) {
					if (rs.getInt(1) > 0) {
						userExists = true;
					}
				}
				
				rs.close();
			}
			
			if (userExists) {
				String tempPassword = generateTemporaryPw(8);
				String hashedPw = BCrypt.hashpw(tempPassword, BCrypt.gensalt());
				String updateSql = "UPDATE users SET password = ? WHERE id = ? AND email = ?";
				
				try(PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
					pstmt.setString(1, hashedPw);
					pstmt.setString(2, id);
					pstmt.setString(3, email);
					
					int update = pstmt.executeUpdate();
					
					if (update > 0) {
						sendJsonResponse(response, "success", "임시 비밀번호가 발급되었습니다.", tempPassword);
					} else {
						sendJsonResponse(response, "error", "비밀번호 업데이트에 실패했습니다.", null);
					}
				}
			} else {
				sendJsonResponse(response, "error", "아이디와 이메일이 일치하는 계정을 찾을 수 없습니다.", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sendJsonResponse(response, "error", "데이터베이스 오류가 발생했습니다.", null);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			sendJsonResponse(response, "error", "서버 내부 오류가 발생했습니다.", null);
			return;
		}
		
	}
	
	private String generateTemporaryPw(int length) {
		String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        
        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();
        
        if (length < 1) throw new IllegalArgumentException();
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        
        return sb.toString();
	}

	private void sendJsonResponse(HttpServletResponse response, String status, String message, String tempPassword) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		Map<String, String> responseMap = new HashMap<>();
		responseMap.put("status", status);
		responseMap.put("message", message);
		
		if (status.equals("success")) {
			responseMap.put("tempPassword", tempPassword);
		}
		
		String jsonResponse = new Gson().toJson(responseMap);
		
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
		out.flush();
	}

}
