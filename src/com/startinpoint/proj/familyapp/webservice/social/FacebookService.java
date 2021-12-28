package com.startinpoint.proj.familyapp.webservice.social;


import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.CoverPhoto;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import com.startinpoint.proj.familyapp.util.CalendarUtil;
import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.Gender;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.utils.DesEncrypter;

/**
 * 
 * Facebook Service
 * 
 * @author nankhinmhwe
 *
 */
@Service("facebookService")
public class FacebookService implements SocialService {
	/**
	 * logger
	 */
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Value("${social.facebook.app_id}")
	private String facebookAppId;

	@Value("${social.facebook.app_secret}")
	private String facebookSecret;

	@Value("${application.server_url}")
	private String serverUrl;
	
	String clientName = "MOBILE";

	/**
	 * Create Facebook Authorization Url
	 * @return
	 */
	@Override
	public String createAuthorizationURL(String client){
		String redirectUrl = "";
		if(client != null && client.equals(clientName)){ // for mobile
			redirectUrl = serverUrl + "/api/user/facebook/login/callback?client="+client;
		}
		else{ //for web
			redirectUrl = serverUrl + "/api/user/facebook/login/callback";
		}
	    FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
	    OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
	    OAuth2Parameters params = new OAuth2Parameters();
	    params.setRedirectUri(redirectUrl);
	    params.setScope("public_profile,email,user_birthday");
	    return oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE,params);
	}
	
	
	/**
	 * Create facebook access token from authorize code
	 * 
	 * @param code
	 * @return
	 */
	public String createFacebookAccessToken(String code,String client) {
		String redirectUrl = "";
		if(client != null && client.equals(clientName)){
			redirectUrl = serverUrl + "/api/user/facebook/login/callback?client="+client;
		}
		else{
			redirectUrl = serverUrl + "/api/user/facebook/login/callback";
		}
		
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, redirectUrl, null);
		return accessGrant.getAccessToken();
	}

	/**
	 * Get Userprofile by authorizeCode
	 */
	@Override
	public UserProfile getUserInfoByAuthorizeCode(String code,String client) throws FamilyAppWebserviceException {
		String accessToken = createFacebookAccessToken(code,client);
		Facebook facebook = new FacebookTemplate(accessToken);
		String[] fields = { "id", "email", "first_name", "last_name","birthday","cover", "about", "relationship_status", "gender" };
		User fbUser = facebook.fetchObject("me", User.class, fields);
		
		//get cover image url
		CoverPhoto cover = fbUser.getCover();
		String coverImageUrl = cover.getSource();
		
		//get profile image url
		String profileImage = "http://graph.facebook.com/"+fbUser.getId()+"/picture?type=large";
		UserProfile profile = new UserProfile();
		
		//get birthday
		String birthday = fbUser.getBirthday(); //MM/DD/YYYY
		if(birthday != null && !fbUser.getBirthday().trim().isEmpty()){
			Date birthDate = CalendarUtil.parseDate(birthday, "MM/DD/YYYY");
			profile.setBirthday(birthDate);
		}
		
		
		//get gender
		if(fbUser.getGender().equals("female")){
			profile.setGender(Gender.F);
		}
		else if(fbUser.getGender().equals("male")){
			profile.setGender(Gender.M);
		}
		else{
			profile.setGender(Gender.NONE);
		}
		profile.setCoverImageUrl(coverImageUrl);
		profile.setProfileImageUrl(profileImage);
		profile.setEmail(fbUser.getEmail());
		profile.setPassword(null);
		profile.setUsername(fbUser.getFirstName()+" "+fbUser.getLastName());
		profile.setEmailVerified(true);
		profile.setCreatedDate(new Date());
		
		return profile;
	}
	
	
	

}
