package com.pch777.blogs.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pch777.blogs.model.UserEntity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserEntityDetails implements UserDetails {

	private static final long serialVersionUID = 4449860334680492515L;

	private UserEntity user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = user
				.getRoles()
				.stream().map(r -> "ROLE_" + r)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
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
		return true;
	}

}
