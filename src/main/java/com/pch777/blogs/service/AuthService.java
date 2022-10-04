package com.pch777.blogs.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

	private final UserEntityRepository userRepository;
	private final BlogRepository blogRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageFileService imageFileService;
	private static final long DEFAULT_IMAGE_ID = 1L;
		
	public UserEntity signup(RegisterUserDto userDto) throws ResourceNotFoundException {
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
		if (!username.equalsIgnoreCase("anonymousUser")) {
			UserEntity loggedUser = userRepository
					.findByUsername(username)
					.orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found."));
			Optional<Blog> blog = blogRepository
					.findAll()
					.stream()
					.filter(b -> b.getUser().getId().equals(loggedUser.getId()) )
					.findFirst();
			if(blog.isPresent()) {
				model.addAttribute("loggedUserBlogId", blog.get().getId());
			}
			
			model.addAttribute("loggedUserId", loggedUser.getId());
		}
	}
	
	public boolean isUserHasBlog(String username) {
		return blogRepository
				.findAll()
				.stream()
				.anyMatch(b -> b.getUser().getUsername().equals(username));
	}
    	
	public Boolean isUsernameExists(String username) {
		return userRepository.existsByUsernameIgnoreCase(username);
	}
	
	public Optional<UserEntity> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public Optional<UserEntity> findById(Long id) {
		return userRepository.findById(id);
	}

	public boolean isUserPresent(String username) {
		return userRepository.existsByUsernameIgnoreCase(username);
	}

	public RegisterUserDto userToRegisterDto(UserEntity user) {
		RegisterUserDto userDto = new RegisterUserDto();
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setPassword(user.getPassword());
		userDto.setUsername(user.getUsername());
		return userDto;
	}
	
	public int getTotalUsers() {
		return userRepository.findAll().size();
	}
	
}
