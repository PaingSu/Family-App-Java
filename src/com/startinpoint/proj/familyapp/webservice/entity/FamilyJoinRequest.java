package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.Date;

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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name = "family_join_request")
public class FamilyJoinRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private JoinStatus joinStatus;
	private Date requestDate;
	private Date responseDate;
	private UserProfile user;
	private FamilyProfile family;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="join_status")
	@Enumerated(EnumType.STRING)
	public JoinStatus getJoinStatus() {
		return joinStatus;
	}

	public void setJoinStatus(JoinStatus joinStatus) {
		this.joinStatus = joinStatus;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="request_date",nullable=false)
	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="response_date")
	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id" )
	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = "family_id", referencedColumnName = "id" )
	public FamilyProfile getFamily() {
		return family;
	}

	public void setFamily(FamilyProfile family) {
		this.family = family;
	}

}
