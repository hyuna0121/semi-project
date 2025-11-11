package com.travel.dto;

import java.util.Arrays;

// 제공된 스키마와 DTO 코드를 기반으로 통합
public class ScheduleDTO {
	private long id;
	private String userId;
	private String title;
	private String location;
	private String description;
	private String visibility;
	private String startDate; // DB DATE 타입을 String으로 처리
	private String endDate;   // DB DATE 타입을 String으로 처리
	private String mainImage;
	private String[] travelBuddies; // 동행자 목록
	private String createdAt; // DB TIMESTAMP 타입을 String으로 처리
  
	public ScheduleDTO() {}
	
    // 🚨 스키마에 없지만 DAO에서 JOIN을 위해 사용하는 필드 추가
	public String getCreatedAt() { return createdAt; }
	public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
	
	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public String getVisibility() { return visibility; }
	public void setVisibility(String visibility) { this.visibility = visibility; }
	
	public String getStartDate() { return startDate; }
	public void setStartDate(String startDate) { this.startDate = startDate; }

	public String getEndDate() { return endDate; }
	public void setEndDate(String endDate) { this.endDate = endDate; }

	public String getMainImage() { return mainImage; }
	public void setMainImage (String mainImage) { this.mainImage = mainImage; }
	
	public String[] getTravelBuddies() { return travelBuddies; }
	public void setTravelBuddies(String[] travelBuddies) { this.travelBuddies = travelBuddies; }

	@Override
	public String toString() {
		return "ScheduleDTO [title=" + title + ", location=" + location + ", description=" + description
				+ ", visibility=" + visibility + ", startDate=" + startDate + ", endDate=" + endDate + ", mainImage="
				+ mainImage + ", travelBuddies=" + Arrays.toString(travelBuddies) + "]";
	}
	
	
}