package com.travel.dao;

import com.travel.dto.ScheduleDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleListDAO {

    public List<ScheduleDTO> findAll(Connection conn, int offset, int limit) throws Exception {
        String sql =
            "SELECT s.id, s.user_id, s.title, s.location, s.description, s.visibility, " +
            "       s.start_date, s.end_date, s.main_image " +
            "FROM schedules s " +
            "ORDER BY s.created_at DESC " +
            "LIMIT ? OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            List<ScheduleDTO> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleDTO d = new ScheduleDTO();
                    d.setId(rs.getLong("id"));
                    d.setUserId(rs.getString("user_id"));
                    d.setTitle(rs.getString("title"));
                    d.setLocation(rs.getString("location"));
                    d.setDescription(rs.getString("description"));
                    d.setVisibility(rs.getString("visibility"));
                    d.setStartDate(rs.getString("start_date"));
                    d.setEndDate(rs.getString("end_date"));
                    d.setMainImage(rs.getString("main_image"));
                    list.add(d);
                }
            }
            return list;
        }
    }

    public int countAll(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM schedules");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
