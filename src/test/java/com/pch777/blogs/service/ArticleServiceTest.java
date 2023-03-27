package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.ArticleRepository;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.CategoryRepository;
import com.pch777.blogs.repository.UserEntityRepository;

@Import({ ArticleService.class, TagService.class, CategoryService.class })
@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext
class ArticleServiceTest {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private UserEntityRepository userRepository;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private TagService tagService;

	private Article givenArticle(String title) {
		Article article = new Article();
		article.setTitle(title);
		article.setCreatedAt(LocalDateTime.now());
		return articleRepository.save(article);
	}

	private Article givenArticleWithCategory(String title, Category category) {
		Article article = new Article();
		article.setTitle(title);
		article.setCreatedAt(LocalDateTime.now());
		article.setCategory(category);
		return articleRepository.save(article);
	}
	
	private Article givenArticle(String title, String summary, String content, Category category, Set<String> tags) {
		Article article = new Article();
		article.setTitle(title);
		article.setSummary(summary);
		article.setContent(content);
		article.setCreatedAt(LocalDateTime.now());
		article.setCategory(category);
		article.setTags(tagService.fetchTagsByNames(tags));
		return articleRepository.save(article);
	}
	
	private ArticleDto givenArticleDto(String title, String summary, String content, String categoryName, Set<String> tags) {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setTitle("title");
		articleDto.setSummary("summary");
		articleDto.setContent("content");
		articleDto.setCategoryName(categoryName);
		articleDto.setTagsDto(tags);
		return articleDto;
	}

	private UserEntity givenUser() {
		return userRepository.save(new UserEntity());
	}

	@Test
	void shouldGetAllArticles() {
		// given
		givenArticle("Title 1");
		givenArticle("Title 2");

		// when
		List<Article> articles = articleService.getAllArticles();

		// then
		assertEquals(2, articles.size());
		assertFalse(articles.isEmpty());
	}

	@Test
	void shouldGetArticleById() {
		// given
		givenArticle("Title 1");
		Article article2 = givenArticle("Title 2");

		// when
		Optional<Article> articleResult = articleService.getArticleById(article2.getId());

		// then
		assertTrue(articleResult.isPresent());
		assertEquals(article2.getTitle(), articleResult.get().getTitle());
	}

	@Test
	void shouldAddArticle() throws ResourceNotFoundException {
		// given
		Category category1 = new Category("sport");		
		categoryRepository.save(category1);
		
		ArticleDto articleDto = new ArticleDto();
		articleDto.setTitle("Title");
		articleDto.setSummary("Summary of aricle");
		articleDto.setContent("Content of article");
		articleDto.setCategoryName(category1.getName());
		articleDto.setTagsDto(Set.of("football", "league"));

		// when
		Article article = articleService.addArticle(articleDto);

		// then
		assertEquals(1, articleRepository.findAll().size());
		assertEquals(articleDto.getTitle(), article.getTitle());
		assertEquals(articleDto.getSummary(), article.getSummary());
		assertEquals(articleDto.getContent(), article.getContent());
	}

	@Test
	void shouldThrowsResourceNotFoundExceptionWhenAddArticleWithDifferentCategoryName()
			throws ResourceNotFoundException {
		// given

		ArticleDto articleDto = 
				givenArticleDto("Title", "Summary of aricle", "Content of article", "science", Set.of("football", "league"));

		// when
		// then
		assertThrows(ResourceNotFoundException.class, () -> articleService.addArticle(articleDto));
	}

	@Test
	void shouldUpdatedArticle() throws ResourceNotFoundException {
		// given
		Category category1 = new Category("sport");		
		categoryRepository.save(category1);
		
		Category category2= new Category("food");		
		categoryRepository.save(category2);
		
		Article article = givenArticle("Title", "Summary of aricle", "Content of article", category1, Set.of("football", "league"));

		ArticleDto articleDto = givenArticleDto("Updated title", "Updated summary of aricle", "Updated content of article", 
						category2.getName(), Set.of("friut", "vegetables", "water"));

		// when
		articleService.updateArticle(article.getId(), articleDto);

		// then
		assertEquals(1, articleRepository.findAll().size());
		assertEquals(articleDto.getTitle(), article.getTitle());
		assertEquals(articleDto.getSummary(), article.getSummary());
		assertEquals(articleDto.getContent(), article.getContent());
		assertEquals(category2.getName(), article.getCategory().getName());
	}
	
	@Test
	void shouldThrownResourceNotFoundExceptionWhenUpdatedArticleHasDifferentCategoryName() throws ResourceNotFoundException {
		// given
		Category category1 = new Category("sport");		
		categoryRepository.save(category1);
		
		Category category2= new Category("food");		
		categoryRepository.save(category2);
		
		givenArticle("Title", "Summary of aricle", "Content of article", category1, Set.of("football", "league"));

		ArticleDto articleDto = givenArticleDto("Updated title", "Updated summary of aricle", "Updated content of article", 
						"category", Set.of("friut", "vegetables", "water"));

		// when
		// then
		assertThrows(ResourceNotFoundException.class, () -> articleService.updateArticle(1L, articleDto));
	}
	
	@Test
	void shouldThrownResourceNotFoundExceptionWhenIdOfUpdatedArticleNotFound() throws ResourceNotFoundException {
		// given
		Category category1 = new Category("sport");		
		categoryRepository.save(category1);
		
		Category category2= new Category("food");		
		categoryRepository.save(category2);
		
		givenArticle("Title", "Summary of aricle", "Content of article", category1, Set.of("football", "league"));

		ArticleDto articleDto = givenArticleDto("Updated title", "Updated summary of aricle", "Updated content of article", 
						"category", Set.of("friut", "vegetables", "water"));

		// when
		// then
		assertThrows(ResourceNotFoundException.class, () -> articleService.updateArticle(10L, articleDto));
	}

	@Test
	void shouldDeleteArticleById() {
		// given
		Article article1 = givenArticle("Title 1");
		Article article2 = givenArticle("Title 2");

		// when
		articleService.deleteArticleById(article2.getId());
		List<Article> articles = articleService.getAllArticles();

		// then
		assertFalse(articles.isEmpty());
		assertEquals(1, articles.size());
		assertTrue(articles.contains(article1));
	}

	@Disabled
	@Test
	void shouldGetTwoLatestArticles() {
		// given
		Article article1 = givenArticle("Title 1");
		Article article2 = givenArticle("Title 2");
		Article article3 = givenArticle("Title 3");

		// when
		List<Article> latestArticles = articleService.getLatestArticles(2);

		// then
		assertTrue(latestArticles.contains(article3));
		assertTrue(latestArticles.containsAll(List.of(article2, article3)));
		assertFalse(latestArticles.contains(article1));
		assertEquals(latestArticles.get(0).getTitle(), article3.getTitle());
	}

	@Test
	void shouldGetTwoMostCommentedArticles() {
		// given
		Article article1 = givenArticle("Title 1");
		Article article2 = givenArticle("Title 2");
		Article article3 = givenArticle("Title 3");
		Comment comment1 = new Comment();
		Comment comment2 = new Comment();
		Comment comment3 = new Comment();
		Comment comment4 = new Comment();
		article2.setComments(Set.of(comment1, comment2, comment3));
		article3.setComments(Set.of(comment4));

		// when
		List<Article> mostCommentedArticles = articleService.getMostCommentedArticles(2);

		// then
		assertEquals(article2.getComments(), mostCommentedArticles.get(0).getComments());
		assertEquals(article3.getTitle(), mostCommentedArticles.get(1).getTitle());
		assertFalse(mostCommentedArticles.contains(article1));

	}

	@Disabled
	@Test
	void shouldGetTwoArticlesByCategorySortedByCreatedAt() {
		// given
		Category category1 = new Category("sport");
		Category category2 = new Category("food");
		categoryRepository.save(category1);
		categoryRepository.save(category2);

		Article article1 = givenArticleWithCategory("Title 1", category1);
		Article article2 = givenArticleWithCategory("Title 2", category2);
		Article article3 = givenArticleWithCategory("Title 3", category2);

		// when
		List<Article> articles = articleService.getArticlesByCategorySortedByCreatedAt(category2.getName());

		// then
		assertEquals(article3, articles.get(0));
		assertEquals(article2.getTitle(), articles.get(1).getTitle());
		assertFalse(articles.contains(article1));

	}

	@Test
	void shouldGetArticlesByCategory() {
		// given
		Category category1 = new Category("sport");
		Category category2 = new Category("food");
		categoryRepository.save(category1);
		categoryRepository.save(category2);

		Article article1 = givenArticleWithCategory("Title 1", category1);
		Article article2 = givenArticleWithCategory("Title 2", category2);
		Article article3 = givenArticleWithCategory("Title 3", category2);

		// when
		Page<Article> articles = articleService.getArticlesByCategory(PageRequest.of(0, 10), category2.getName());

		// then
		assertEquals(2, articles.getTotalElements());
		assertEquals(List.of(article2, article3), articles.getContent());
		assertFalse(articles.getContent().contains(article1));
	}

	@Test
	void shouldGetArticlesByNameLike() {
		// given
		Article article1 = givenArticle("Title 1");
		Article article2 = givenArticle("article 2");
		Article article3 = givenArticle("Title 3");

		// when
		Page<Article> articles = articleService.getArticlesByNameLike(PageRequest.of(0, 10), "tit");

		// then
		assertEquals(2, articles.getTotalElements());
		assertEquals(List.of(article1, article3), articles.getContent());
		assertFalse(articles.getContent().contains(article2));
	}

	@Test
	void testGetAllArticlesByBlogId() {
		// given
		UserEntity user = givenUser();
		Blog blog = new Blog("blog 1", "description 1");

		Article article1 = givenArticle("Title 1");
		Article article2 = givenArticle("article 2");
		Article article3 = givenArticle("Title 3");
		blog.setArticles(Set.of(article1, article3));
		blog.setUser(user);
		blogRepository.save(blog);
		// when
		List<Article> articles = articleService.getArticlesByBlogId(1L);

		// then
		assertEquals(2, articles.size());
		assertEquals(List.of(article1, article3), articles);
		assertFalse(articles.contains(article2));
	}

	@Test
	void shouldGetArticlesByBlogIdAndByCategory() {
		// given
		UserEntity user = givenUser();
		Blog blog = new Blog("blog 1", "description 1");

		Category category1 = new Category("sport");
		Category category2 = new Category("food");
		categoryRepository.save(category1);
		categoryRepository.save(category2);
		Article article1 = givenArticleWithCategory("Title 1", category1);
		Article article2 = givenArticleWithCategory("Title 2", category2);
		Article article3 = givenArticleWithCategory("Title 3", category2);
		blog.setArticles(Set.of(article1, article3));
		blog.setUser(user);
		blogRepository.save(blog);
		// when
		Page<Article> articles = articleService.getArticlesByBlogIdAndByCategory(PageRequest.of(0, 10), blog.getId(),
				category1.getName());

		// then
		System.out.println("total el: " +  articles.getTotalElements());
		assertEquals(1, articles.getTotalElements());
		assertEquals(List.of(article1), articles.getContent());
		assertFalse(articles.getContent().contains(article2));
		assertFalse(articles.getContent().contains(article3));
	}

}
