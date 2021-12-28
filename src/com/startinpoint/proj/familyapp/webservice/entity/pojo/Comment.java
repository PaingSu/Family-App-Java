package com.startinpoint.proj.familyapp.webservice.entity.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;

/**
 * 
 * @author nankhinmhwe
 *
 */
public class Comment {
	private Long commentId;
	private String comment;
	private Long userId;
	private String username;
	private String imageUrl;
	private Date commentedDate;
	private Date updatedDate;
	
	@JsonProperty(value="commentId")
	public Long getCommentId() {
		return commentId;
	}
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	
	@JsonProperty(value="comment")
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@JsonProperty(value="userId")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@JsonProperty(value="username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonProperty(value="imageUrl")
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@JsonProperty(value="commentDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getCommentedDate() {
		return commentedDate;
	}
	public void setCommentedDate(Date commentedDate) {
		this.commentedDate = commentedDate;
	}
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value="updatedDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	
}
