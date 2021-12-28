package com.startinpoint.proj.familyapp.webservice.entity.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;

public class CheckListOverview {
	private Long checkListId;
	private String checkListName;
	private String description;
	private Date targetDate;
	private int totalTaskCount;
	private int completeTaskCount;
	private int commentCount;

	@JsonProperty(value="checkListId")
	public Long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(Long checkListId) {
		this.checkListId = checkListId;
	}

	@JsonProperty(value="checkListName")
	public String getCheckListName() {
		return checkListName;
	}

	public void setCheckListName(String checkListName) {
		this.checkListName = checkListName;
	}

	@JsonProperty(value="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonProperty(value="targetDate")
	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	@JsonProperty(value="totalTaskCount")
	public int getTotalTaskCount() {
		return totalTaskCount;
	}

	public void setTotalTaskCount(int totalTaskCount) {
		this.totalTaskCount = totalTaskCount;
	}

	@JsonProperty(value="completeTaskCount")
	public int getCompleteTaskCount() {
		return completeTaskCount;
	}

	public void setCompleteTaskCount(int completeTaskCount) {
		this.completeTaskCount = completeTaskCount;
	}

	@JsonProperty(value="commentCount")
	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

}
