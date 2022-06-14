package com.pch777.blogs.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
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
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.CategoryService;
import com.pch777.blogs.service.CommentService;
import com.pch777.blogs.service.TagService;

@Controller
@PropertySource("classpath:values.properties")
public class CategoryController {

	private final CategoryService categoryService;
	private final ArticleService articleService;
	private final TagService tagService;
	private final CommentService commentService;
	private final UserEntityRepository userRepository;
	private final BlogRepository blogRepository;
	private final AuthService authService;
	private final int numberOfLatestArticles;
	private final int numberOfTopCategories;
	private final int numberOfTopTags;
	
	public CategoryController(CategoryService categoryService, 
			ArticleService articleService, 
			TagService tagService,
			CommentService commentService, 
			UserEntityRepository userRepository, 
			BlogRepository blogRepository,
			AuthService authService, 
			@Value("${numberOfLatestArticles}") int numberOfLatestArticles,
			@Value("${numberOfTopCategories}") int numberOfTopCategories,
			@Value("${numberOfTopTags}") int numberOfTopTags) {
		this.categoryService = categoryService;
		this.articleService = articleService;
		this.tagService = tagService;
		this.commentService = commentService;
		this.userRepository = userRepository;
		this.blogRepository = blogRepository;
		this.authService = authService;
		this.numberOfLatestArticles = numberOfLatestArticles;
		this.numberOfTopCategories = numberOfTopCategories;
		this.numberOfTopTags = numberOfTopTags;
	}
	

	@GetMapping("/categories/add")
	public String showAddTagForm(Model model) {
		model.addAttribute("categoryDto", new CategoryDto());

		return "category-form";
	}

	@PostMapping("/categories/add")
	public String addCategory(@Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
			BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {	
			return "category-form";
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
		
		List<Article> latestFiveArticles = articleService.getLatestArticles(numberOfLatestArticles);
		
		List<Category> topCategories = categoryService.findTopCategories(numberOfTopCategories);
		
		List<Category> categories = categoryService.findAllCategoriesSortedByName();
		
		List<Tag> tags = tagService.findTopTags(numberOfTopTags);
		
		List<Blog> blogs = blogRepository.findAll();
		
		int totalBlogs = blogs.size();
		int totalArticles = articleService.getAllArticles().size();
		int totalUsers = userRepository.findAll().size();
		int totalComments = commentService.getAllComments().size();		
		boolean createButton = true;
		
		model.addAttribute("createButton", createButton);
		model.addAttribute("articles", articlesByCategory);
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("latestArticles", latestFiveArticles);
		model.addAttribute("categories", categories);
		model.addAttribute("topFourCategories", topCategories);
		model.addAttribute("tags", tags);
		model.addAttribute("blogs", blogs);	
		model.addAttribute("totalBlogs", totalBlogs);
		model.addAttribute("totalArticles", totalArticles);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComments", totalComments);

		return "articles";
	}

}
