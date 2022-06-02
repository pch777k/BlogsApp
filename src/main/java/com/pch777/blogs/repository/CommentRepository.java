package com.pch777.blogs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pch777.blogs.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

	List<Comment> findCommmentsByArticleId(Long articleId);
	
	Page<Comment> findAllCommentsByArticleId(Pageable pageable, Long articleId);

}
