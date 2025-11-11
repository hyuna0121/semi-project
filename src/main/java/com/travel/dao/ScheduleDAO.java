package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.travel.dto.ScheduleDTO;
import util.DBUtil; // DBUtil 임포트

public class ScheduleDAO {
	
	/**
	 * [새 메서드 1]
	 * 특정 사용자 ID가 '참여 중인' 모든 일정을 조회합니다. (본인이 작성자X)
	 * (DAO 내부에서 자체적으로 Connection을 열고 닫습니다.)
	 * * @param userId 조회할 사용자 ID
	 * @return ScheduleDTO 리스트
	 */
	public List<ScheduleDTO> getJoinedSchedulesByUserId(String userId) {
		List<ScheduleDTO> scheduleList = new ArrayList<>();
		Connection conn = null;
		
		// SQL: members 테이블에 userId가 존재하고, schedules.user_id(작성자)와는 다른 일정을 조회
		String sql = "SELECT s.*, GROUP_CONCAT(m_buddies.user_id) AS buddies " +
					 "FROM schedules s " +
					 "JOIN members m_join ON s.id = m_join.schedule_id AND m_join.user_id = ? " + // 1. 참여자 테이블 조인
					 "LEFT JOIN members m_buddies ON s.id = m_buddies.schedule_id " + // 2. 동행자 목록을 위한 조인
					 "WHERE s.user_id != ? " + // 3. 자신이 작성한 일정은 제외
					 "GROUP BY s.id " +
					 "ORDER BY s.start_date DESC";

		try {
			conn = DBUtil.getConnection();
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userId); // m_join.user_id
				pstmt.setString(2, userId); // s.user_id
				
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						ScheduleDTO schedule = new ScheduleDTO();
						schedule.setId(rs.getLong("id"));
						schedule.setUserId(rs.getString("user_id")); // 작성자 ID
						schedule.setTitle(rs.getString("title"));
						schedule.setLocation(rs.getString("location"));
						schedule.setDescription(rs.getString("description"));
						schedule.setVisibility(rs.getString("visibility"));
						schedule.setStartDate(rs.getString("start_date"));
						schedule.setEndDate(rs.getString("end_date"));
						schedule.setMainImage(rs.getString("main_image"));
						// schedule.setCreatedAt(rs.getString("created_at")); // DTO에 setCreatedAt이 있다면
						
						String buddiesString = rs.getString("buddies");
						
						if (buddiesString != null && !buddiesString.isEmpty()) {
							schedule.setTravelBuddies(buddiesString.split(","));
						} else {
							schedule.setTravelBuddies(new String[0]);
						}
						
						scheduleList.add(schedule);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>(); // 오류 발생 시 빈 리스트 반환
		} finally {
			DBUtil.close(conn); // Connection 닫기
		}
		return scheduleList;
	}

	/**
	 * [새 메서드 2]
	 * 특정 사용자 ID가 '생성한' 모든 일정을 조회합니다. (마이페이지 용)
	 * (DAO 내부에서 자체적으로 Connection을 열고 닫습니다.)
	 * * @param userId 조회할 사용자 ID
	 * @return ScheduleDTO 리스트
	 */
	public List<ScheduleDTO> getMySchedules(String userId) {
		Connection conn = null;
		List<ScheduleDTO> scheduleList = new ArrayList<>();

		try {
			conn = DBUtil.getConnection();
			
			// Connection을 매개변수로 받는 기존 메서드 호출
			scheduleList = getSchedulesByUserId(conn, userId);	
			
		} catch (Exception e) {
			e.printStackTrace();
			// 오류 발생 시 빈 리스트 반환
		} finally {
			DBUtil.close(conn); // Connection 닫기
		}
		return scheduleList;
	}
	
	// ----------------------------------------------------------------------
	// 💡 아래는 기존 메서드들 (Connection conn을 매개변수로 받음)
	// ----------------------------------------------------------------------

	/**
	 * 새 일정을 DB에 추가 (schedules 테이블)
	 */
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

	/**
	 * 특정 ID의 일정 정보를 조회 (동행인 목록 포함)
	 */
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
					if (buddiesString != null && !buddiesString.isEmpty()) {
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
	
	/**
	 * 키워드(location)로 일정 검색
	 */
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
					if (buddiesString != null && !buddiesString.isEmpty()) {
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
	
	/**
	 * 일정 생성 시, 동행인 목록을 members 테이블에 추가
	 */
	public void insertMembers(Connection conn, long scheduleId, String ownerId, String[] userIds) throws SQLException {
	    String sql = "INSERT IGNORE INTO members (schedule_id, user_id) VALUES (?, ?)";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        if (ownerId != null && !ownerId.isBlank()) {
	            ps.setLong(1, scheduleId);
	            ps.setString(2, ownerId.trim());
	            ps.executeUpdate();
	        }
	        if (userIds != null) {
	            for (String uid : userIds) {
	                if (uid == null || uid.isBlank()) continue;
	                ps.setLong(1, scheduleId);
	                ps.setString(2, uid.trim());
	                ps.executeUpdate();
	            }
	        }
	    }
	}


	/**
	 * 특정 사용자 ID가 '생성한' 모든 일정을 조회합니다. (Connection을 매개변수로 받음)
	 */
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
					// schedule.setCreatedAt(rs.getString("created_at")); // DTO에 setCreatedAt이 있다면
					
					String buddiesString = rs.getString("buddies");
					if (buddiesString != null && !buddiesString.isEmpty()) {
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
		String sql = "UPDATE schedules SET title = ?, location = ?, description = ?, visibility = ?, start_date = ?, end_date = ?, main_image = ? " +
					 "WHERE id = ?";
			
		this.deleteMembersByScheduleId(conn, schedule.getId());
		this.insertMembers(conn, schedule.getId(), schedule.getUserId(), schedule.getTravelBuddies());
			
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, schedule.getTitle());
			pstmt.setString(2, schedule.getLocation());
			pstmt.setString(3, schedule.getDescription());
			pstmt.setString(4, schedule.getVisibility());
			pstmt.setString(5, schedule.getStartDate());
			pstmt.setString(6, schedule.getEndDate());
			pstmt.setString(7, schedule.getMainImage());
			pstmt.setLong(8, schedule.getId()); 
				
			pstmt.executeUpdate();
		}
			
	}
	
	public int deleteMembersByScheduleId(Connection conn, long scheduleId) throws SQLException {
		String sql = "DELETE FROM members WHERE schedule_id = ?";
		int result = 0;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, scheduleId);
			result = pstmt.executeUpdate();				
		}
		
		return result;
	}
	
	public int deleteSchedule(Connection conn, long scheduleId) throws SQLException {
		int rowsAffected = 0;
		
		DetailDAO detailDAO = new DetailDAO();
		ChatDAO chatDAO = new ChatDAO();
		
		String deleteScheduleSql = "DELETE FROM schedules WHERE id = ?";
		

		detailDAO.deleteDetailByScheduleId(conn, scheduleId);
		chatDAO.deleteCommentByScheduleId(conn, scheduleId);
		this.deleteMembersByScheduleId(conn, scheduleId);			
			
		try (PreparedStatement pstmt = conn.prepareStatement(deleteScheduleSql)) {
			pstmt.setLong(1, scheduleId); 
			rowsAffected = pstmt.executeUpdate();
				
			if (rowsAffected == 0) {
				throw new SQLException("Schedule 삭제 실패: ID를 찾을 수 없습니다 - " + scheduleId);
			}
		}
		
		return rowsAffected;
	}
}