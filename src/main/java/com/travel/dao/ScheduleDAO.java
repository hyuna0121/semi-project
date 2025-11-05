package com.travel.dao;

import com.travel.dto.ScheduleDTO;
import util.DBUtil; // DBUtil 패키지 경로 (DB 연결 관리를 위해 추가)
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    // 🚨🚨🚨 travel_schedule.jsp에서 사용할 새로운 조회 메소드 🚨🚨🚨
	
	/**
     * 특정 사용자 ID가 참여 중인 모든 일정을 조회합니다.
     * (사용자가 작성자는 아니지만 members 테이블에 user_id가 존재하는 일정)
     * @param userId 조회할 사용자 ID
     * @return ScheduleDTO 리스트
     */
    public List<ScheduleDTO> getJoinedSchedulesByUserId(String userId) {
        List<ScheduleDTO> scheduleList = new ArrayList<>();
        Connection conn = null;
        
        // 💡 SQL: members 테이블에 userId가 존재하고, schedules.user_id(작성자)와는 다른 일정을 조회
        String sql = "SELECT s.*, GROUP_CONCAT(m_buddies.user_id) AS buddies " +
                     "FROM schedules s " +
                     "JOIN members m_join ON s.id = m_join.schedule_id AND m_join.user_id = ? " + // 참여자 테이블 조인
                     "LEFT JOIN members m_buddies ON s.id = m_buddies.schedule_id " + // 동행자 목록을 위한 조인
                     "WHERE s.user_id != ? " + // 자신이 작성한 일정은 제외
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
                        schedule.setCreatedAt(rs.getString("created_at"));
                        
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
            return new ArrayList<>();
        } finally {
            DBUtil.close(conn);
        }
        return scheduleList;
    }
		
    /**
     * 특정 사용자 ID가 생성한 모든 일정을 조회합니다. (DAO 내부에서 Connection 관리)
     * @param userId 조회할 사용자 ID
     * @return ScheduleDTO 리스트
     */
    public List<ScheduleDTO> getMySchedules(String userId) {
        Connection conn = null;
        List<ScheduleDTO> scheduleList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            
            // 기존 getSchedulesByUserId(Connection conn, String userId) 로직 호출
            scheduleList = getSchedulesByUserId(conn, userId); 
            
        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 빈 리스트 반환
        } finally {
            DBUtil.close(conn);
        }
        return scheduleList;
    }
    
    // ----------------------------------------------------------------------
    // 💡 아래는 코드를 그대로 유지합니다. (Connection conn을 매개변수로 받음)
    // ----------------------------------------------------------------------

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
					// 여기서 Id는 long 타입이므로, rs.getLong("id")를 사용하거나, 
                    // DTO/DB 통일이 필요합니다. 현재는 long으로 가정합니다.
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

	public void insertMembers(Connection conn, long scheduleId, String creatorId, String[] travelBuddies)
			throws SQLException {
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

    /**
     * 특정 사용자 ID가 생성한 모든 일정을 조회합니다. (DAO 내부에서 Connection을 받도록 유지)
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
                    // DTO에 createdAt 필드가 있으므로 추가
                    schedule.setCreatedAt(rs.getString("created_at")); 
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
}