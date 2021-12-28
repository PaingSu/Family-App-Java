package com.startinpoint.proj.familyapp.webservice.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * 
 * Common Utility Class for JWT Token
 * 
 * @author nankhinmhwe
 *
 */
@Component
public class JwtTokenUtil implements Serializable {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String SECRET = "FamilyApp";
	private static final Long EXPIRATION_TIME = (long) 604800; // 7 days
	private static final Long REFRESH_TOKEN_EXPIRE_TIME = (long) 31540000; // 100
									

	/**
	 * Generated JWT Token
	 * 
	 * @param userDetails
	 * @return
	 * @throws FamilyAppWebserviceException 
	 */
	public String generateToken(Long userId, String email) throws FamilyAppWebserviceException {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("email", email);

		String token = Jwts.builder()
				.setClaims(map)				
				.setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_TIME * 1000)))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		return token;
	}

	/**
	 * Generated Refresh JWT Token
	 * @param userId
	 * @param email
	 * @return
	 * @throws FamilyAppWebserviceException
	 */
	public String generateRefreshToken(Long userId, String email) throws FamilyAppWebserviceException {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("email", email);

		String token = Jwts.builder()
				.setClaims(map)				
				.setExpiration(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRE_TIME * 1000)))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		return token;
	}

	/**
	 * Getting Expire Date from JWT Token
	 * 
	 * @param token
	 * @return
	 */
	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	/**
	 * Validate JWT Token Expire or Not
	 * 
	 * @param token
	 * @return
	 */
	public Boolean isTokenExpire(String token) {
		boolean flag = false;
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException | MalformedJwtException e) {
			logger.info("in token expired.");
			claims = null;
			flag = true;
		}
		return flag;
	}
	
	/**
	 * Get Email from JWT Token
	 * @param token
	 * @return
	 */
	public String getEmailFromToken(String token){
		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
			String email = (String) claims.get("email");
			return email;
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
			return null;
		}
	}

	/**
	 * Get UserId from JWT Token
	 * @param token
	 * @return
	 */
	public Long getUserIdFromToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
			Long userId = (Long) claims.get("userId");
			return userId;
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
			logger.error("Invalid JWT Token", ex);
			throw new BadCredentialsException("Invalid JWT Token.");
		}
	}
	
}
