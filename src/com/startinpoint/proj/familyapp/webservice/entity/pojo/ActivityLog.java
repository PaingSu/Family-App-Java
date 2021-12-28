package com.startinpoint.proj.familyapp.webservice.entity.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Activity;

/**
 * 
 * @since 14/06/2018
 * @author nankhinmhwe
 *
 */
public class ActivityLog {

	private Long checkListId;
	private Date createdDate;
	private Date updatedDate;
	private String username;
	private String imageUrl;
	private String parentTaskName;
	private String taskName;
	private String action;
	private Activity type;
	private String comment;
	

	@JsonProperty(value = "checkListId")
	public Long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(Long checkListId) {
		this.checkListId = checkListId;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonProperty(value = "createdDate")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@JsonInclude(Include.NON_NULL)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonProperty(value = "updatedDate")
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@JsonProperty(value = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "imageUrl")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "parentTaskName")
	public String getParentTaskName() {
		return parentTaskName;
	}

	public void setParentTaskName(String parentTaskName) {
		this.parentTaskName = parentTaskName;
	}

	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "taskName")
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@JsonProperty(value = "type")
	public Activity getType() {
		return type;
	}

	public void setType(Activity type) {
		this.type = type;
	}
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
