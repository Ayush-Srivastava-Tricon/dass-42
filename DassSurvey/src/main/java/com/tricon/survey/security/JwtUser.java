package com.tricon.survey.security;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class JwtUser implements UserDetails{

	private static final long serialVersionUID = -524873938807585091L;
	
	private final String uuid;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final int active;
    private final Date lastPasswordResetDate;
    private String role;
    
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public JwtUser(
          String uuid,
          String firstName,
          String lastName,
          String email,
          String password,
          Collection<? extends GrantedAuthority> authorities,
          int enabled,
          Date lastPasswordResetDate
          
    ) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.active = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public String getUsername() {
		return email;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		if (this.active ==1) return true;
		return false;
	}
	public String getUuid() {
		return uuid;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public int getActive() {
		return active;
	}
	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}
}
