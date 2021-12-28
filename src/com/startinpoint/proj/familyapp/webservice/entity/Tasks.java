package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

@Entity
@Table(name = "tasks")
public class Tasks implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String taskName;
	private Long completedBy;
	private Long createdBy;
	private Date completedDateTime;
	private Date createdDateTime;
	private Long checkListId;
	private CheckList checkList;
	private Date updatedDateTime;
	private Boolean isComplete = false;
	private Status status = Status.A;
	private List<SubTask> subTaskList = new ArrayList<>();
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonProperty(value = "taskName")
	@Column(name = "task_name", nullable=false)
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	@JsonProperty(value = "completedBy")
	@Column(name = "completed_by")
	public Long getCompletedBy() {
		return completedBy;
	}
	public void setCompletedBy(Long completedBy) {
		this.completedBy = completedBy;
	}
	
	@JsonProperty(value = "createdBy")
	@Column(name = "created_by")
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	
	@JsonProperty(value = "completedDateTime")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "completed_datetime")
	public Date getCompletedDateTime() {
		return completedDateTime;
	}
	public void setCompletedDateTime(Date completedDateTime) {
		this.completedDateTime = completedDateTime;
	}
	
	@JsonProperty(value = "createdDateTime")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_datetime")
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "check_list_id", referencedColumnName = "id" , insertable = false, updatable = false)
	public CheckList getCheckList() {
		return checkList;
	}
	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}
	

	@JsonProperty(value = "checkListId")
	@Column(name="check_list_id")
	public Long getCheckListId() {
		return checkListId;
	}
	public void setCheckListId(Long checkListId) {
		this.checkListId = checkListId;
	}
	
	@JsonProperty(value = "updatedDateTime")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}
	
	@JsonProperty(value = "isComplete")
	@Column(name="is_complete",columnDefinition = "TINYINT(1)")
	public Boolean getIsComplete() {
		return isComplete;
	}
	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
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
	
	@JsonProperty(value = "subTaskList")
	@OneToMany(mappedBy="task", fetch = FetchType.LAZY)
    public List<SubTask> getSubTaskList() {
		return subTaskList;
	}

	public void setSubTaskList(List<SubTask> subTaskList) {
		this.subTaskList = subTaskList;
	}
	
}
