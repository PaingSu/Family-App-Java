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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@Table(name="event")
@Where(clause = "event_status = 'A'")
public class Event implements Serializable {
	
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	 private String eventTitle;
	 private String description;
	 private String location;
	 private Date startDateTime;
	 private Date endDateTime;
     private Long invitee;
     private Date createdDate;
     private Date updatedDate;
     private Long familyProfileId;
     private FamilyProfile familyProfile;
     private AlertSchedule alertSchedule;
     private List<EventMember> eventMemberLists;
     private List<UserProfile> memberLists;
     private Boolean isView=false;
     private Status eventStatus;
     private EventAcceptanceStatus eventAcceptanceStatus;
	
	 
	 @Id
	 @GeneratedValue(strategy=GenerationType.AUTO)
	 @JsonProperty(value="id")
	 public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty(value="eventTitle")
	@Column(name="event_title")
	public String getEventTitle() {
		return eventTitle;
	}
	public void setEventTitle(String event_title) {
		this.eventTitle = event_title;
	}
	@JsonProperty(value="description")
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonProperty(value="location")
	@Column(name="location")
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@JsonProperty(value="startDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern=FamilyAppConst.CALENDAR_DATETIMEZONE_FORMAT)
	@DateTimeFormat(pattern=FamilyAppConst.CALENDAR_DATETIMEZONE_FORMAT)
	@Column(name="start_dateTime")
	public Date getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}
	@JsonProperty(value="endDateTime")	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern=FamilyAppConst.CALENDAR_DATETIMEZONE_FORMAT)
	@DateTimeFormat(pattern=FamilyAppConst.CALENDAR_DATETIMEZONE_FORMAT)
	@Column(name="end_dateTime")
	public Date getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}
	@JsonProperty(value="invitee")
	@Column(name="invitee")
	public Long getInvitee() {
		return invitee;
	}
	public void setInvitee(Long invitee) {
		this.invitee = invitee;
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
    
	@JsonProperty("familyProfileId")
	@Column(name="family_profile_id" )
	public Long getFamilyProfileId() {
		return familyProfileId;
	}
	public void setFamilyProfileId(Long familyProfileId) {
		this.familyProfileId = familyProfileId;
	}
	
	@JsonProperty("familyProfile")
	@ManyToOne
	@JsonInclude(Include.NON_NULL)
	@JoinColumn(name="family_profile_id",referencedColumnName = "id", insertable = false, updatable = false)
	public FamilyProfile getFamilyProfile() {
		return familyProfile;
	}
	public void setFamilyProfile(FamilyProfile familyProfile) {
		this.familyProfile = familyProfile;
	}
	
	@JsonProperty("alertSchedule")
	@ManyToOne
	@JoinColumn(name="alert_schedule_id" , referencedColumnName = "id")
	public AlertSchedule getAlertSchedule() {
		return alertSchedule;
	}
	public void setAlertSchedule(AlertSchedule alertSchedule) {
		this.alertSchedule = alertSchedule;
	}
	
	
	@JsonIgnore
	@OneToMany(mappedBy="event",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	public List<EventMember> getEventMemberLists() {
		return eventMemberLists;
	}
	public void setEventMemberLists(List<EventMember> eventMemberLists) {
		this.eventMemberLists = eventMemberLists;
	}
	
	@JsonProperty(value="isView")
	@Transient
	public Boolean getIsView() {
		return isView;
	}
	public void setIsView(Boolean isView) {
		this.isView = isView;
	}
	@JsonProperty(value="memberLists")
	@Transient
	public List<UserProfile> getMemberLists() {
		return memberLists;
	}
	public void setMemberLists(List<UserProfile> memberLists) {
		this.memberLists = memberLists;
	}
	
	@JsonProperty(value="eventStatus")
	@Column(name="event_status")
	@Enumerated(EnumType.STRING)
	public Status getEventStatus() {
		return eventStatus;
	}
	public void setEventStatus(Status eventStatus) {
		this.eventStatus = eventStatus;
	}
	@Transient
	@JsonProperty(value="eventAcceptanceStatus")
	public EventAcceptanceStatus getEventAcceptanceStatus() {
		return eventAcceptanceStatus;
	}
	public void setEventAcceptanceStatus(EventAcceptanceStatus eventAcceptanceStatus) {
		this.eventAcceptanceStatus = eventAcceptanceStatus;
	}
	
}
