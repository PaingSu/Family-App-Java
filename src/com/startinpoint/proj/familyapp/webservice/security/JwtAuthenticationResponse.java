package com.startinpoint.proj.familyapp.webservice.security;

import java.io.Serializable;
import java.util.Date;

import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;

/**
 * 
 * @author nankhinmhwe
 *
 */
public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private String token;
    private String refreshToken;
    private Date expireddate;
    private UserProfile user;
	
	public JwtAuthenticationResponse(){
		
	}
	public JwtAuthenticationResponse(String token, String refreshToken, Date expireddate,
			UserProfile user) {
		super();
		this.token = token;
		this.refreshToken = refreshToken;
		this.expireddate = expireddate;
		this.user = user;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpireddate() {
		return expireddate;
	}

	public void setExpireddate(Date expireddate) {
		this.expireddate = expireddate;
	}

	public UserProfile getUser() {
		return user;
	}
	
	public void setUser(UserProfile user) {
		this.user = user;
	}
	
	public String getToken() {
		return token;
	}
}
