package com.praxis.authentication.business.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails{

	private static final long serialVersionUID = 1L;
	private String username;
    private String password;
    //>private Collection<GrantedAuthority> authorities;
    private List<GrantedAuthority> authorities;
    private Integer userId;
    private String email;
    
    public UserDetailsImpl() {
	}
    
	public UserDetailsImpl(String username, String password, List<GrantedAuthority> authorities, Integer userId, String email) {
		super();
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.userId = userId;
		this.email = email;
	}

	@Override
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserDetailsImpl [username=" + username + ", password=" + password + ", authorities=" + authorities
				+ ", userId=" + userId + ", email=" + email + "]";
	}
}
