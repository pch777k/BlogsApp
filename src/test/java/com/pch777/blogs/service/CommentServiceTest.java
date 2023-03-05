package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.repository.ArticleRepository;
import com.pch777.blogs.repository.CommentRepository;

@Import({ CommentService.class })
@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext
class CommentServiceTest {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private CommentService commentService;

	@Test
	void shouldGetAllComments() {
		// given
		Comment comment1 = new Comment();
		Comment comment2 = new Comment();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		// when
		List<Comment> comments = commentService.getAllComments();

		// then
		assertEquals(2, comments.size());
		assertTrue(comments.contains(comment1));
		assertTrue(comments.contains(comment2));

	}

	@Test
	void shouldGetListOfAllCommentsByArticleId() {
		// given
		Comment comment1 = new Comment();
		Comment comment2 = new Comment();
		commentRepository.save(comment1);
		commentRepository.save(comment2);
		Article article = new Article();
		article.setComments(Set.of(comment1, comment2));
		articleRepository.save(article);

		// when
		List<Comment> comments = commentService.getAllCommentsByArticleId(article.getId());

		// then
		assertEquals(2, comments.size());
	}

	@Test
	void shouldGetPageOfCommentsByArticleId() {
		// given
		Comment comment1 = new Comment();
		Comment comment2 = new Comment();
		commentRepository.save(comment1);
		commentRepository.save(comment2);
		Article article = new Article();
		article.setComments(Set.of(comment1, comment2));
		articleRepository.save(article);

		// when
		Page<Comment> comments = commentService.getCommentsByArticleId(PageRequest.of(0, 10),article.getId());
		// then
		assertEquals(2, comments.getTotalElements());

	}

	@Test
	void shouldGetCommentById() {
		// given
		Comment comment1 = new Comment();
		comment1.setContent("comment 1");
		Comment comment2 = new Comment();
		comment2.setContent("comment 2");
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		// when
		Optional<Comment> commentResult = commentService.getCommentById(comment2.getId());

		// then
		assertTrue(commentResult.isPresent());
		assertEquals(comment2.getContent(), commentResult.get().getContent());

	}

	@Test
	void shouldAddingComment() {
		// given
		Comment comment = new Comment();

		// when
		commentService.addComment(comment);
		List<Comment> comments = commentRepository.findAll();

		// then
		assertEquals(1, comments.size());
	}

	@Test
	void shouldDeletingCommentById() {
		// given
		Comment comment1 = new Comment();
		comment1.setContent("comment 1");
		Comment comment2 = new Comment();
		comment2.setContent("comment 2");
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		// when
		commentService.deleteById(comment2.getId());
		List<Comment> comments = commentRepository.findAll();

		// then
		assertTrue(comments.contains(comment1));
		assertFalse(comments.contains(comment2));
		assertEquals(1, comments.size());
	}

}
