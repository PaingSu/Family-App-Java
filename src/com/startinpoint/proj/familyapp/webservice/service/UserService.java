package com.startinpoint.proj.familyapp.webservice.service;

import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * 
 * @author nankhinmhwe
 *
 */

public interface UserService {
	UserProfile findById(Long id) throws FamilyAppWebserviceException;

	UserProfile saveOrUpdateUser(UserProfile user) throws FamilyAppWebserviceException;

	UserProfile findByUsername(String username) throws FamilyAppWebserviceException;
	
	UserProfile findByEmail(String email) throws FamilyAppWebserviceException;
}
