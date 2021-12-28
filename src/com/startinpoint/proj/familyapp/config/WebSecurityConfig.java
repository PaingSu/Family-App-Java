package com.startinpoint.proj.familyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import com.startinpoint.proj.familyapp.webservice.security.JwtAuthenticationTokenFilter;
import com.startinpoint.proj.familyapp.webservice.security.RestAuthenticationEntryPoint;
import com.startinpoint.utils.DesEncrypter;
/**
 * 
 * @author nankhinmhwe
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
    public GenericFilterBean authenticationFilter() {
        return new JwtAuthenticationTokenFilter();
    }
	
	@Bean
    public AuthenticationManager authenticationManagerBean() throws Exception        
    {
        return super.authenticationManagerBean();
    }
	
	@Bean
	public DesEncrypter passwordEncoder(){
		try {
			return new DesEncrypter();
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	 @Override
	    protected void configure(HttpSecurity http) throws Exception {
		 
		 http
		 .cors(); //enable cors
		 
		http
		 .csrf().disable() 
		 .headers().disable() //disable header
	     .authorizeRequests()  	     
	     .antMatchers("/login","/signup",
	    		 "/upload_image","/download_image").permitAll()   
	     .and()
	     .exceptionHandling() //default response if the client wants to get a resource unauthorized
         .authenticationEntryPoint(new RestAuthenticationEntryPoint());

		 http
         .addFilterAfter(authenticationFilter(), BasicAuthenticationFilter.class);
		 
	    }	
	
}
