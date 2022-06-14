package com.pch777.blogs.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pch777.blogs.dto.ImageFileDto;
import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class AuthController {

	private final AuthService authService;
	private final ImageFileService imageFileService;

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("userDto", new RegisterUserDto());
		return "signup-form";
	}

	@Transactional
	@PostMapping("/register")
	public String processRegister(@Valid RegisterUserDto userDto, 
			BindingResult bindingResult, Model model) throws IOException, ResourceNotFoundException {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("userDto", new RegisterUserDto());
			return "signup-form";
		}

		if (authService.isUsernameExists(userDto.getUsername())) {
			model.addAttribute("exist", true);
			return "signup_form";
		}
		Set<String> roles = new HashSet<>();
		roles.add("USER");
		userDto.setRoles(roles);
		authService.signup(userDto);

		model.addAttribute("username", userDto.getUsername());
		model.addAttribute("imageFileDto", new ImageFileDto());
		return "register-success";
	}
	
	@GetMapping("/users/{id}/image/add")
	public String showImageForm(@PathVariable Long id, Model model) throws ResourceNotFoundException {
		UserEntity user = authService.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean createButton = false;
		model.addAttribute("createButton", createButton);
		model.addAttribute("user", user);

		return "image-user-form";
	}
	
	@Transactional
	@PostMapping("/users/{id}/image/add")
	public String addImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException, IOException {
		UserEntity user = authService.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
		ImageFile imageFile = ImageFile.builder().file(file.getBytes()).filename(file.getOriginalFilename())
				.contentType(file.getContentType()).createdAt(LocalDate.now()).fileLength(file.getSize()).build();
		imageFileService.saveImageFile(imageFile);
		user.setImage(imageFile);

		return "redirect:/";
	}
	
	@GetMapping("/users/{id}/image")
	public void showImage(@PathVariable Long id, HttpServletResponse response)
			throws ServletException, IOException, ResourceNotFoundException {

		UserEntity user = authService.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		response.getOutputStream().write(user.getImage().getFile());
		response.getOutputStream().close();
	}

}
