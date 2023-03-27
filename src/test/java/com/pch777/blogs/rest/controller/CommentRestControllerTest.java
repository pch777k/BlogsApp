package com.pch777.blogs.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blogs.config.SecurityConfig;
import com.pch777.blogs.dto.CommentDto;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.security.UserEntityDetailsService;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.CommentService;


@WebMvcTest({CommentRestController.class})
@Import(SecurityConfig.class)
class CommentRestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CommentService commentService;
	
	@MockBean
	private UserEntityDetailsService userService;
	
	@MockBean
	private ArticleService articleService;
	
	@MockBean
	private AuthService authService;

	@MockBean
	private UserSecurity userSecurity;
	
	@Test
	void shouldFindAllComments() throws Exception {
		
		Comment comment = givenCommentWithContent("first comment");
		when(commentService.getAllComments()).thenReturn(List.of(comment));

		mockMvc
			.perform(get("/comments"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$.[0].content").value("first comment"));
	}

	private Comment givenCommentWithContent(String content) {
		Comment comment = new Comment();
		comment.setContent(content);
		return comment;
	}
	
	@Test
	void shouldFindAllCommentsByArticleId() throws Exception {
		
		Comment firstComment = givenCommentWithContent("first comment");
		
		when(commentService.getAllCommentsByArticleId(1L)).thenReturn(List.of(firstComment));
		
		mockMvc
			.perform(get("/articles/{articleId}/comments", 1L))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$.[0].content").value("first comment"));
	}
	
	@Test
	void shouldNotFoundCommentsByArticleId() throws Exception {
		
		when(commentService.getAllCommentsByArticleId(1L)).thenReturn(List.of());
		
		mockMvc
			.perform(get("/articles/{articleId}/comments", 1L))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}

	@Test
	@WithMockUser
	void shouldAddComment() throws Exception {
		
		when(articleService.getArticleById(1L)).thenReturn(Optional.of(new Article()));
		when(authService.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));

		CommentDto commentDto = givenCommentDto("some content");	
		
		mockMvc.perform(post("/articles/{articleId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(commentDto)))
			.andDo(print())
			.andExpect(status().isOk());		
	}
	
	@Test
	void shouldNotAddCommentUserUnauthorized() throws Exception {
		
		when(articleService.getArticleById(1L)).thenReturn(Optional.of(new Article()));
		when(authService.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));

		CommentDto commentDto = givenCommentDto("some content");	
		
		mockMvc.perform(post("/articles/{articleId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(commentDto)))
			.andDo(print())
			.andExpect(status().isUnauthorized());		
	}
	
	@Test
	@WithMockUser(username = "username")
	void shouldReturnNotFoundStatusWhenUpdateComment() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(1L, "content", userEntity);	
		CommentDto commentDto = givenCommentDto("updated content");
		
		when(authService.findByUsername("username")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(2L)).thenReturn(Optional.empty());
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(true);
		
		mockMvc.perform(put("/comments/{commentId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(commentDto)))
			.andDo(print())
			.andExpect(status().isNotFound());		
	}
	
	@Test
	void shouldReturnUnauthorizedStatusWhenUpdateComment() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(1L, "content", userEntity);	
		CommentDto commentDto = givenCommentDto("updated content");
		
		when(authService.findByUsername("username")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(2L)).thenReturn(Optional.empty());
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(true);	
		
		mockMvc.perform(put("/comments/{commentId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(commentDto)))
			.andDo(print())
			.andExpect(status().isUnauthorized());	
	}

	@Test
	@WithMockUser(username = "username")
	void shouldUpdateComment() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(1L, "content", userEntity);
		CommentDto commentDto = givenCommentDto("updated content");
		
		when(authService.findByUsername("username")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(1L)).thenReturn(Optional.of(comment));
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(true);
		
		mockMvc.perform(put("/comments/{commentId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(commentDto)))
			.andDo(print())
			.andExpect(status().isOk());
		
	}
	
	@Test
	@WithMockUser(username = "user")
	void shouldReturnForbiddenStatusWhenUpdateCommentById() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(1L, "content", userEntity);	
		CommentDto commentDto = givenCommentDto("updated content");
		
		when(authService.findByUsername("user")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(1L)).thenReturn(Optional.of(comment));
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(false);		
		
		mockMvc.perform(put("/comments/{commentId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(commentDto)))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "username")
	void shouldDeleteCommentById() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(1L, "content", userEntity);		

		when(commentService.getCommentById(1L)).thenReturn(Optional.of(comment));
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(true);		
		
		mockMvc.perform(delete("/comments/{commentId}", 1L))
			.andDo(print())
			.andExpect(status().isNoContent());
	}
	
	@Test
	@WithMockUser(username = "user")
	void shouldReturnForbiddenStatusWhenDeleteCommentById() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(1L, "content", userEntity);		
		
		when(authService.findByUsername("user")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(1L)).thenReturn(Optional.of(comment));
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "user")).thenReturn(false);		
		
		mockMvc.perform(delete("/comments/{commentId}", 1L))
			.andDo(print())
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = "username")
	void shouldReturnNotFoundStatusWhenDeleteCommentById() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(2L, "content", userEntity);		
		
		when(authService.findByUsername("user")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(1L)).thenReturn(Optional.empty());
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(true);		
		
		mockMvc.perform(delete("/comments/{commentId}", 1L))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldReturnUnauthorizedWhenDeleteCommentById() throws Exception {
		UserEntity userEntity = givenUserEntity();
		Comment comment = givenComment(2L, "content", userEntity);		
		
		when(authService.findByUsername("user")).thenReturn(Optional.of(userEntity));
		when(commentService.getCommentById(1L)).thenReturn(Optional.empty());
		when(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), "username")).thenReturn(true);		
		
		mockMvc.perform(delete("/comments/{commentId}", 1L))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	private CommentDto givenCommentDto(String content) {
		CommentDto commentDto = new CommentDto();
		commentDto.setContent(content);
		return commentDto;
	}

	private Comment givenComment(Long id, String content, UserEntity userEntity) {
		Comment comment = new Comment();
		comment.setId(id);
		comment.setContent(content);
		comment.setUser(userEntity);
		return comment;
	}

	private UserEntity givenUserEntity() {
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("username");
		return userEntity;
	}
	
	public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
