package com.tricon.survey.jwt.service;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationCustomResponse implements Serializable{

	private static final long serialVersionUID = -3567054753302711911L;

	private String token;

	private String userName;
	
	private String firstName;
	
	private Collection<? extends GrantedAuthority> authorities;

	public JwtAuthenticationCustomResponse(String token, String userName,
			Collection<? extends GrantedAuthority> authorities,String firstName) {
		this.token = token;
		this.userName = userName;
		this.authorities = authorities;
		this.firstName = firstName;
	}

	public String getToken() {
		return token;
	}

	public String getUserName() {
		return userName;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
}
