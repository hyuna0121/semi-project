package com.travel.dto;

public class ScheduleDTO {
	private long id;
	private String userId;
	private String title;
	private String location;
	private String description;
	private String visibility;
	private String startDate;
	private String endDate;
	private String mainImage;
	private String[] travelBuddies;
  
	public ScheduleDTO() {}
	
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
	
}
