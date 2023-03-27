package com.pch777.blogs.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.exception.ForbiddenException;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.ArticleRepository;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.security.UserSecurity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	private final ImageFileService imageFileService;
	private final CategoryService categoryService;
	private final TagService tagService;
	private final UserSecurity userSecurity;
	
	
	public List<Article> getAllArticles() {
		return articleRepository.findAll();		
	}
	
	public Optional<Article> getArticleById(Long id) {
		return articleRepository.findById(id);
	}
	
	public Article addArticle(ArticleDto articleDto) throws ResourceNotFoundException {
		
		Article article = new Article();
		
		Category category = categoryService
				.findByName(articleDto.getCategoryName())
				.orElseThrow(() -> new ResourceNotFoundException("Category with name " + articleDto.getCategoryName() + " not found"));

		article.setCategory(category);
		
		Set<Tag> tags = tagService.fetchTagsByNames(articleDto.getTagsDto());
		article.setTags(tags);

		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());
		article.setSummary(articleDto.getSummary());
		article.setCreatedAt(LocalDateTime.now());
		return articleRepository.save(article);
	}
	
	public Article createArticle(Long blogId, ArticleDto articleDto, Principal principal) throws ResourceNotFoundException {
		Blog blog = blogRepository.findById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));
		if (!userSecurity.isOwnerOrAdmin(blog.getUser().getUsername(), principal.getName())) {
			//return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			throw new ForbiddenException("Access denied");
		}
		UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found"));
		
		Article article = new Article();
		
		Category category = categoryService
				.findByName(articleDto.getCategoryName())
				.orElseThrow(() -> new ResourceNotFoundException("Category with name " + articleDto.getCategoryName() + " not found"));

		article.setCategory(category);
		
		Set<Tag> tags = tagService.fetchTagsByNames(articleDto.getTagsDto());
		article.setTags(tags);

		ImageFile imageFile = new ImageFile();
		imageFileService.saveImageFile(imageFile);
		
		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());
		article.setSummary(articleDto.getSummary());
		article.setCreatedAt(LocalDateTime.now());
		
		
		article.setImage(imageFile);
		article.setBlog(blog);
		article.setUser(user);
		
		return articleRepository.save(article);
	}
	
	public void updateArticle(Long articleId, ArticleDto articleDto) throws ResourceNotFoundException {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));
		
		Category category = categoryService
				.findByName(articleDto.getCategoryName())
				.orElseThrow(() -> new ResourceNotFoundException("Category with name " + articleDto.getCategoryName() + " not found"));

		Set<Tag> tags = tagService.fetchTagsByNames(articleDto.getTagsDto());
		
		article.setCategory(category);
		article.setTags(tags);
		article.setSummary(articleDto.getSummary());
		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());	
	
	}
	
	public Article changeArticle(Long articleId, @Valid ArticleDto articleDto, Principal principal) throws ResourceNotFoundException {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));
		
		if (!userSecurity.isOwnerOrAdmin(article.getUser().getUsername(), principal.getName())) {
			throw new ForbiddenException();
		}
		
		Category category = categoryService
				.findByName(articleDto.getCategoryName())
				.orElseThrow(() -> new ResourceNotFoundException("Category with name " + articleDto.getCategoryName() + " not found"));

		Set<Tag> tags = tagService.fetchTagsByNames(articleDto.getTagsDto());
		
		article.setCategory(category);
		article.setTags(tags);
		article.setSummary(articleDto.getSummary());
		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());	
		
		return article;
	}

	public List<Article> getArticlesByBlogId(Long id) {
		return articleRepository.findArticlesByBlogId(id);
	}
	
	public List<Article> getLatestArticles(int numberOfArticles) {
		return articleRepository.findAll()
				.stream()
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed())
				.limit(numberOfArticles)
				.collect(Collectors.toList());
	}
	
	public List<Article> getMostCommentedArticles(int numberOfArticles) {
		return articleRepository.findAll().stream().sorted((o1, o2) -> {
			if (o1.getComments().size() == o2.getComments().size())
				return 0;
			else if (o1.getComments().size() < o2.getComments().size())
				return 1;
			else
				return -1;
		}).limit(numberOfArticles).collect(Collectors.toList());
	}
	
	public List<Article> getArticlesByCategorySortedByCreatedAt(String categoryName) {
		return articleRepository
				.findAll()
				.stream()
				.filter(a -> a.getCategory().getName().equalsIgnoreCase(categoryName))
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed())
				.collect(Collectors.toList());
	}

	public void deleteArticleById(Long articleId) {
		articleRepository.deleteById(articleId);
		
	}
	
	public Page<Article> getArticlesByCategory(Pageable pageable, String categoryName) {
		return articleRepository.findByCategory(pageable, categoryName);
	}

	public Page<Article> getArticlesByNameLike(Pageable pageable, String keyword) {
		return articleRepository.findArticlesByNameLike(pageable, keyword);
	}
	
	public Page<Article> getAllArticlesByBlogId(Pageable pageable, Long blogId, String keyword) {
		return articleRepository.findAllArticlesByBlogId(pageable, blogId, keyword.toLowerCase());
	}
	
	public ArticleDto articleToArticleDto(Article article) {
		Set<String> tagNames = article
				.getTags()
				.stream()
				.map(Tag::getName)
				.collect(Collectors.toSet());
		ArticleDto articleDto = new ArticleDto();
		articleDto.setTitle(article.getTitle());
		articleDto.setSummary(article.getSummary());
		articleDto.setContent(article.getContent());
		articleDto.setCategoryName(article.getCategory().getName());
		articleDto.setTagsDto(tagNames);
		return articleDto;
	}

	public Page<Article> getArticlesByBlogIdAndByCategory(Pageable pageable, Long blogId, String categoryName) {
		return articleRepository.findArticlesByBlogIdAndByCategory(pageable, blogId, categoryName);
	}

	

}
