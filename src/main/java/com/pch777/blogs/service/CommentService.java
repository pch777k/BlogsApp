package com.pch777.blogs.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Comment;
import com.pch777.blogs.repository.CommentRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommentService {

	private final CommentRepository commentRepository;
	
	public List<Comment> getAllComments() {
		return commentRepository.findAll();
	}

	public List<Comment> getAllCommentsByArticleId(Long articleId) {
		return commentRepository.findCommmentsByArticleId(articleId);
	}
	
	public Page<Comment> getCommentsByArticleId(Pageable pageable, Long articleId) {
		return commentRepository.findAllCommentsByArticleId(pageable, articleId);
	}
	
	public Optional<Comment> getCommentById(Long id) {
		return commentRepository.findById(id);
	}

	public Comment addComment(@Valid Comment comment) {	
		return commentRepository.save(comment);
	}

	public void deleteById(Long id) {
		commentRepository.deleteById(id);
		
	}
}
