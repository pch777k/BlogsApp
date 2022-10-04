package com.pch777.blogs.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pch777.blogs.dto.CategoryDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.generator.ArticleValuesProperties;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.BlogService;
import com.pch777.blogs.service.CategoryService;
import com.pch777.blogs.service.CommentService;
import com.pch777.blogs.service.TagService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@PropertySource("classpath:values.properties")
public class CategoryController {

	private static final String CATEGORY_FORM = "category-form";
	private static final String HAS_BLOG = "hasBlog";
	private final CategoryService categoryService;
	private final ArticleService articleService;
	private final TagService tagService;
	private final CommentService commentService;
	private final BlogService blogService;
	private final AuthService authService;
	private final ArticleValuesProperties articleValuesProperties;

	@GetMapping("/categories/add")
	public String showAddTagForm(Model model) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean hasBlog = authService.isUserHasBlog(username);
		
		
		model.addAttribute(HAS_BLOG, hasBlog);
		model.addAttribute("categoryDto", new CategoryDto());

		return CATEGORY_FORM;
	}

	@PostMapping("/categories/add")
	public String addCategory(@Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
			BindingResult bindingResult, Model model) throws ResourceNotFoundException {
		
		if (bindingResult.hasErrors()) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = authService.isUserHasBlog(username);
			
			model.addAttribute(HAS_BLOG, hasBlog);
			return CATEGORY_FORM;
		}
		if (categoryService.categoryExists(categoryDto.getName())) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = authService.isUserHasBlog(username);
			
			model.addAttribute("exist", true);
			model.addAttribute(HAS_BLOG, hasBlog);
			return CATEGORY_FORM;
		}
		
		categoryService.addCategory(categoryDto.getName());
		return "redirect:/";
	}
	
	@GetMapping("categories/{categoryName}")
	public String getArticlesByCategoryName(@PathVariable String categoryName, 
			@RequestParam(defaultValue = "") String keyword, Model model)
			throws ResourceNotFoundException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		
		List<Article> articlesByCategory = articleService
				.getArticlesByCategorySortedByCreatedAt(categoryName)
				.stream()
				.filter(a -> a.getTitle()
						.toLowerCase()
						.contains(keyword.toLowerCase()))
				.collect(Collectors.toList());
		
		List<Article> latestFiveArticles = articleService.getLatestArticles(articleValuesProperties.getNumberOfLatestArticles());
		
		List<Category> topCategories = categoryService.findTopCategories(articleValuesProperties.getNumberOfTopCategories());
		
		List<Category> categories = categoryService.findAllCategoriesSortedByName();
		
		List<Tag> topSixTags = tagService.findTopTags(articleValuesProperties.getNumberOfTopTags());
		
		List<Tag> tags = tagService.findAllTagsSorted();
		
		List<Blog> blogs = blogService.findAllBlogs();
		
		boolean hasBlog = authService.isUserHasBlog(username);
		int totalBlogs = blogs.size();
		int totalArticles = articleService.getAllArticles().size();
		int totalUsers = authService.getTotalUsers();
		int totalComments = commentService.getAllComments().size();		
		boolean createButton = true;
		
		model.addAttribute("createButton", createButton);
		model.addAttribute(HAS_BLOG, hasBlog);
		model.addAttribute("articles", articlesByCategory);
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("latestArticles", latestFiveArticles);
		model.addAttribute("categories", categories);
		model.addAttribute("topFourCategories", topCategories);
		model.addAttribute("topSixTags", topSixTags);
		model.addAttribute("tags", tags);
		model.addAttribute("blogs", blogs);	
		model.addAttribute("totalBlogs", totalBlogs);
		model.addAttribute("totalArticles", totalArticles);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComments", totalComments);

		return "articles";
	}

}
