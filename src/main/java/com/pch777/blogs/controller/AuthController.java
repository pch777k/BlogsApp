package com.pch777.blogs.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.dto.ImageFileDto;
import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
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
	private final PasswordEncoder passwordEncoder;

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
	public String processRegister(@Valid RegisterUserDto userDto, BindingResult bindingResult, Model model) throws IOException, ResourceNotFoundException {
		if (bindingResult.hasErrors()) {
			model.addAttribute("userDto", new RegisterUserDto());
			return "signup-form";
		}

		if (authService.isUsernameExists(userDto.getUsername())) {
			model.addAttribute("exist", true);
			return "signup_form";
		}
		userDto.setRoles(Set.of("USER"));
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
		// model.addAttribute("imageFileDto", new ImageFileDto());

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

		// model.addAttribute("blog", blog);
		// model.addAttribute("imageFileDto", new ImageFileDto());

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
	

//	@GetMapping("/image/form")
//	public String showImageForm(Model model) {
//
//		User user = userService.findUserById(userId);
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String email = auth.getName();
//		if (userSecurity.isOwnerOrAdmin(user.getEmail(), email)) {
//
//			UserProfileDto userProfileDto = UserProfileDto.builder().nickname(user.getNickname()).email(user.getEmail())
//					.build();
//
//			model.addAttribute("userProfileDto", userProfileDto);
//			model.addAttribute("photoFileDto", new PhotoFileDto());
//			model.addAttribute("currentUser", userService.findUserByEmail(email));
//		} else {
//			throw new ForbiddenException("Access denied");
//		}
//
//		return "image-form";
//	}
/*
	@Transactional
	@RequestMapping("/image/add")
	public String addImage(RedirectAttributes redirectAttributes, @ModelAttribute("username") String username,
			@Valid @ModelAttribute("imageFileDto") ImageFileDto imageFileDto, BindingResult result, Model model) throws IOException {
		System.out.println("username: " +  username);
		UserEntity user = authService.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User wiith username " + username + " not found"));

//		if (result.hasErrors()) {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			String email = auth.getName();
//			userProfileDto = UserProfileDto.builder().nickname(user.getNickname()).email(user.getEmail()).build();
//			model.addAttribute("userProfileDto", userProfileDto);
//			model.addAttribute("currentUser", userService.findUserByEmail(email));
//			return "profile";
//		}
		System.out.println("image size: " +  imageFileDto.getFileImage().getSize());
		ImageFile imageFile = ImageFile
				.builder()
				.file(imageFileDto.getFileImage().getBytes())
				.filename(imageFileDto.getFileImage().getOriginalFilename())
				.contentType(imageFileDto.getFileImage().getContentType()).createdAt(LocalDate.now())
				.fileLength(imageFileDto.getFileImage().getSize())
				.build();
		imageFileService.saveImageFile(imageFile);
		user.setImage(imageFile);
		//redirectAttributes.addFlashAttribute("editedPhoto", "The photo has been edited successfully.");

		return "register-success";
	} */

//	@PostMapping
//	public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto userDto) {
//		if(authService.isUsernameExists(userDto.getUsername())) {
//			return ResponseEntity
//			          .badRequest()
//			          .body(new MessageResponse("Error: Username is already taken!"));
//		}
//		authService.signup(userDto);
//		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//	}
}
