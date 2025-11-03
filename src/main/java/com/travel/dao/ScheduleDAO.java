package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.travel.dto.ScheduleDTO;

public class ScheduleDAO {
	
	public long insertSchedule(Connection conn, ScheduleDTO schedule) throws SQLException {
        String sql = "INSERT INTO schedules(user_id, title, location, description, visibility, start_date, end_date, main_image, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        long scheduleId = 0;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, schedule.getUserId());
            pstmt.setString(2, schedule.getTitle());
            pstmt.setString(3, schedule.getLocation());
            pstmt.setString(4, schedule.getDescription());
            pstmt.setString(5, schedule.getVisibility());
            pstmt.setString(6, schedule.getStartDate());
            pstmt.setString(7, schedule.getEndDate());
            pstmt.setString(8, schedule.getMainImage());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        scheduleId = rs.getLong(1);
                    } else {
                        throw new SQLException("Creating schedule failed, no ID obtained.");
                    }
                }
            }
        }
        return scheduleId;
    }
	
	public void insertMembers(Connection conn, long scheduleId, String creatorId, String[] travelBuddies) throws SQLException {
        String sql = "INSERT INTO members(schedule_id, user_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, scheduleId);
            pstmt.setString(2, creatorId);
            pstmt.addBatch();

            if (travelBuddies != null) {
                for (String buddyId : travelBuddies) {
                    pstmt.setLong(1, scheduleId);
                    pstmt.setString(2, buddyId);
                    pstmt.addBatch();
                }
            }
            
            pstmt.executeBatch();
        }
    }
}
