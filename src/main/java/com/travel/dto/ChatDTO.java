package com.travel.dto;

import java.sql.Timestamp;

public class ChatDTO {
    private int comment_id;
    private int schedule_id;
    private String user_id;
    private String comment;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String profile_image;
    private String scheduleTitle; // 추가함
    
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public int getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(int schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getcomment() {
		return comment;
	}
	public void setcomment(String comment) {
		this.comment = comment;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getProfile_image() {
		return profile_image;
	}
	public void setProfile_image(String profile_image) {
		this.profile_image = profile_image;
	}
	// 추가함
	public String getScheduleTitle() {
        return scheduleTitle;
    }
    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }
	
   
    
   
}



