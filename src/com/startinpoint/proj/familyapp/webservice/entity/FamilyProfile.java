package com.startinpoint.proj.familyapp.webservice.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.DateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Status;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name = "family_profile")
//@Where(clause = "status = 'A'")
public class FamilyProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String familyCode;
	private String familyName;
	private String description;
	private Date familyStartDate;
	private Date createdDate;
	private Long familyCreatorId;
	private String imageUrl;
	private List<FamilyMember> familyMemberList;
	private Status status= Status.A;
	
	private List<UserProfile> members= new ArrayList<>();
	private List<FamilyJoinRequest> joinRequestList = new ArrayList<>();
	
	private Boolean isFamilyCreator;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "family_code", nullable = false)
	@JsonProperty(value="familyCode")
	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	@Column(name = "family_name")
	@NotNull@NotEmpty
	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	@Column(name = "description",columnDefinition = "LONGTEXT")
	@NotNull@NotEmpty
	@JsonProperty(value="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonSerialize(using = DateSerializer.class)
	@Temporal(TemporalType.DATE)
	@Column(name="family_start_date")
	@JsonProperty(value="familyStartDate")
	public Date getFamilyStartDate() {
		return familyStartDate;
	}

	public void setFamilyStartDate(Date familyStartDate) {
		this.familyStartDate = familyStartDate;
	}

	@Column(name="family_creator_id")
	@JsonProperty(value="familyCreatorId")
	public Long getFamilyCreatorId() {
		return familyCreatorId;
	}

	public void setFamilyCreatorId(Long familyCreatorId) {
		this.familyCreatorId = familyCreatorId;
	}


	@Column(name="image_url")
	@JsonProperty(value="imageUrl")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name="status")
	@Enumerated(EnumType.STRING)
	@JsonProperty(value="status")
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@JsonFormat(pattern=FamilyAppConst.CALENDAR_DATE_FORMAT)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date")
	@JsonProperty(value="createdDate")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@JsonIgnore
	@OneToMany(mappedBy="family",cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@Where(clause = "status = 'A'")
	public List<FamilyMember> getFamilyMemberList() {
		return familyMemberList;
	}

	public void setFamilyMemberList(List<FamilyMember> familyMemberList) {
		this.familyMemberList = familyMemberList;
	}

	@Transient
	@JsonProperty(value="members")
	public List<UserProfile> getMembers() {
		return members;
	}

	public void setMembers(List<UserProfile> members) {
		this.members = members;
	}

	@Transient
	@JsonProperty(value="joinRequestList")
	public List<FamilyJoinRequest> getJoinRequestList() {
		return joinRequestList;
	}

	public void setJoinRequestList(List<FamilyJoinRequest> joinRequestList) {
		this.joinRequestList = joinRequestList;
	}
	
	
	@Transient
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value="isFamilyCreator")
	public Boolean getIsFamilyCreator() {
		return isFamilyCreator;
	}

	public void setIsFamilyCreator(Boolean isFamilyCreator) {
		this.isFamilyCreator = isFamilyCreator;
	}
}
