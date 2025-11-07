package com.travel.servlet;

import java.io.IOException;
import java.sql.Connection;

import com.travel.dao.UserDAO;
import com.travel.dao.UserDAO.UserLite;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DBUtil;

@WebServlet("/friend/find")
public class FriendFindServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String q = request.getParameter("q");
        if (q == null || q.trim().isEmpty()) {
            response.getWriter().write("{\"found\":false,\"message\":\"검색어가 비었습니다.\"}");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            UserDAO dao = new UserDAO();
            UserDAO.UserLite u = dao.findByIdOrNickname(conn, q.trim());
            if (u == null) {
                response.getWriter().write("{\"found\":false,\"message\":\"해당 사용자를 찾을 수 없습니다.\"}");
                return;
            }

            String idRaw       = nvl(u.getId());
            String nameRaw     = nvl(u.getName());
            String nicknameRaw = nvl(u.getNickname());
            String profileRaw  = nvl(u.getProfileImage()); // 파일명/상대경로/절대경로/절대URL 등

            String profileUrl  = buildProfileUrl(request, profileRaw);  // ✅ 여기만 바뀜

            StringBuilder sb = new StringBuilder();
            sb.append("{\"found\":true,\"user\":{");
            sb.append("\"id\":\"").append(escapeJson(idRaw)).append("\",");
            sb.append("\"name\":\"").append(escapeJson(nameRaw)).append("\",");
            sb.append("\"nickname\":\"").append(escapeJson(nicknameRaw)).append("\",");
            sb.append("\"profileImage\":\"").append(escapeJson(profileRaw)).append("\",");
            sb.append("\"profileUrl\":\"").append(escapeJson(profileUrl)).append("\"");
            sb.append("}}");

            response.getWriter().write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"found\":false,\"message\":\"서버 오류\"}");
        }
    }

    // ✅ base를 /mypage/image 로 고정. DB 값이 어떤 형태든 파일명만 뽑아 붙입니다.
    private static String buildProfileUrl(HttpServletRequest request, String profile) {
        String ctx = request.getContextPath();
        String p = nvl(profile).trim();
        if (p.isEmpty()) return ctx + "/mypage/image/default_profile.png";

        // 절대 URL은 그대로 사용
        String low = p.toLowerCase();
        if (low.startsWith("http://") || low.startsWith("https://")) return p;

        // 윈도우 경로/유닉스 경로 모두에서 "파일명"만 추출
        p = p.replace('\\', '/');
        int idx = p.lastIndexOf('/');
        String filename = (idx >= 0) ? p.substring(idx + 1) : p;

        // 빈 파일명 방어
        if (filename.isEmpty()) filename = "default_profile.png";

        // 최종 URL: /컨텍스트/mypage/image/파일명
        return ctx + "/mypage/image/" + filename;
    }

    private static String nvl(String s){ return (s==null) ? "" : s; }

    private static String escapeJson(String s){
        StringBuilder out = new StringBuilder(s.length()+16);
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            switch(c){
                case '\"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b");  break;
                case '\f': out.append("\\f");  break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x",(int)c));
                    else out.append(c);
            }
        }
        return out.toString();
    }
}
