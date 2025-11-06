package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}
