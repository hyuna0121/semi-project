package com.travel.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.travel.dto.ChatDTO;
import util.DBUtil; 

public class ChatDAO {


    public int insertComment(ChatDTO dto) {
        String sql = "INSERT INTO chat(schedule_id, user_id, comment) VALUES(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, dto.getSchedule_id());
            ps.setString(2, dto.getUser_id());
            ps.setString(3, dto.getcomment()); 
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    

    public int deleteComment(int commentId) { 
        String sql = "DELETE FROM chat WHERE comment_id = ?"; 
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, commentId); 
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int deleteCommentByScheduleId(Connection conn, Long scheduleId) throws SQLException { 
    	String sql = "DELETE FROM chat WHERE schedule_id = ?"; 
    	int result = 0;
    	
    	try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setLong(1, scheduleId); 
    		
    		result = pstmt.executeUpdate();
    		
    	} 
    	
    	return result;
    }

    public List<ChatDTO> getCommentsByScheduleId(int scheduleId) { 
        List<ChatDTO> list = new ArrayList<>();
        

        String sql = "SELECT c.*, SUBSTRING_INDEX(u.profile_image, '/', -1) AS profile_image " +
                "FROM chat c " +
                "LEFT JOIN users u ON c.user_id = u.id " + 
                "WHERE c.schedule_id = ? " +
                "ORDER BY c.created_at ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, scheduleId); 
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChatDTO dto = new ChatDTO();
                    dto.setComment_id(rs.getInt("comment_id"));
                    dto.setSchedule_id(rs.getInt("schedule_id"));
                    dto.setUser_id(rs.getString("user_id"));
                    dto.setcomment(rs.getString("comment")); 
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    dto.setProfile_image(rs.getString("profile_image"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // 🚨🚨🚨 [추가된 메서드]: 특정 사용자의 모든 댓글 조회 메서드 🚨🚨🚨
    public List<ChatDTO> getUserCommentsByUserId(String userId, int offset, int limit) { 
        List<ChatDTO> list = new ArrayList<>();

        String sql = "SELECT c.*, s.title AS schedule_title " + 
                "FROM chat c " +
                "LEFT JOIN schedules s ON c.schedule_id = s.id " + // ⬅️ LEFT JOIN 사용 확인
                "WHERE c.user_id = ? " +
                "ORDER BY c.created_at DESC " + 
                "LIMIT ? OFFSET ?"; 

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { 
            ps.setString(1, userId); 
            ps.setInt(2, limit);
            ps.setInt(3, offset); 
                
            try (ResultSet rs = ps.executeQuery()) { 
                while (rs.next()) {
                    ChatDTO dto = new ChatDTO();
                    dto.setComment_id(rs.getInt("comment_id"));
                    dto.setSchedule_id(rs.getInt("schedule_id"));
                    dto.setUser_id(rs.getString("user_id"));
                    dto.setcomment(rs.getString("comment")); 
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));                   
                    dto.setScheduleTitle(rs.getString("schedule_title")); 
                    
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int countUserComments(String userId) throws SQLException {
    	String sql = "SELECT COUNT(*) FROM chat WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
        	DBUtil.close(rs, ps, conn);
        }
        return 0;
    }
    // 🚨🚨🚨 [추가된 메서드 끝] 🚨🚨🚨
    
    public int updateComment(int commentId, String content) {
        String sql = "UPDATE chat SET comment = ?, updated_at = NOW() WHERE comment_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, content);
            ps.setInt(2, commentId);
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public ChatDTO getCommentById(int commentId) {
        String sql = "SELECT * FROM chat WHERE comment_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChatDTO dto = new ChatDTO();
                    dto.setComment_id(rs.getInt("comment_id"));
                    dto.setSchedule_id(rs.getInt("schedule_id"));
                    dto.setUser_id(rs.getString("user_id"));
                    dto.setcomment(rs.getString("comment"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    public String getProfileImageByUserId(String userId){
		String sql ="SELECT SUBSTRING_INDEX(profile_image, '/', -1) AS profile_image " +
                "FROM users WHERE id = ?";
		String profileImg = null;
		
		try (Connection conn = DBUtil.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            
	            ps.setString(1, userId);
	            
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    profileImg = rs.getString("profile_image");
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return profileImg;
	}

}