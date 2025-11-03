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
        // 🚨 SQL 수정: password_update_count 컬럼 추가 조회
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
        // 🚨 SQL 수정: READ COMMITTED 설정 덕분에 단순 조회로 복귀
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
        
        // SQL 빌더 (카운트 증가 로직 포함)
        StringBuilder sql = new StringBuilder("UPDATE users SET name=?, phone=?, email=?, address=?, gender=?, profile_image=?");
        
        if (newPassword != null && !newPassword.isEmpty()) {
            sql.append(", password=?, password_update_count = password_update_count + 1"); 
        }
        sql.append(" WHERE id=?");

        try {
            conn = DBUtil.getConnection(); 
            // 🚨🚨 핵심: 트랜잭션 시작 (AutoCommit OFF)
            conn.setAutoCommit(false); 

            pstmt = conn.prepareStatement(sql.toString());
            
            int index = 1;
            
            // 파라미터 바인딩
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
            
            // 🚨🚨 핵심: 성공 시 커밋, 실패 시 롤백
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
                conn.setAutoCommit(true); // 상태 복구
            }
            DBUtil.close(pstmt, conn); 
        }
        
        return result;
    }
    
    /**
     * 새로운 회원 정보를 DB에 저장합니다. (기존 로직 유지)
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
    
    /**
     * 회원가입 시 ID 중복을 체크합니다. (기존 로직 유지)
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