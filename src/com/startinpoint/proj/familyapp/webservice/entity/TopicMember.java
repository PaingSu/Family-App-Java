package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.Date;
/**
 * @author ThoonSandy
 * @version 1.0
 * @since 24-04-2018
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

@Entity
@Table(name="topic_member")
@Where(clause = "member_status = 'A'")
public class TopicMember implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Status memberStatus=Status.A;
	private Date createdDate;
	private Topic topic;
	private Long topicId;
	private Long memberId;
	private UserProfile member;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty(value="memberStatus")
	@Column(name="member_status")
	@Enumerated(EnumType.STRING)
	public Status getMemberStatus() {
		return memberStatus;
	}
	public void setMemberStatus(Status memberStatus) {
		this.memberStatus = memberStatus;
	}
	
	@JsonProperty(value="createdDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date",nullable=false)
	public Date getCreatedDate() {
		return createdDate;
	}

	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@JsonIgnoreProperties("memberLists")
	@JsonProperty("topic")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="topic_id",referencedColumnName = "id")
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	@JsonProperty(value="topicId")
	@Column(name="topic_id",insertable = false, updatable = false )
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	
	
	@JsonProperty("member")
	@ManyToOne
	@Where(clause = "member_status = 'A'")
	@JoinColumn(name="member_id",referencedColumnName = "id",insertable = false, updatable = false)
	public UserProfile getMember() {
		return member;
	}
	
	public void setMember(UserProfile member) {
		this.member = member;
	}
	
	@JsonProperty(value="memberId")
	@Column(name="member_id" )
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	
	
}
