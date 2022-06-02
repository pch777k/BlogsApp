package com.pch777.blogs.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;

import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.UserEntityRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

	private final UserEntityRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageFileService imageFileService;
	private static final String DEFAULT_IMAGE_FILENAME = "default-avatar";
	private static final long DEFAULT_IMAGE_ID = 1L;
	
	
	public UserEntity signup(RegisterUserDto userDto) throws IOException, ResourceNotFoundException {
		UserEntity user = new UserEntity();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setUsername(userDto.getUsername());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setRoles(userDto.getRoles());
		ImageFile file = imageFileService
				.getImageById(DEFAULT_IMAGE_ID)
				.orElseThrow(() -> new ResourceNotFoundException("File with name " + DEFAULT_IMAGE_ID + " not found"));
		user.setImage(file);
		return userRepository.save(user);
	}
	
	public void ifNotAnonymousUserGetIdToModel(Model model, String username) throws ResourceNotFoundException {
		if(!username.equalsIgnoreCase("anonymousUser")) {
			UserEntity loggedUser = userRepository
					.findByUsername(username)
					.orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
			model.addAttribute("loggedUserId", loggedUser.getId());
		}
	}
    
	
	public Boolean isUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public Optional<UserEntity> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public Optional<UserEntity> findById(Long id) {
		return userRepository.findById(id);
	}

	public boolean isUserPresent(String username) {
		return userRepository.existsByUsername(username);
	}

	public RegisterUserDto userToRegisterDto(UserEntity user) {
		RegisterUserDto userDto = new RegisterUserDto();
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setPassword(user.getPassword());
		userDto.setUsername(user.getUsername());
		return userDto;
	}
	
}
