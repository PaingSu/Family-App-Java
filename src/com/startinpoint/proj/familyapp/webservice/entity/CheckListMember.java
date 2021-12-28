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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name = "check_list_member")
public class CheckListMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private CheckList checklist;
	private Long userId;
	private UserProfile user;
	private Date createdDate;
	private Status status = Status.A;
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "check_list_id", referencedColumnName = "id" )
	@JsonProperty(value="checklist")
	public CheckList getChecklist() {
		return checklist;
	}

	public void setChecklist(CheckList checklist) {
		this.checklist = checklist;
	}
	

	@Column(name = "user_id")
	@JsonProperty(value="userId")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id",insertable= false,updatable=false)
	@JsonProperty(value="user")
	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	@JsonProperty(value="createdDate")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	@JsonProperty(value="status")
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
