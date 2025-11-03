package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        } catch (SQLException e) {
			e.printStackTrace();
		}
        return scheduleId;
    }
	
	public ScheduleDTO selectSchedule(Connection conn, long scheduleId, String[] travelBuddies) {
		String sql = "SELECT * FROM schedules WHERE id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, scheduleId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	ScheduleDTO schedule = new ScheduleDTO();
            	schedule.setId(scheduleId);
            	schedule.setUserId(rs.getString("user_id"));
            	schedule.setTitle(rs.getString("title"));
            	schedule.setLocation(rs.getString("location"));
            	schedule.setDescription(rs.getString("description"));
            	schedule.setVisibility(rs.getString("visibility"));
            	schedule.setStartDate(rs.getString("start_date"));
            	schedule.setEndDate(rs.getString("end_date"));
            	schedule.setMainImage(rs.getString("main_image"));
            	schedule.setTravelBuddies(travelBuddies);
            	
            	return schedule;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
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
        } catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	public String[] selectMembers(Connection conn, long scheduleId) throws SQLException {
		String sql = "SELECT user_id FROM members WHERE schedule_id = ?";
		List<String> travelBuddiesList = new ArrayList<>();
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, scheduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					travelBuddiesList.add(rs.getString("user_id"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return travelBuddiesList.toArray(new String[travelBuddiesList.size()]);
	}
}
