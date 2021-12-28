package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;
import com.startinpoint.proj.familyapp.webservice.entity.pojo.ActivityLog;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name = "check_list")
public class CheckList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long familyId;
	private FamilyProfile family;
	private String checkListName;
	private String description;
	private Date createdDate;
	private Date updatedDate;
	private Long creatorId;
	private Date targetDate;
	private Status status = Status.A;
	private List<Tasks> taskList= new ArrayList<>();
	private List<UserProfile> members = new ArrayList<>();
	private List<ActivityLog> activityLogs = new ArrayList<>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="family_id")
	@JsonProperty(value="familyId")
	public Long getFamilyId() {
		return familyId;
	}

	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}

	@ManyToOne
	@JoinColumn(name="family_id" ,insertable=false, updatable=false)
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value="family")
	public FamilyProfile getFamily() {
		return family;
	}

	public void setFamily(FamilyProfile family) {
		this.family = family;
	}

	@Column(name="check_list_name")
	@JsonProperty(value="checkListName")
	public String getCheckListName() {
		return checkListName;
	}

	public void setCheckListName(String checkListName) {
		this.checkListName = checkListName;
	}

	@Column(name="description", columnDefinition = "LONGTEXT")
	@JsonProperty(value="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date")
	@JsonProperty(value="createdDate")
	public Date getCreatedDate() {
		return createdDate;
	}

	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_date")
	@JsonProperty(value="updatedDate")
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="target_date")
	@JsonProperty(value="targetDate")
	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	@JsonProperty(value="creatorId")
	@Column(name="creator_id")
	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@JsonProperty(value="status")
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Transient
	@JsonProperty(value="taskList")
	public List<Tasks> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Tasks> taskList) {
		this.taskList = taskList;
	}

	@Transient
	@JsonProperty(value="checkListMembers")
	@JsonInclude(Include.NON_EMPTY)
	public List<UserProfile> getMembers() {
		return members;
	}

	public void setMembers(List<UserProfile> members) {
		this.members = members;
	}

	@Transient
	@JsonProperty(value="activityLogs")
	@JsonInclude(Include.NON_EMPTY)
	public List<ActivityLog> getActivityLogs() {
		return activityLogs;
	}

	public void setActivityLogs(List<ActivityLog> activityLogs) {
		this.activityLogs = activityLogs;
	}

}
