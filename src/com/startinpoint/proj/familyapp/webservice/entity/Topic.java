package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
/**
 * @author ThoonSandy
 * @version 1.0
 * @since 24-04-2018
 */
@Entity
@Table(name="topic")
@Where(clause = "topic_status = 'A'")
public class Topic implements Serializable {

private static final long serialVersionUID = 1L;
	
	private Long id;
    private String topicTitle;
    private String description;
    private Date createdDate;
    private Date modifiedDate;
    private Long topicCreator;
    private Status topicStatus=Status.A;
    private List<TopicMember> topicMemberLists;
    private List<UserProfile> memberLists;   
    private FamilyProfile familyProfile;
    private Integer commentCount;
    private Integer memberCount;
    
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonProperty(value="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonProperty(value="topicTitle")
	@NotNull
	@Column(name="topic_title",nullable=false)
	public String getTopicTitle() {
		return topicTitle;
	}
	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}
	
	@JsonProperty(value="description")
	@Column(name="description",nullable=false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	@JsonProperty(value="modifiedDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date",nullable=false)
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	@JsonProperty(value="topicCreator")
	@Column(name="topic_creator")
	public Long getTopicCreator() {
		return topicCreator;
	}
	public void setTopicCreator(Long topicCreator) {
		this.topicCreator = topicCreator;
	}

	
	@JsonProperty(value="topicStatus")
	@Column(name="topic_status")
	@Enumerated(EnumType.STRING)
	public Status getTopicStatus() {
		return topicStatus;
	}
	public void setTopicStatus(Status topicStatus) {
		this.topicStatus = topicStatus;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy="topic",cascade=CascadeType.ALL ,fetch=FetchType.EAGER)	
	@Fetch(value = FetchMode.SELECT)
	public List<TopicMember> getTopicMemberLists() {
		return topicMemberLists;
	}
	public void setTopicMemberLists(List<TopicMember> topicMemberLists) {
		this.topicMemberLists = topicMemberLists;
	}
	
	@JsonProperty(value="memberLists")
	@Transient
	public List<UserProfile> getMemberLists() {
		return memberLists;
	}
	public void setMemberLists(List<UserProfile> memberLists) {
		this.memberLists = memberLists;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="family_profile_id")
	@JsonInclude(Include.NON_NULL)
	public FamilyProfile getFamilyProfile() {
		return familyProfile;
	}
	public void setFamilyProfile(FamilyProfile familyProfile) {
		this.familyProfile = familyProfile;
	}
	
	@Transient
	@JsonInclude(Include.NON_NULL)
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	
	@Transient
	@JsonInclude(Include.NON_NULL)
	public Integer getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}
}
