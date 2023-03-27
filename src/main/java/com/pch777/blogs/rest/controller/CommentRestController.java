package com.pch777.blogs.rest.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.blogs.dto.CommentDto;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.CommentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class CommentRestController {

	private final CommentService commentService;
	private final ArticleService articleService;
	private final AuthService authService;
	private final UserSecurity userSecurity;
	
	@GetMapping("/comments")
	public List<Comment> findAllComments() {
		return commentService.getAllComments();
	}
	
	@GetMapping("/articles/{articleId}/comments")
	public List<Comment> findAllCommentsByArticleId(@PathVariable Long articleId) {
		return commentService.getAllCommentsByArticleId(articleId);		
	}
	
	@Transactional
	@PostMapping("/articles/{articleId}/comments")
	public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto, 
			@PathVariable Long articleId, Principal principal) {

		return articleService
				.getArticleById(articleId)
				.map(article -> {
					UserEntity user = toUser(principal);
					toCreateComment(commentDto, article, user);	    
				    return ResponseEntity.ok().build();
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@Transactional
	@PutMapping("comments/{commentId}")
	public ResponseEntity<Object> updateComment(@RequestBody CommentDto commentDto, 
			@PathVariable Long commentId, Principal principal) {
		
		return commentService.getCommentById(commentId).map(comment -> {
			if(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), principal.getName())) {			
				comment.setContent(commentDto.getContent());
				return ResponseEntity.ok().build();
			} 
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		}).orElse(ResponseEntity.notFound().build());
	}
	
	@Transactional
	@DeleteMapping("comments/{commentId}")
	public ResponseEntity<Object> deleteCommentById(@PathVariable Long commentId, Principal principal) {
		return commentService.getCommentById(commentId)
				.map(comment -> {
					if(userSecurity.isOwnerOrAdmin(comment.getUser().getUsername(), principal.getName())) {
						commentService.deleteById(commentId);
						return ResponseEntity.noContent().build();
					} else {
		                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		            }
				}).orElse(ResponseEntity.notFound().build());					
	}		

	private UserEntity toUser(Principal principal) {
		return authService
				.findByUsername(principal.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found."));
	}

	private Comment toCreateComment(CommentDto commentDto, Article article, UserEntity user) {
		Comment comment = new Comment();
		comment.setContent(commentDto.getContent());
		comment.setUser(user);
		comment.setArticle(article);
		commentService.addComment(comment);
		return comment;
	}

}
