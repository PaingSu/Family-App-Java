package com.startinpoint.proj.familyapp.webservice.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.startinpoint.proj.familyapp.util.StringUtils;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;


/**
 * 
 * @author nankhinmhwe
 *
 */
public class JwtAuthenticationTokenFilter extends GenericFilterBean {

	protected final Log logger = LogFactory.getLog(this.getClass());
		
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsAuthenticationServiceImpl userDetailsService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        logger.info("Request Method: " + httpRequest.getMethod() + " ,Request Url: " + httpRequest.getRequestURL()
		+ ", Request Params: " + new ContentCachingRequestWrapper(httpRequest).getParameterMap());

		//skip facebook
		if(httpRequest.getRequestURI().contains("facebook")){
//			nothing to do
		}
		else if(httpRequest.getRequestURI().contains("google")){
			
		}
		else if (httpRequest.getRequestURI().contains("/auth/")) {
			String authToken = httpRequest.getHeader("Authorization") != null?httpRequest.getHeader("Authorization"):httpRequest.getHeader("authorization");
			
			//validate auth key missing
			if (null == authToken || authToken.isEmpty() ) {
				logger.info(FamilyAppConst.AUTH_KEY_MISSING);
				httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
				httpResponse.getWriter().write(StringUtils.responseString(FamilyAppConst.AUTH_KEY_MISSING));
				return;
			}
			
			 
			//auth key expire
			if(jwtTokenUtil.isTokenExpire(authToken)){
				logger.info(FamilyAppConst.TOKEN_EXPIRED);
				httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
				httpResponse.getWriter().write(StringUtils.responseString(FamilyAppConst.TOKEN_EXPIRED));
				return;
			}
			
			String email = jwtTokenUtil.getEmailFromToken(authToken);	
			if(email == null){//invalid token
				logger.info("Invalid Token");
				httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
				httpResponse.getWriter().write(StringUtils.responseString(FamilyAppConst.UNAUTHORIZED));
				return;
			}
			else {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
				if(userDetails == null){
					logger.info("Invalid Token");
					httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
					httpResponse.getWriter().write(StringUtils.responseString(FamilyAppConst.UNAUTHORIZED));
					return;
				}
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}		
		
		} 
		

		chain.doFilter(request, response);
	}
	

}
