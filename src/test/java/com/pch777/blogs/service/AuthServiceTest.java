package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.UserEntity;

@Import({ AuthService.class })
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext
class AuthServiceTest {

	@Autowired
	private AuthService authService;

	private RegisterUserDto givenRegisterUserDto(String firstName, String lastName, String username, String password) {
		return new RegisterUserDto(firstName, lastName, username, password, Set.of("USER"));
	}

	@Test
	void testSignup() throws ResourceNotFoundException {
		// given
		RegisterUserDto user = givenRegisterUserDto("John", "Doe", "johndoe", "pass123");

		// when
		UserEntity userResult = authService.signup(user);

		// then
		assertEquals(user.getFirstName(), userResult.getFirstName());
		assertEquals(user.getLastName(), userResult.getLastName());
		assertNotNull(userResult.getPassword());
		assertEquals("default-avatar.png", userResult.getImage().getFilename());
		assertEquals(Set.of("USER"), userResult.getRoles());
	} 

	@Test
	void userWithGivenUsernameShouldExists() throws ResourceNotFoundException {
		// given
		UserEntity userResult = givenUserEntity();

		// when
		boolean result = authService.isUsernameExists(userResult.getUsername());

		// then
		assertTrue(result);
	}

	@Test
	void userWithGivenUsernameShouldNotExists() throws ResourceNotFoundException {
		// given
		UserEntity userResult = givenUserEntity();

		// when
		boolean result = authService.isUsernameExists(userResult.getUsername() + "name");

		// then
		assertFalse(result);
	}

	@Test
	void shouldFindUserByUsername() throws ResourceNotFoundException {
		// given
		UserEntity userResult = givenUserEntity();

		// when
		Optional<UserEntity> result = authService.findByUsername(userResult.getUsername());

		// then
		assertTrue(result.isPresent());
	}

	@Test
	void shouldFindUserById() throws ResourceNotFoundException {
		// given
		UserEntity userResult = givenUserEntity();

		// when
		Optional<UserEntity> result = authService.findById(userResult.getId());

		// then
		assertTrue(result.isPresent());
	}
	
	private UserEntity givenUserEntity() throws ResourceNotFoundException {
		RegisterUserDto user = givenRegisterUserDto("John", "Doe", "johndoe", "pass123");
		UserEntity userResult = authService.signup(user);
		return userResult;
	}

}
