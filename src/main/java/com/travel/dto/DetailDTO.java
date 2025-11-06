package com.travel.dto;

public class DetailDTO {
	private long id;
	private long scheduleId;
	private String date;
	private String place;
	private String startTime;
	private String memo;
	private String category;
    private double latitude;  // 위도 (y)
    private double longitude; // 경도 (x)
    
    public DetailDTO() {}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public long getScheduleId() { return scheduleId; }
	public void setScheduleId(long scheduleId) { this.scheduleId = scheduleId; }

	public String getDate() { return date; }
	public void setDate(String date) { this.date = date; }

	public String getPlace() { return place; }
	public void setPlace(String place) { this.place = place; }

	public String getStartTime() { return startTime; }
	public void setStartTime(String startTime) { this.startTime = startTime; }

	public String getMemo() { return memo; }
	public void setMemo(String memo) { this.memo = memo; }

	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }

	public double getLatitude() { return latitude; }
	public void setLatitude(double latitude) { this.latitude = latitude; }
	
	public double getLongitude() { return longitude; }
	public void setLongitude(double longitude) { this.longitude = longitude; }
    
}
