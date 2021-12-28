package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;

@Entity
@Table(name="sub_task")
public class SubTask implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Tasks task;
	private String subTaskName;
	private Long completedBy;
	private Date completedDateTime;
	private Long createdBy;
	private Date createdDateTime;
	private Date updatedDateTime;
	private Boolean isComplete = false;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty(value = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore // because infinite recursive loop in json response
	@ManyToOne
	@JoinColumn(name = "task_id", referencedColumnName = "id")
	public Tasks getTask() {
		return task;
	}

	public void setTask(Tasks task) {
		this.task = task;
	}

	@JsonProperty(value = "subTaskName")
	@Column(name = "sub_task_name")
	public String getSubTaskName() {
		return subTaskName;
	}

	public void setSubTaskName(String subTaskName) {
		this.subTaskName = subTaskName;
	}

	@JsonProperty(value = "completedBy")
	@Column(name = "completed_by")
	public Long getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(Long completedBy) {
		this.completedBy = completedBy;
	}

	@JsonProperty(value = "completedDateTime")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "completed_date")
	public Date getCompletedDateTime() {
		return completedDateTime;
	}

	public void setCompletedDateTime(Date completedDateTime) {
		this.completedDateTime = completedDateTime;
	}
	
	@JsonProperty(value = "createdBy")	
	@Column(name = "created_by")
	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	@JsonProperty(value = "createdDateTime")	
	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	@JsonProperty(value = "updatedDateTime")	
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

}
