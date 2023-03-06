package com.pch777.blogs.rest.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.service.AuthService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AuthService authService;
	

	@Test
	void shouldRegisterANewUser() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
			.content("{\"firstName\":\"Al\",\"lastName\":\"Pacino\",\"username\":\"alpacino\",\"password\":\"123\"}")
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("User registered successfully!")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithoutFirstName() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"lastName\":\"Hanks\",\"username\":\"tom\",\"password\":\"123\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("First name may not be blank")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithoutLastName() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"username\":\"tom\",\"password\":\"123\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Last name may not be blank")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithoutUsername() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"password\":\"123\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Username may not be blank")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithUsernameLessThanThreeCharacters() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"username\":\"Ed\",\"password\":\"123\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Username cannot be less than 3 characters and more than 30 characters long")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithUsernameMoreThanThirtyCharacters() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"username\":\"TomTomTomTomTomTomTomTomTomTomT\",\"password\":\"123\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Username cannot be less than 3 characters and more than 30 characters long")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithoutPassword() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"username\":\"tom\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Password may not be blank")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithPasswordLessThanThreeCharacters() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"username\":\"Tom\",\"password\":\"12\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Password cannot be less than 3 characters and more than 30 characters long")));
			
	}
	
	@Test
	void shouldNotRegisterANewUserWithPasswordMoreThanThirtyCharacters() throws Exception {
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"username\":\"Tom\",\"password\":\"1234567890123456789012345678901\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Password cannot be less than 3 characters and more than 30 characters long")));
			
	}
	
	@Test
	void shouldNotRegisterExistedUser() throws Exception {
		// given
		RegisterUserDto userDto = new RegisterUserDto();
		userDto.setFirstName("Tom");
		userDto.setLastName("Hanks");
		userDto.setUsername("tom");
		userDto.setPassword("123");
		
		authService.signup(userDto);
		
		mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"Tom\",\"lastName\":\"Hanks\",\"username\":\"tom\",\"password\":\"123\"}")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("Error: Username is already taken!")));
			
	}

}
  