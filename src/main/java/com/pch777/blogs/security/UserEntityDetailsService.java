package com.pch777.blogs.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pch777.blogs.repository.UserEntityRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserEntityDetailsService implements UserDetailsService {

	private final UserEntityRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository
				.findByUsername(username)
				.map(UserEntityDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
	}

}
