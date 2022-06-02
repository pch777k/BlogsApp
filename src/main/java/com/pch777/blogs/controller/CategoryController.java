package com.pch777.blogs.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class CategoryController {

	private final CategoryService categoryService;
	private final ArticleService articleService;
	private final TagService tagService;
	private final CommentService commentService;
	private final UserEntityRepository userRepository;
	private final BlogRepository blogRepository;
	private final AuthService authService;
	

	@GetMapping("/categories/add")
	public String showAddTagForm(Model model) {
//	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	String email = auth.getName();

		// model.addAttribute("shops", shops);
		// model.addAttribute("currentUser", userService.findUserByEmail(email));
		// model.addAttribute("blogService", blogService);
		// model.addAttribute("blog", new Blog());
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
	public String getArticlesByCategoryName(Model model, 
			@PathVariable String categoryName,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "6") int pageSize)
			throws ResourceNotFoundException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);

//		Blog blog = blogRepository
//				.findById(blogId)
//				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));
		//List<Article> articles = articleService
		//		.getArticlesByBlogId(blogId);
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<Article> pageArticles = articleService.getArticlesByCategory(pageable, categoryName);
		
		List<Article> articlesByCategory = articleService
				.getAllArticles()
				.stream()
				.filter(a -> a.getCategory().getName().equalsIgnoreCase(categoryName))
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed())
				.collect(Collectors.toList());
		
		//List<Tag> tags = getTagsByBlogId(blogId);
		List<Article> latestFiveArticles = articleService
				.getAllArticles()
				.stream()
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed())
				.limit(5)
				.collect(Collectors.toList());
		
		List<Category> topCategories = categoryService
				.findAllCategories()
				.stream()
				.sorted((o1, o2) -> {
			            if(o1.getArticles().size() == o2.getArticles().size()) return 0;
			            else if(o1.getArticles().size() < o2.getArticles().size()) return 1;
			            else return -1;
						})
				.limit(4)
				.collect(Collectors.toList());
		
		List<Category> categories = categoryService.findAllCategories().stream()
				.sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList());
		
		List<Tag> tags = tagService
				.findAllTags()
				.stream()
				.sorted((o1, o2) -> {
			            if(o1.getArticles().size() == o2.getArticles().size()) return 0;
			            else if(o1.getArticles().size() < o2.getArticles().size()) return 1;
			            else return -1;
						})
				.limit(6)
				.collect(Collectors.toList());
		
		List<Blog> blogs = blogRepository.findAll();
		
		int totalBlogs = blogs.size();
		int totalArticles = articleService.getAllArticles().size();
		int totalUsers = userRepository.findAll().size();
		int totalComments = commentService.getAllComments().size();
		
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("articles", articlesByCategory);
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("pageArticles", pageArticles);
		model.addAttribute("latestArticles", latestFiveArticles);

		model.addAttribute("categories", categories);
		model.addAttribute("topCategories", topCategories);
		model.addAttribute("tags", tags);
		model.addAttribute("blogs", blogs);
		
		model.addAttribute("totalBlogs", totalBlogs);
		model.addAttribute("totalArticles", totalArticles);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComments", totalComments);

		return "articles";
	}
}
