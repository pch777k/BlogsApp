package com.pch777.blogs.controller;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.pch777.blogs.dto.CommentDto;
import com.pch777.blogs.exception.ForbiddenException;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.CommentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class CommentController {

	private final CommentService commentService;
	private final ArticleService articleService;
	private final AuthService authService;
	private final UserSecurity userSecurity;

	@PostMapping("/articles/{articleId}/comments/add")
	@Transactional
	public String addComment(@PathVariable Long articleId, @Valid @ModelAttribute("commentDto") CommentDto commentDto,
			BindingResult bindingResult) throws ResourceNotFoundException {

		if (bindingResult.hasErrors()) {
			return "article";
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		UserEntity user = authService.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

		Article article = articleService.getArticleById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));

		Comment comment = new Comment();
		comment.setContent(commentDto.getContent());
		comment.setUser(user);
		comment.setArticle(article);
		comment.setCreatedAt(LocalDateTime.now());
		commentService.addComment(comment);

		return "redirect:/articles/" + articleId;
	}

	@GetMapping("/comments/{commentId}/update")
	public String showUpdateBlogForm(@PathVariable Long commentId, Model model) throws ResourceNotFoundException {

		Comment comment = commentService.getCommentById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment with id " + commentId + " not found"));

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserUsername = auth.getName();
		String commentOwnerUsername = comment.getUser().getUsername();
		
		if(userSecurity.isOwner(commentOwnerUsername, loggedUserUsername)) {
			CommentDto commentDto = new CommentDto();
			commentDto.setContent(comment.getContent());

			model.addAttribute("articleId", comment.getArticle().getId());
			model.addAttribute("commentDto", commentDto);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
		}
		
		return "comment-update-form";
	}

	@PostMapping("/comments/{commentId}/update")
	@Transactional
	public String updateComment(@PathVariable Long commentId, @Valid @ModelAttribute("commentDto") CommentDto commentDto, 
			BindingResult bindingResult, Model model) throws ResourceNotFoundException {
		
		Comment comment = commentService.getCommentById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment with id " + commentId + " not found"));

		if (bindingResult.hasErrors()) {
			model.addAttribute("articleId", comment.getArticle().getId());
			return "comment-update-form";
		}
		
		comment.setContent(commentDto.getContent());
		
		return "redirect:/articles/" + comment.getArticle().getId();
	}

	@GetMapping("/comments/{commentId}/delete")
	public String deleteCommentById(@PathVariable Long commentId) throws ResourceNotFoundException {
		Comment comment = commentService.getCommentById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment with id " + commentId + " not found"));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserUsername = auth.getName();
		String commentOwnerUsername = comment.getUser().getUsername();
		
		if (userSecurity.isOwner(commentOwnerUsername, loggedUserUsername)) {
			commentService.deleteById(commentId);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
		}
		
		return "redirect:/articles/" + comment.getArticle().getId();
	}
}
