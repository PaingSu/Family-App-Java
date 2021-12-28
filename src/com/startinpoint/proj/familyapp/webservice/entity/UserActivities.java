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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;

@Entity
@Table(name = "user_activities")
public class UserActivities implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long userId;
	private Long familyId;
	private String action;
	private Date actionTime;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	
	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "family_id", nullable = false)
	public Long getFamilyId() {
		return familyId;
	}
	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}
	
	@Column(name = "action")
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "action_time")
	public Date getActionTime() {
		return actionTime;
	}
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
	
	

}
