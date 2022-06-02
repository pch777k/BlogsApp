package com.pch777.blogs.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.repository.ArticleRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ArticleService {

	private final ArticleRepository articleRepository;
	
	public List<Article> getAllArticles() {
		return articleRepository.findAll();		
	}
	
	public Optional<Article> getArticleById(Long id) {
		return articleRepository.findById(id);
	}
	
	public Article addArticle(ArticleDto articleDto) {
		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		Article article = new Article();
		
		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());
		article.setSummary(articleDto.getSummary());
//		article.setCategory(articleDto.getCategory());
//		article.setUsername(authentication.getName());
		return articleRepository.save(article);
	}
	
	public void deleteArticle(Long id) {
		articleRepository.deleteById(id);
	}

	public List<Article> getArticlesByBlogId(Long id) {
		return articleRepository.findArticlesByBlogId(id);
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
		return articleRepository.findAllArticlesByBlogId(pageable, blogId, keyword);
	}
	
	public ArticleDto articleToArticleDto(Article article) {
		Set<String> tagNames = article
				.getTags()
				.stream()
				.map(t -> t.getName())
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
		// TODO Auto-generated method stub
		return articleRepository.findArticlesByBlogIdAndByCategory(pageable, blogId, categoryName);
	}

//	public Page<Article> getArticlesByBlogIdAndByTag(Pageable pageable, Long blogId, String tagName) {
		// TODO Auto-generated method stub
//		return articleRepository.findArticlesByBlogIdAndByTag(pageable, blogId, tagName);
//	}


}
