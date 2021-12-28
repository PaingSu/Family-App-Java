package com.startinpoint.proj.familyapp.webservice.dao;

import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

public interface UserDao {
	public UserProfile  saveUser(UserProfile user) throws FamilyAppWebserviceException;

	public UserProfile findByUsername(String username) throws FamilyAppWebserviceException;

	public UserProfile findByEmail(String email) throws FamilyAppWebserviceException;
	
	public UserProfile findById(Long id)throws FamilyAppWebserviceException;

}
