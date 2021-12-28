package com.startinpoint.proj.familyapp.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @author nankhinmhwe
 *
 */
public class RequestUtil {
	/**
	 * Get email address from authentication object
	 * @return
	 */
	public static String getEmailFromAuthentication(){
		String username= null;
		  Authentication auth = (Authentication)SecurityContextHolder.getContext().getAuthentication();
	        if (auth != null) {
	            Object p = auth.getPrincipal();
	            if (p instanceof UserDetails) {
	                username = ((UserDetails)p).getUsername();
	            }
	        }
	    return username;
	}
	
	/**
	 * Get server base url
	 * @param request
	 * @return
	 */
	public static String getBaseUrl(HttpServletRequest request){
		String uri = request.getRequestURI();
		String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().length() - uri.length()) + request.getContextPath();
		return baseUrl;
	}
}
