package com.travel.dao;

import com.travel.dto.MemberDTO;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO {
    
    /**
     * 사용자 ID를 기반으로 회원 정보를 조회합니다.
     */
    public MemberDTO getMemberById(String userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MemberDTO member = null;
        String sql = "SELECT id, name, password, email, phone, address, gender, profile_image, password_update_count FROM users WHERE id = ?"; 

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                member = new MemberDTO();
                member.setId(rs.getString("id"));
                member.setName(rs.getString("name"));
                member.setPassword(rs.getString("password")); 
                member.setEmail(rs.getString("email"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                member.setGender(rs.getString("gender"));
                member.setProfileImage(rs.getString("profile_image"));
                member.setPasswordUpdateCount(rs.getInt("password_update_count"));
            }
            return member;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }


    /**
     * 특정 사용자의 저장된 비밀번호 (평문)을 조회합니다.
     */
    public String getPasswordHash(String userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT password FROM users WHERE id = ?"; 

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("password");
            }
            return null;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }


    /**
     * 회원 정보를 업데이트합니다. (성공 시 passwordUpdateCount 증가 포함)
     */
    public int updateMember(MemberDTO member, String newPassword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        StringBuilder sql = new StringBuilder("UPDATE users SET name=?, phone=?, email=?, address=?, gender=?, profile_image=?");
        
        if (newPassword != null && !newPassword.isEmpty()) {
            sql.append(", password=?, password_update_count = password_update_count + 1"); 
        }
        sql.append(" WHERE id=?");

        try {
            conn = DBUtil.getConnection(); 
            conn.setAutoCommit(false); 

            pstmt = conn.prepareStatement(sql.toString());
            
            int index = 1;
            
            pstmt.setString(index++, member.getName());      
            pstmt.setString(index++, member.getPhone());     
            pstmt.setString(index++, member.getEmail());     
            pstmt.setString(index++, member.getAddress());   
            pstmt.setString(index++, member.getGender());
            pstmt.setString(index++, member.getProfileImage());
            
            if (newPassword != null && !newPassword.isEmpty()) {
                pstmt.setString(index++, newPassword); 
            }

            pstmt.setString(index, member.getId());          
            
            result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit(); 
            } else {
                conn.rollback(); 
            }

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); 
            }
            DBUtil.close(pstmt, conn); 
        }
        
        return result;
    }
    
    /**
     * 새로운 회원 정보를 DB에 저장합니다. 
     */
    public int insertMember(MemberDTO member) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        String sql = "INSERT INTO users (id, name, password, phone, email, address, gender, profile_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            conn = DBUtil.getConnection(); 
            pstmt = conn.prepareStatement(sql);
            
            int index = 1;
            pstmt.setString(index++, member.getId());           
            pstmt.setString(index++, member.getName());         
            pstmt.setString(index++, member.getPassword());     
            pstmt.setString(index++, member.getPhone());        
            pstmt.setString(index++, member.getEmail());        
            pstmt.setString(index++, member.getAddress());      
            pstmt.setString(index++, member.getGender());       
            pstmt.setString(index++, member.getProfileImage());  
            
            result = pstmt.executeUpdate();

        } finally {
            DBUtil.close(pstmt, conn); 
        }
        
        return result;
    }
    
    public boolean updateProfileInfo(String id, String password, String name, String address, String phone,
            String email, String gender, String profileImagePath) {
		String sql = "UPDATE users SET name=?, password=?, address=?, phone=?, email=?, gender=?, profile_image=? WHERE id=?";
			try (Connection con = DBUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, name);
				ps.setString(2, password);
				ps.setString(3, address);
				ps.setString(4, phone);
				ps.setString(5, email);
				ps.setString(6, gender);
				ps.setString(7, profileImagePath);
				ps.setString(8, id);
				return ps.executeUpdate() == 1;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
		}
}

    
    /**
     * 회원가입 시 ID 중복을 체크합니다. 
     */
    public boolean isIdDuplicate(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT id FROM users WHERE id = ?"; 

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            
            return rs.next();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
}