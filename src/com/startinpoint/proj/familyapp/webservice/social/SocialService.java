package com.startinpoint.proj.familyapp.webservice.social;

import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

/**
 * Social Service
 * @author nankhinmhwe
 *
 */
public interface SocialService {
	public UserProfile getUserInfoByAuthorizeCode(String code,String client)throws FamilyAppWebserviceException;
	
	public String createAuthorizationURL(String client) throws FamilyAppWebserviceException;
}
