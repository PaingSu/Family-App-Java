package com.startinpoint.proj.familyapp.webservice.social;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.Connection;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.utils.DesEncrypter;

@Service("googleService")
public class GoogleService implements SocialService{

	@Value("${social.google.app_id}")
	private String googleAppId;

	@Value("${social.google.app_secret}")
	private String googleSecret;

	@Value("${application.server_url}")
	private String serverUrl;
	
	String clientName = "MOBILE";
	/**
	 * Get Google Connection
	 * @param code
	 * @return
	 */
	public Connection<Google> getGoogleConnection(String code,String client){
			
		String redirectUrl = "";
		if(client != null && client.equals(clientName)){
			redirectUrl = serverUrl + "/api/user/google/login/callback?client="+client;
		}
		else{
			redirectUrl = serverUrl + "/api/user/google/login/callback";
		}
		try{
			GoogleConnectionFactory googlecon =  new GoogleConnectionFactory(googleAppId,googleSecret);
			OAuth2Operations oauthOperations = googlecon.getOAuthOperations();
			AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, redirectUrl, null);
			return googlecon.createConnection(accessGrant);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**
	 * Get Userprofile by authorizeCode
	 */
	@Override
	public UserProfile getUserInfoByAuthorizeCode(String code,String client) throws FamilyAppWebserviceException {
		
		Connection<Google> connection = getGoogleConnection(code,client);
		Google google = connection.getApi();
		
		Person googleUser = google.plusOperations().getGoogleProfile();
//		String userId = googleUser.getId();
		
		String gender = googleUser.getGender();
				
		UserProfile profile = new UserProfile();
		profile.setProfileImageUrl(googleUser.getImageUrl());
		profile.setEmail(googleUser.getAccountEmail());
		profile.setPassword(null);
		profile.setUsername(googleUser.getDisplayName());
		profile.setBirthday(googleUser.getBirthday());
		profile.setEmailVerified(true);
		profile.setCreatedDate(new Date());
		System.out.println("Gender "+googleUser.getGender());
		
		return profile;
	}

	@Override
	public String createAuthorizationURL(String client) throws FamilyAppWebserviceException {
		String redirectUrl = "";
		if(client != null && client.equals(clientName)){ // for mobile
			redirectUrl = serverUrl + "/api/user/google/login/callback?client="+client;
		}
		else{ //for web
			redirectUrl = serverUrl + "/api/user/google/login/callback";
		}
		
		OAuth2Operations oauthOperations = new GoogleConnectionFactory(googleAppId,googleSecret).getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri(redirectUrl);
		params.setScope("https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
		return oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
	}

}
