package com.pch777.blogs.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.service.AuthService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserSecurity {

	private AuthService authService;
	
	public boolean isOwnerOrAdmin(String ownerUsername, String loggedUsername) {
		return isOwner(ownerUsername, loggedUsername) || isAdmin(loggedUsername);
	}

	public boolean isOwner(String ownerUsername, String loggedUsername) {
		return loggedUsername.equalsIgnoreCase(ownerUsername);
	}
	


	public boolean isAdmin(String username) {
		if(authService.isUserPresent(username)) {
			UserEntity user = authService
				.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found."));
			return user.getRoles()
				.stream()
				.anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
		}
		return false;
	}
}
