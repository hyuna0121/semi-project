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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/findId")
public class FindIdServlet extends HttpServlet {
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
		
		String email = jsonRequest.get("email").getAsString();
		String name = jsonRequest.get("name").getAsString();
		
		String foundId = null;
		String sql = "SELECT id FROM users WHERE email = ? AND name = ?";
		
		try(Connection conn = DBUtil.getConnection()) {			
			try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, email);
				pstmt.setString(2, name);
				
				ResultSet rs = pstmt.executeQuery();
				
				if (rs.next()) {
					foundId = rs.getString("id");
				}
				
				rs.close();
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
		
		if (foundId != null) {
			sendJsonResponse(response, "success", "아이디를 찾았습니다.", foundId);
		} else {
			sendJsonResponse(response, "error", "일치하는 사용자가 없습니다.", null);
		}
	}

	private void sendJsonResponse(HttpServletResponse response, String status, String message, String userId) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		Map<String, String> responseMap = new HashMap<>();
		responseMap.put("status", status);
		
		if (status.equals("success")) {
			responseMap.put("userId", userId);
		} else {
			responseMap.put("message", message);
		}
		
		String jsonResponse = new Gson().toJson(responseMap);
		
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
		out.flush();
	}

}
