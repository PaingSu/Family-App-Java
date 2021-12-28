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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.JoinStatus;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name = "family_member")
@Where(clause = "status = 'A'")
public class FamilyMember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Boolean isFamilyCreator;
	private JoinStatus requestStatus;
	private Date joinDate;
	private Status status=Status.A;
	
	private FamilyProfile family;
	private UserProfile member;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="request_status")
	@Enumerated(EnumType.STRING)
	public JoinStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(JoinStatus requestStatus) {
		this.requestStatus = requestStatus;
	}	
	
	@Column(name="is_family_creator",columnDefinition = "TINYINT(1)")
	public Boolean getIsFamilyCreator() {
		return isFamilyCreator;
	}

	public void setIsFamilyCreator(Boolean isFamilyCreator) {
		this.isFamilyCreator = isFamilyCreator;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="join_date")
	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	@JsonIgnoreProperties("members")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "family_id", referencedColumnName = "id" )
	public FamilyProfile getFamily() {
		return family;
	}

	public void setFamily(FamilyProfile family) {
		this.family = family;
	}


	@JsonIgnoreProperties("familyList")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	public UserProfile getMember() {
		return member;
	}

	public void setMember(UserProfile member) {
		this.member = member;
	}

	@Column(name="status")
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
