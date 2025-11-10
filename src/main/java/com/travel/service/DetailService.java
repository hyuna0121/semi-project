package com.travel.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.travel.dao.DetailDAO;
import com.travel.dao.ScheduleDAO;
import com.travel.dto.DetailDTO;
import com.travel.dto.ScheduleDTO;

import util.DBUtil;

public class DetailService {
	private DetailDAO detailDAO;
	private ScheduleDAO scheduleDAO;
	
	public DetailService() {
		this.detailDAO = new DetailDAO();
		this.scheduleDAO = new ScheduleDAO();
	}
	
	public Map<String, List<DetailDTO>> getGroupedDetails(long scheduleId) {
		ScheduleDTO schedule;
		List<DetailDTO> detailList;
		
		try (Connection conn = DBUtil.getConnection()) {
			schedule = scheduleDAO.selectSchedule(conn, scheduleId);
			detailList = detailDAO.selectDetails(conn, scheduleId);
		} catch (Exception e) {
			e.printStackTrace();
            return new LinkedHashMap<>();
		}
		
		Map<String, List<DetailDTO>> groupedByDate = new LinkedHashMap<>();
		for (DetailDTO detail : detailList) {
			String dateKey = detail.getDate();
			
			groupedByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(detail);
		}
		
		LocalDate startDate = LocalDate.parse(schedule.getStartDate());
        LocalDate endDate = LocalDate.parse(schedule.getEndDate());
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		Map<String, List<DetailDTO>> groupedByCount = new LinkedHashMap<>();
		int dayCount = 1;
		
		for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = startDate.plusDays(i); 
            String currentDayIndex = String.valueOf(i + 1); 
            String currentDateString = currentDate.format(dtf); 

            List<DetailDTO> detailsForThisDate = groupedByDate.get(currentDateString);
            
            if (detailsForThisDate != null) {
            	groupedByCount.put(currentDayIndex, detailsForThisDate);
            } else {
            	groupedByCount.put(currentDayIndex, new ArrayList<>());
            }
        }
		
		return groupedByCount;
	}
}
