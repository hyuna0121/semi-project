package com.travel.dto;

public class Schedule {
	private int id;
	private String userId;
	private String title;
	private String location;
	private String description;
	private char visibility;
	private String date;
	private String mainImage;

  
	public Schedule() {}
	public Schedule(String userId, String title, String location, String description, char visibility, String date,
			String mainImage) {
		this.userId = userId;
		this.title = title;
		this.location = location;
		this.description = description;
		this.visibility = visibility;
		this.date = date;
		this.mainImage = mainImage;
	}
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public char getVisibility() { return visibility; }
	public void setVisibility(char visibility) { this.visibility = visibility; }
	
	public String getDate() { return date; }
	public void setDate(String date) { this.date = date; }
	
	public String getMainImage() { return mainImage; }
	public void setMainImage (String mainImage) { this.mainImage = mainImage; }
	
}
