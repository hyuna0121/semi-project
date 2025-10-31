package com.travel.dao;

import com.travel.dto.MemberDTO;
import util.DBUtil; // DB 연결 유틸리티 클래스
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// MemberDAO는 회원 정보 조회, 수정, 등록, 중복 체크 기능을 담당합니다.
public class MemberDAO {
    
    /**
     * 사용자 ID를 사용하여 회원 정보를 조회합니다.
     */
    public MemberDTO getMemberById(String id) throws SQLException {
        MemberDTO users = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // 🚨 테이블 이름: users
        String sql = "SELECT id, name, phone, email, address, gender, profile_image FROM users WHERE id = ?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
            	users = new MemberDTO();
            	users.setId(rs.getString("id"));
            	users.setName(rs.getString("name"));
            	users.setPhone(rs.getString("phone"));
            	users.setEmail(rs.getString("email"));
            	users.setAddress(rs.getString("address"));
            	users.setGender(rs.getString("gender"));
            	users.setProfileImage(rs.getString("profile_image"));
                
                // 참고: 비밀번호는 보안상 조회하지 않습니다.
            }
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return users;
    }

    /**
     * 회원 정보를 업데이트합니다.
     * @param users 업데이트할 회원 정보를 담은 DTO
     * @param newHashedPassword 새로운 비밀번호 (변경 없을 시 null)
     * @return 성공적으로 업데이트된 행의 수
     */
    public int updateMember(MemberDTO users, String newHashedPassword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        // 비밀번호 변경 여부 체크
        boolean isPasswordChange = newHashedPassword != null && !newHashedPassword.isEmpty();
        String sql = "";
        
        if (isPasswordChange) {
            // 🚨 테이블 이름: users, 비밀번호 포함 업데이트
            sql = "UPDATE users SET name=?, password=?, phone=?, email=?, address=?, profile_image=? WHERE id=?";
        } else {
            // 🚨 테이블 이름: users, 비밀번호 미포함 업데이트
            sql = "UPDATE users SET name=?, phone=?, email=?, address=?, profile_image=? WHERE id=?";
        }

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            int index = 1;
            pstmt.setString(index++, users.getName());
            
            if (isPasswordChange) {
                pstmt.setString(index++, newHashedPassword); // 암호화된 비밀번호
            }
            
            pstmt.setString(index++, users.getPhone());
            pstmt.setString(index++, users.getEmail());
            pstmt.setString(index++, users.getAddress());
            // 🚨 성별 수정은 mypage_profile.jsp에서 제외되었지만, DB 스키마에 있다면 여기에서 처리할 수 있습니다. 
            // 현재는 포함하지 않으므로 DTO에 gender가 설정되어 있지 않다고 가정합니다.
            
            pstmt.setString(index++, users.getProfileImage());
            pstmt.setString(index++, users.getId()); // WHERE 조건

            result = pstmt.executeUpdate();

        } finally {
            DBUtil.close(pstmt, conn);
        }
        
        return result;
    }

    /**
     * 새로운 회원 정보를 DB에 저장합니다. (회원가입 기능)
     * @param users 저장할 회원 정보를 담은 DTO
     * @return 성공적으로 삽입된 행의 수
     */
    public int insertMember(MemberDTO users) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
     
        String sql = "INSERT INTO users (id, name, password, phone, email, address, gender, profile_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            conn = DBUtil.getConnection(); 
            pstmt = conn.prepareStatement(sql);
            
            int index = 1;
            pstmt.setString(index++, users.getId());           // 1. id
            pstmt.setString(index++, users.getName());         // 2. name
            pstmt.setString(index++, users.getPassword());     // 3. password (암호화된 값 가정)
            pstmt.setString(index++, users.getPhone());        // 4. phone
            pstmt.setString(index++, users.getEmail());        // 5. email
            pstmt.setString(index++, users.getAddress());      // 6. address
            pstmt.setString(index++, users.getGender());       // 7. gender
            pstmt.setString(index++, users.getProfileImage());  // 8. profile_image
            
            result = pstmt.executeUpdate();

        } finally {
            DBUtil.close(pstmt, conn); 
        }
        
        return result;
    }
    
    /**
     * 회원가입 시 ID 중복을 체크합니다.
     * @param id 체크할 ID
     * @return true: 이미 존재함(중복), false: 사용 가능
     */
    public boolean isIdDuplicate(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // 🚨 테이블 이름: users
        String sql = "SELECT id FROM users WHERE id = ?"; 

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            
            return rs.next(); // 결과가 있으면 true (중복)
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
}