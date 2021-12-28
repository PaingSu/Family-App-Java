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
@Table(name = "check_list_comment")
public class CheckListComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long checkListId;
	private CheckList checklist;
	private String comment;
	private Date createdDate;
	private Date updatedDate;
	private UserProfile member;
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

	@JsonProperty(value="checkListId")
	@Column(name="check_list_id")
	public Long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(Long checkListId) {
		this.checkListId = checkListId;
	}
	
	@JsonProperty(value="checklist")
	@ManyToOne
	@JoinColumn(name="check_list_id", referencedColumnName="id", insertable=false, updatable=false)
	public CheckList getChecklist() {
		return checklist;
	}

	public void setChecklist(CheckList checklist) {
		this.checklist = checklist;
	}

	@JsonProperty(value="comment")
	@Column(name="comment",columnDefinition = "LONGTEXT")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	@JsonProperty(value="member")
	@ManyToOne(optional = false)
	@JoinColumn(name="member_id")
	public UserProfile getMember() {
		return member;
	}

	public void setMember(UserProfile member) {
		this.member = member;
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

	
	

}
