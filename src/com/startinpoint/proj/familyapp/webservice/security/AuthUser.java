package com.startinpoint.proj.familyapp.webservice.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 
 * @author nankhinmhwe
 *
 */
public class AuthUser extends User{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthUser(String email, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(email, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
	}
}
