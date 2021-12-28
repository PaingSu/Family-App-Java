package com.startinpoint.proj.familyapp.webservice.security;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.startinpoint.proj.familyapp.webservice.entity.UserProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.UserRole;
import com.startinpoint.proj.familyapp.webservice.service.UserService;

@Service
public class UserDetailsAuthenticationServiceImpl implements UserDetailsService{

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			UserProfile u = userService.findByEmail(email);
			if (u == null){
				return null;
			}
			
			return buildDetailFromEntity(u);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new UsernameNotFoundException("User not found");
		} 
	}
	
	private org.springframework.security.core.userdetails.User buildDetailFromEntity(UserProfile u) {
		String email = u.getEmail();
		String password = u.getPassword() == null ? "" : u.getPassword();

		boolean enabled =true;
		boolean accountNonExpired =true;
		boolean credentialsNonExpired =true;
		boolean accountNonLocked =true;
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_USER.toString()));

		AuthUser authUser = new AuthUser(email, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, authorities);
		return authUser;
	}
}
