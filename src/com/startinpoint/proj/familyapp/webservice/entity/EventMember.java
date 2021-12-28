package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.Date;

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
import com.startinpoint.proj.familyapp.webservice.entity.enums.EventAcceptanceStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

/**
 * @author ThoonSandy
 * @since 14-05-2018
 *
 */
@Entity
@Table(name="event_member")
@Where(clause = "member_status = 'A'")
public class EventMember implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private EventAcceptanceStatus eventAcceptanceStatus;
	private Event event;
	private Long userProfileId;
	private UserProfile userProfile;
	private Date createdDate;
	private Date updatedDate;
	private Boolean isView =false;
	private AlertSchedule alertSchedule;
	private Status memberStatus;

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonProperty(value="eventAcceptanceStatus")
	@Column(name="event_acceptance_status")
	@Enumerated(EnumType.STRING)
	public EventAcceptanceStatus getEventAcceptanceStatus() {
		return eventAcceptanceStatus;
	}
	public void setEventAcceptanceStatus(EventAcceptanceStatus eventAcceptanceStatus) {
		this.eventAcceptanceStatus = eventAcceptanceStatus;
	}
	
	@JsonIgnoreProperties("memberLists")
	@JsonProperty("event")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="event_id",referencedColumnName = "id")
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	@JsonProperty("userProfile")
	@ManyToOne
	@Where(clause = "status = 'A'")
	@JoinColumn(name="member_id",referencedColumnName = "id" )
	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	
	@JsonProperty(value="userProfileId")
	@Column(name="member_id", insertable = false, updatable = false)
	public Long getUserProfileId() {
		return userProfileId;
	}
	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}
	
	@JsonProperty(value="createdDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Column(name="created_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	@JsonProperty(value="updatedDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Column(name="updated_date")	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@JsonProperty(value="alertSchedule")
	@ManyToOne
	@JoinColumn(name="alert_schedule_id",referencedColumnName = "id")
	public AlertSchedule getAlertSchedule() {
		return alertSchedule;
	}
	public void setAlertSchedule(AlertSchedule alertSchedule) {
		this.alertSchedule = alertSchedule;
	}
	@JsonProperty(value="isView")
	@Column(name="is_view",columnDefinition = "TINYINT(1)")
	public Boolean getIsView() {
		return isView;
	}
	public void setIsView(Boolean isView) {
		this.isView = isView;
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
	
}
