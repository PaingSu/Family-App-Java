package com.startinpoint.proj.familyapp.webservice.entity;
/**
 * @author ThoonSandy
 * @version 1.0
 * @since 24-04-2018
 */

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

@Entity
@Table(name="topic_Comment")
@Where(clause = "comment_status = 'A'")
public class TopicComment implements Serializable  {
	
	public static final long serialVersionUID=1L;
	
	
	private Long id;
	private String comment;
	private Date commentDate;
	private Date updatedDate;
	private Long topicId;
	private Long topicMemberId;
	private Topic topic;
	private TopicMember topicMember;
	private Status commentStatus=Status.A;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty(value="comment")
	@NotNull
	@Column(name="comment",nullable=false,columnDefinition="TEXT")
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@JsonProperty(value="commentDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="comment_date")
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}
	
	@JsonProperty(value="updatedDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_date")
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	

	@JsonProperty("topic")
	@JsonBackReference(value="topic")
	@ManyToOne
	@JoinColumn(name="topic_id",referencedColumnName = "id",insertable = false, updatable = false)
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	
	@JsonProperty("topicMember")
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="topic_member_id",referencedColumnName = "id",insertable = false, updatable = false)
	public TopicMember getTopicMember() {
		return topicMember;
	}
	public void setTopicMember(TopicMember topicMember) {
		this.topicMember = topicMember;
	}
	@JsonProperty(value="topicId")
	@Column(name="topic_id")
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	@JsonProperty(value="topicMemberId")
	@Column(name="topic_member_id")
	public Long getTopicMemberId() {
		return topicMemberId;
	}
	public void setTopicMemberId(Long topicMemberId) {
		this.topicMemberId = topicMemberId;
	}

	@JsonProperty(value="commentStatus")
	@Column(name="comment_status")
	@Enumerated(EnumType.STRING)
	public Status getCommentStatus() {
		return commentStatus;
	}
	public void setCommentStatus(Status commentStatus) {
		this.commentStatus = commentStatus;
	}
	
	
}
