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
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.startinpoint.proj.familyapp.config.CustomDateSerializer;
import com.startinpoint.proj.familyapp.config.DateSerializer;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Gender;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Entity
@Table(name="user_profile")
public class UserProfile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String username;
	private String email;
	private String password;
	private Date createdDate;
	private Gender gender;
	private Date birthday;
	private String phoneNo;
	private String profileImageUrl;
	private String coverImageUrl;
	private Boolean isLogin = false;
	private Boolean emailVerified = false;
	private List<FamilyMember> familyMemberList= new ArrayList<>();
	
	private List<FamilyJoinRequest> familyPendingList = new ArrayList<>();
	private List<FamilyProfile> familyList= new ArrayList<>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty(value="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@NotNull@NotEmpty
	@Column(name="username")
	@JsonProperty(value="username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	@Column(name="password")
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty(value="codeword")
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Email
	@NotNull@NotEmpty
	@Column(name="email" ,nullable=false)
	@JsonProperty(value="email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date",nullable=false)
	@JsonProperty(value="createdDate")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
	@Enumerated(EnumType.STRING)
	@JsonProperty(value="gender")
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}	
	
	@JsonSerialize(using = DateSerializer.class)
	@Temporal(TemporalType.DATE)
	@JsonProperty(value="birthday")
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@JsonProperty("phone_no")
	@Column(name="phone_no")
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	
	@JsonProperty("profile_image_url")
	@Column(name="profile_image_url")
	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	@JsonProperty("cover_image_url")
	@Column(name="cover_image_url")
	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}


	@JsonProperty("is_login")
	@Column(name="is_login",columnDefinition = "TINYINT(1)")
	public Boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}

	@JsonProperty("email_verified")
	@Column(name="email_verified",columnDefinition = "TINYINT(1)")
	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	@JsonIgnore
	@OneToMany(mappedBy="member",cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@Where(clause = "status = 'A'")
	public List<FamilyMember> getFamilyMemberList() {
		return familyMemberList;
	}

	public void setFamilyMemberList(List<FamilyMember> familyMemberList) {
		this.familyMemberList = familyMemberList;
	}

	
	@Transient
	@JsonProperty(value="familyPendingList")
	public List<FamilyJoinRequest> getFamilyPendingList() {
		return familyPendingList;
	}

	public void setFamilyPendingList(List<FamilyJoinRequest> familyPendingList) {
		this.familyPendingList = familyPendingList;
	}

	@Transient
	@JsonProperty(value="familyList")
	public List<FamilyProfile> getFamilyList() {
		return familyList;
	}

	public void setFamilyList(List<FamilyProfile> familyList) {
		this.familyList = familyList;
	}


}
