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
	
	public ScheduleDTO selectSchedule(Connection conn, long scheduleId) {
		String sql = "SELECT s.*, GROUP_CONCAT(m.user_id) AS buddies " +
                "FROM schedules s LEFT JOIN members m ON s.id = m.schedule_id " +
                "WHERE s.id = ? GROUP BY s.id";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, scheduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
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
            		
            		String buddiesString = rs.getString("buddies");
            		
            		if (buddiesString != null && ! buddiesString.isEmpty()) {
						schedule.setTravelBuddies(buddiesString.split(","));
					} else {
						schedule.setTravelBuddies(new String[0]);
					}
            		
            		return schedule;
            	}
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<ScheduleDTO> searchSchedule(Connection conn, String keyword) {
		String sql = "SELECT s.*, GROUP_CONCAT(m.user_id) AS buddies " +
                "FROM schedules s LEFT JOIN members m ON s.id = m.schedule_id " +
                "WHERE s.location LIKE ? GROUP BY s.id";
		
		List<ScheduleDTO> searchResult = new ArrayList<>();
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, "%" + keyword + "%");
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ScheduleDTO schedule = new ScheduleDTO();
					schedule.setId(rs.getLong("id"));
					schedule.setUserId(rs.getString("user_id"));
					schedule.setTitle(rs.getString("title"));
					schedule.setLocation(rs.getString("location"));
					schedule.setDescription(rs.getString("description"));
					schedule.setVisibility(rs.getString("visibility"));
					schedule.setStartDate(rs.getString("start_date"));
					schedule.setEndDate(rs.getString("end_date"));
					schedule.setMainImage(rs.getString("main_image"));
					
					String buddiesString = rs.getString("buddies");
					
					if (buddiesString != null && ! buddiesString.isEmpty()) {
						schedule.setTravelBuddies(buddiesString.split(","));
					} else {
						schedule.setTravelBuddies(new String[0]);
					}
					
					searchResult.add(schedule);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			return new ArrayList<>();
		}
		
		return searchResult;
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
	


		public List<ScheduleDTO> getSchedulesByUserId(Connection conn, String userId) {
			String sql = "SELECT s.*, GROUP_CONCAT(m.user_id) AS buddies " +
	                "FROM schedules s LEFT JOIN members m ON s.id = m.schedule_id " + 
	                "WHERE s.user_id = ? " +
	                "GROUP BY s.id " + 
	                "ORDER BY s.start_date DESC";
			
			List<ScheduleDTO> scheduleList = new ArrayList<>();
			
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userId);
				
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						ScheduleDTO schedule = new ScheduleDTO();
						schedule.setId(rs.getLong("id"));
						schedule.setUserId(rs.getString("user_id"));
						schedule.setTitle(rs.getString("title"));
						schedule.setLocation(rs.getString("location"));
						schedule.setDescription(rs.getString("description"));
						schedule.setVisibility(rs.getString("visibility"));
						schedule.setStartDate(rs.getString("start_date"));
						schedule.setEndDate(rs.getString("end_date"));
						schedule.setMainImage(rs.getString("main_image"));
						
						String buddiesString = rs.getString("buddies");
						
						if (buddiesString != null && ! buddiesString.isEmpty()) {
							schedule.setTravelBuddies(buddiesString.split(","));
						} else {
							schedule.setTravelBuddies(new String[0]);
						}
						
						scheduleList.add(schedule);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
			
			return scheduleList;
		}
		
		
		public void updateSchedule(Connection conn, ScheduleDTO schedule) throws SQLException {
	        String sql = "UPDATE schedules SET title = ?, location = ?, description = ?, main_image = ?, visibility = ? " +
	                     "WHERE id = ?"; 
	        
	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            pstmt.setString(1, schedule.getTitle());
	            pstmt.setString(2, schedule.getLocation());
	            pstmt.setString(3, schedule.getDescription());
	            pstmt.setString(4, schedule.getMainImage());
	            pstmt.setString(5, schedule.getVisibility());
	            pstmt.setLong(6, schedule.getId());

	            pstmt.executeUpdate();
	        }
	    }
		
		public void deleteSchedule(Connection conn, long scheduleId) throws SQLException {
	        
	        
	        String deleteMembersSql = "DELETE FROM members WHERE schedule_id = ?";
	        String deleteScheduleSql = "DELETE FROM schedules WHERE id = ?";
	        
	        try {

	            conn.setAutoCommit(false); 

	            
	            try (PreparedStatement pstmt = conn.prepareStatement(deleteMembersSql)) {
	                pstmt.setLong(1, scheduleId); 
	                pstmt.executeUpdate();
	            }

	           
	            try (PreparedStatement pstmt = conn.prepareStatement(deleteScheduleSql)) {
	                pstmt.setLong(1, scheduleId); 
	                int rowsAffected = pstmt.executeUpdate();
	                if (rowsAffected == 0) {
	                    throw new SQLException("Schedule 삭제 실패: ID를 찾을 수 없습니다 - " + scheduleId);
	                }
	            }
	            

	            conn.commit();
	            
	        } catch (SQLException e) {

	            if (conn != null) {
	                conn.rollback();
	            }
	            e.printStackTrace();
	            throw e; 
	        } finally {
	           
	            if (conn != null) {
	                conn.setAutoCommit(true);
	            }
	        }
	    }
	
	
}
