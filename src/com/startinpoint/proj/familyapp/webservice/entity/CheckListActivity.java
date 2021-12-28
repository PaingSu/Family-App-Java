package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name = "check_list_activity")
public class CheckListActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long checkListId;
	private String username;
	private String parentTaskName;
	private String taskName;
	private Date createdDate;
	private String action;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty(value="username")
	@Column(name="username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty(value="parentTaskName")
	@Column(name="parent_task_name")
	public String getParentTaskName() {
		return parentTaskName;
	}

	public void setParentTaskName(String parentTaskName) {
		this.parentTaskName = parentTaskName;
	} 
	
	@JsonProperty(value="taskName")
	@Column(name="task_name")
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@JsonProperty(value="createdDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@JsonProperty(value="action")
	@Column(name="action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@JsonProperty(value="checkListId")
	@Column(name="check_list_id")
	public Long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(Long checkListId) {
		this.checkListId = checkListId;
	}

}
