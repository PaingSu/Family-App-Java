package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author ThoonSandy
 * @version 1.0
 * @since 14-05-2018
 */
@Entity
@Table(name="alert_schedule")
public class AlertSchedule implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String alertBeforeName;
	private Integer requiredTime;
		
	
	public AlertSchedule() {
		super();
	}
	public AlertSchedule(String alertBeforeName, Integer requiredTime) {
		this.alertBeforeName = alertBeforeName;
		this.requiredTime = requiredTime;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}


	
	@Column(name="alert_before_name")
	public String getAlertBeforeName() {
		return alertBeforeName;
	}
	public void setAlertBeforeName(String alertBeforeName) {
		this.alertBeforeName = alertBeforeName;
	}
	
	@Column(name="required_time")
	public Integer getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(Integer requiredTime) {
		this.requiredTime = requiredTime;
	}
		

}
