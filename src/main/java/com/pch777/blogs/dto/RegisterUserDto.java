package com.pch777.blogs.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserDto {
	
	@NotBlank(message = "First name may not be blank")
	private String firstName;
	
	@NotBlank(message = "Last name may not be blank")
	private String lastName;
	
	@NotBlank(message = "Username may not be blank")
	@Size(min = 3, max = 30, message = "Username cannot be less than 3 characters and more than 30 characters long")
	private String username;
	
	@NotBlank(message = "Password may not be blank")
	@Size(min = 3, max = 30, message = "Password cannot be less than 3 characters and more than 30 characters long")
	private String password;
	
	private Set<String> roles = new HashSet<>();
	
	
}
