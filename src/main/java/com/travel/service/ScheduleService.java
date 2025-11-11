package com.travel.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import com.travel.dao.DetailDAO;
import com.travel.dao.ScheduleDAO;
import com.travel.dto.ScheduleDTO;

import jakarta.servlet.http.Part;
import util.DBUtil;

public class ScheduleService {
	private ScheduleDAO scheduleDAO = new ScheduleDAO();
	private DetailDAO detailDAO = new DetailDAO();
	
	public long addSchedule(ScheduleDTO schedule, Part filePart, String uploadPath) throws Exception {
        
        Connection conn = null;
        long scheduleId = 0;
        String fileName = null;

        try {
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = filePart.getSubmittedFileName();
                String ext = "";
                int dotIndex = originalFileName.lastIndexOf(".");
                
                if (dotIndex > 0) {
                    ext = originalFileName.substring(dotIndex);
                }
                
                String baseName = originalFileName.substring(0, dotIndex);
                String uuid = UUID.randomUUID().toString().substring(0, 8);
                fileName = baseName + "_" + uuid + ext;
                
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                filePart.write(uploadPath + File.separator + fileName);
            }

            schedule.setMainImage(fileName);

            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            scheduleId = scheduleDAO.insertSchedule(conn, schedule);
            
            if (scheduleId > 0) {
                scheduleDAO.insertMembers(conn, scheduleId, schedule.getUserId(), schedule.getTravelBuddies());
            } else {
                throw new SQLException("No schedule ID generated, member insert aborted.");
            }

            conn.commit();

        } catch (SQLException | IOException e) {
            // 롤백
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            
            // 롤백 시 업로드된 파일 삭제
            if (fileName != null) {
                File uploadedFile = new File(uploadPath + File.separator + fileName);
                if (uploadedFile.exists()) {
                    uploadedFile.delete();
                }
            }
            
            e.printStackTrace();
            throw new Exception("스케줄 추가 중 오류 발생: " + e.getMessage()); 
            
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return scheduleId;
    }

	public void editSchedule(ScheduleDTO schedule, Part filePart, String uploadPath) throws Exception {
        String fileName = null;
        Connection conn = null;

        try {
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = filePart.getSubmittedFileName();
                String ext = "";
                int dotIndex = originalFileName.lastIndexOf(".");
                
                if (dotIndex > 0) {
                    ext = originalFileName.substring(dotIndex);
                }
                
                String baseName = originalFileName.substring(0, dotIndex);
                String uuid = UUID.randomUUID().toString().substring(0, 8);
                fileName = baseName + "_" + uuid + ext;
                
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                filePart.write(uploadPath + File.separator + fileName);
                schedule.setMainImage(fileName);
            }

            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            detailDAO.deleteDetailsOutRange(conn, schedule.getId(), schedule.getStartDate(), schedule.getEndDate());
            
            if (schedule.getTravelBuddies().length == 0) {
            	scheduleDAO.deleteSchedule(conn, schedule.getId());
            } else {
            	scheduleDAO.updateSchedule(conn, schedule);
            }

            conn.commit();

        } catch (SQLException | IOException e) {
            // 롤백
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            
            // 롤백 시 업로드된 파일 삭제
            if (fileName != null) {
                File uploadedFile = new File(uploadPath + File.separator + fileName);
                if (uploadedFile.exists()) {
                    uploadedFile.delete();
                }
            }
            
            e.printStackTrace();
            throw new Exception("스케줄 추가 중 오류 발생: " + e.getMessage()); 
            
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
}
