package com.pch777.blogs.rest.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RequestMapping("/api")
@RestController
public class ArticleRestController {

	private final ArticleService articleService;
	private final UserSecurity userSecurity;
	private final ImageFileService imageFileService;

	@GetMapping("/articles")
	public ResponseEntity<List<Article>> getAllArticles() {
		List<Article> articles = articleService.getAllArticles();
		return ResponseEntity.ok(articles);
	}
	
	@GetMapping("/articles/{articleId}")
	public ResponseEntity<Article> getArticleById(@PathVariable Long articleId) {
		return articleService.getArticleById(articleId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/{blogId}/articles")
	public List<Article> getArticlesByBlogId(@PathVariable Long blogId) {
		return articleService.getArticlesByBlogId(blogId);
	}

	@Transactional
	@PostMapping("/{blogId}/articles")
	public ResponseEntity<Article> addArticle(@PathVariable Long blogId, @Valid @RequestBody ArticleDto articleDto,
			Principal principal) throws ResourceNotFoundException {
		
		Article article = articleService.createArticle(blogId, articleDto, principal);

		return new ResponseEntity<>(article, HttpStatus.OK);
	}
	
	@Transactional
	@PutMapping("/articles/{articleId}")
	public ResponseEntity<Article> changeArticle(@PathVariable Long articleId, @Valid @RequestBody ArticleDto articleDto,
			Principal principal) throws ResourceNotFoundException {

		Article articleChanged = articleService.changeArticle(articleId, articleDto, principal);
		
		return ResponseEntity.ok(articleChanged);
	}


	@DeleteMapping("/articles/{articleId}")
	public void deleteArticle(@PathVariable Long articleId) {
		articleService.deleteArticleById(articleId);

	}
	
	@Transactional
	@PostMapping(value = "/articles/{articleId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> addPhotoToBlog(@PathVariable Long articleId, 
			 @RequestParam(value = "file") MultipartFile multipartFile, Principal principal) {

		return articleService.getArticleById(articleId)
				.map(article -> {
                    if (userSecurity.isOwnerOrAdmin(article .getUser().getUsername(), principal.getName())) {
                    	if(!multipartFile.isEmpty()) {
							try {
								ImageFile imageFile = imageFileService.multipartToImageFile(multipartFile);
								article.setImage(imageFile);
							} catch (IOException e) {
								log.error(e.getMessage());
							}	  		
                    	}                  	
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());
	}

	
}
