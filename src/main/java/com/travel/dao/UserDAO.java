package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public static class UserLite {
        private String id;
        private String name;
        private String nickname;
        private String profileImage;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getNickname() { return nickname; }
        public String getProfileImage() { return profileImage; }

        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    }

    /** id 또는 nickname 으로 1명 조회 */
    public UserLite findByIdOrNickname(Connection conn, String q) throws Exception {
        String sql = "SELECT id, name, nickname, profile_image "
                   + "FROM users "
                   + "WHERE id = ? OR nickname = ? "
                   + "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q);
            ps.setString(2, q);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserLite u = new UserLite();
                    u.setId(rs.getString("id"));
                    u.setName(rs.getString("name"));
                    u.setNickname(rs.getString("nickname"));
                    u.setProfileImage(rs.getString("profile_image"));
                    return u;
                }
            }
        }
        return null;
    }
}
