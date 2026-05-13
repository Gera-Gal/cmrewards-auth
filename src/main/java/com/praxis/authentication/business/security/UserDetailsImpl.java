package com.praxis.authentication.business.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class UserDetailsImpl implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private ArrayList<String> authorities;  // ← SIEMPRE ArrayList
    private Integer userId;
    private String email;
    private Integer entId;
    
    public UserDetailsImpl() {
        this.authorities = new ArrayList<>();
    }
    
    public UserDetailsImpl(String username, String password, List<String> authorities, 
                           Integer userId, String email, Integer entId) {
        this.username = username;
        this.password = password;
        // ← FORZAR a ArrayList, NUNCA usar authorities directamente
        this.authorities = new ArrayList<>();
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
        this.userId = userId;
        this.email = email;
        this.entId = entId;
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();
    }
    
    public void setAuthorities(List<String> authorities) {
        // ← SIEMPRE crear nuevo ArrayList
        this.authorities = new ArrayList<>();
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
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

    public Integer getEntId() {
        return entId;
    }

    public void setEntId(Integer entId) {
        this.entId = entId;
    }

    @Override
    public String toString() {
        return "UserDetailsImpl [username=" + username + ", authorities=" + authorities + 
               ", userId=" + userId + ", email=" + email + ", entId=" + entId + "]";
    }
}