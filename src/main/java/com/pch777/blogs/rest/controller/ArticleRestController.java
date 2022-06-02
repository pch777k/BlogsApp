package com.pch777.blogs.rest.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.CategoryRepository;
import com.pch777.blogs.repository.TagRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.TagService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("api/blogs")
@RestController
public class ArticleRestController {

	private final ArticleService articleService;
	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final TagService tagService;

	@GetMapping
	public ResponseEntity<List<Article>> getAllArticles() {
		List<Article> articles = articleService.getAllArticles();
		return ResponseEntity.ok(articles);
	}
	
	@GetMapping("/{blogId}/articles")
	public List<Article> getArticlesByBlogId(@PathVariable Long blogId) {
		return articleService.getArticlesByBlogId(blogId);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@Transactional
	@PostMapping("/{blogId}/articles")
	public ResponseEntity<Article> addArticle(@PathVariable Long blogId, @Valid @RequestBody ArticleDto articleDto,
			Principal principal) throws ResourceNotFoundException {

		Blog blog = blogRepository.findById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));

		UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found"));

		Optional<Category> categoryOpt = categoryRepository.findByName(articleDto.getCategoryName());

		// articleDto.getTags().stream()

		Article article = articleService.addArticle(articleDto);

		if (!categoryOpt.isPresent()) {
			Category newCategory = new Category();
			newCategory.setName(articleDto.getCategoryName());
			categoryRepository.save(newCategory);
			article.setCategory(newCategory);
		} else {
			article.setCategory(categoryOpt.get());
		}

		// article.setTags(articleDto.getTags());

		Set<Tag> tags = fetchTagsByNames(articleDto.getTagsDto());
		article.setTags(tags);
		// System.out.println("tags size: " + tags.size());
		// updateArticle(article, tags);
		article.setBlog(blog);
		article.setUser(user);
		
		// articleDto.getTags().forEach(t -> t.addArticle(article));
		// article.setTags(articleDto.getTags());
		return ResponseEntity.ok(article);
	}

	private Set<Tag> fetchTagsByNames(Set<String> names) {
		return names.stream().map(name -> {
			tagService.addTag(name);
			return tagRepository.findTagByName(name).orElseThrow();
		}).collect(Collectors.toSet());
	}

/*	private void updateArticle(Article article, Set<Tag> tags) {
		article.removeTags();
		tags.forEach(article::addTag);
	} */

	@DeleteMapping("/articles/{articleId}")
	public void deleteArticle(@PathVariable Long articleId) {
		articleService.deleteArticle(articleId);

	}
}
