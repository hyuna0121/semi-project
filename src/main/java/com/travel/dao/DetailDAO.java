package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.dto.DetailDTO;

public class DetailDAO {
	public void insertDetail(Connection conn, List<DetailDTO> detailList) throws SQLException {
		String sql = "INSERT INTO details(schedule_id, date, place, start_time, memo, category, position) VALUES (?, ?, ?, ?, ?, ?, POINT(?, ?))";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			conn.setAutoCommit(false);
			
			for (DetailDTO detail : detailList) {
				pstmt.setLong(1, detail.getScheduleId());
				pstmt.setString(2, detail.getDate());
				pstmt.setString(3, detail.getPlace());
				pstmt.setString(4, detail.getStartTime());
				pstmt.setString(5, detail.getMemo());
				pstmt.setString(6, detail.getCategory());
				pstmt.setDouble(7, detail.getLongitude());
				pstmt.setDouble(8, detail.getLatitude());
				
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			} 
			
			e.printStackTrace();
			
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public List<DetailDTO> selectDetails(Connection conn, long scheduleId) throws SQLException {
		List<DetailDTO> detailList = new ArrayList<>();
		String sql = "SELECT id, schedule_id, date, place, start_time, memo, category, " +
                "ST_X(position) AS longitude, " +  // 경도 (X)
                "ST_Y(position) AS latitude " +   // 위도 (Y)
                "FROM details WHERE schedule_id = ? ORDER BY date, start_time";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, scheduleId);
			
			try (ResultSet rs = pstmt.executeQuery()){
				while (rs.next()) {
					DetailDTO detail = new DetailDTO();
					
					detail.setId(rs.getLong("id"));
					detail.setScheduleId(rs.getLong("schedule_id"));
					detail.setDate(rs.getString("date"));
					detail.setPlace(rs.getString("place"));
					detail.setStartTime(rs.getString("start_time"));
					detail.setMemo(rs.getString("memo"));
					detail.setCategory(rs.getString("category"));
					detail.setLongitude(rs.getDouble("longitude")); 
					detail.setLatitude(rs.getDouble("latitude")); 
					
					detailList.add(detail);
				}
			} catch (SQLException e) {
				System.out.println("ResultSet 처리 중 오류");
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			System.out.println("PreparedStatement 생성 또는 실행 중 오류");
			e.printStackTrace();
		}
		
		return detailList;
	}
	
	public Long selectScheduleIdByDetailId(Connection conn, Long detailId) {
		String sql = "SELECT * FROM details WHERE id = ?";
		long scheduleId = 0;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, detailId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					scheduleId = rs.getLong("schedule_id");
				}
			} catch (Exception e) {
				System.out.println("ResultSet 처리 중 오류");
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			System.out.println("PreparedStatement 생성 또는 실행 중 오류");
			e.printStackTrace();
		}
		
		return scheduleId;
	}

	public long deleteDetail(Connection conn, Long detailId) {
		long scheduleId = this.selectScheduleIdByDetailId(conn, detailId);
		
		String sql = "DELETE FROM details WHERE id = ?";
		long success = 0;
		int result = 0;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, detailId);
			
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("PreparedStatement 생성 또는 실행 중 오류");
			e.printStackTrace();
		}
		
		if (result == 1) {
			success = scheduleId;
		}

		return success;
	}
	
	public int deleteDetailByScheduleId(Connection conn, Long scheduleId) throws SQLException {		
		String sql = "DELETE FROM details WHERE schedule_id = ?";
		int result = 0;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, scheduleId);
			
			result = pstmt.executeUpdate();
		} 
		
		return result;
	}
	
	public int deleteDetailsOutRange(Connection conn, long scheduleId, String startDate, String endDate) throws SQLException {
	    String sql = "DELETE FROM details " +
	                  "WHERE schedule_id = ? " +
	                  "AND (date < DATE(?) OR date > DATE(?))";
	    int result = 0;
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setLong(1, scheduleId);
	        pstmt.setString(2, startDate); // 또는 setDate
	        pstmt.setString(3, endDate);   // 또는 setDate
	        
	        result = pstmt.executeUpdate();
	    }
	    
	    return result;
	}
	
	public void insertDetailCopy(Connection conn, List<DetailDTO> detailList) throws SQLException {
		String sql = "INSERT INTO details(schedule_id, date, place, start_time, memo, category, position) VALUES (?, ?, ?, ?, ?, ?, POINT(?, ?))";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			for (DetailDTO detail : detailList) {
				pstmt.setLong(1, detail.getScheduleId());
				pstmt.setString(2, detail.getDate());
				pstmt.setString(3, detail.getPlace());
				pstmt.setString(4, detail.getStartTime());
				pstmt.setString(5, detail.getMemo());
				pstmt.setString(6, detail.getCategory());
				pstmt.setDouble(7, detail.getLongitude());
				pstmt.setDouble(8, detail.getLatitude());
				
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			
		} catch (SQLException e) {
			
			
			e.printStackTrace();
			
			throw e;
		} finally {
		}
	}

}
