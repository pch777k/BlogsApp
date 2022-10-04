package com.pch777.blogs.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.dto.CategoryDto;
import com.pch777.blogs.dto.CommentDto;
import com.pch777.blogs.dto.DateDto;
import com.pch777.blogs.dto.TagDto;
import com.pch777.blogs.exception.ForbiddenException;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.BlogService;
import com.pch777.blogs.service.CategoryService;
import com.pch777.blogs.service.CommentService;
import com.pch777.blogs.service.ImageFileService;
import com.pch777.blogs.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ArticleController {
	
	private static final String ARTICLE_UPDATE_FORM = "article-update-form";
	private static final String ARTICLE_FORM = "article-form";
	private static final String REDIRECT_ARTICLES = "redirect:/articles/";
	private static final String REDIRECT_BLOGS = "redirect:/blogs/";
	private final ArticleService articleService;
	private final ImageFileService imageFileService;
	private final AuthService authService;
	private final BlogService blogService;
	private final CategoryService categoryService;
	private final TagService tagService;
	private final CommentService commentService;
	private final UserSecurity userSecurity;
	
	
	@GetMapping("/articles/{articleId}")
	public String getArticleById(Model model, 
			@PathVariable Long articleId,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int pageSize) 
					throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		
		Article article = articleService
				.getArticleById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));
		
		Blog blog = article.getBlog();
	
		List<Blog> blogs = blogService.findAllBlogs();
			
		List<Tag> tags = articleService
			.getArticlesByBlogId(blog.getId())
			.stream()
			.map(Article::getTags)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
		
		List<Category> categories = categoryService.findAllCategoriesSortedByName();
			
		List<Category> blogCategories = articleService
				.getArticlesByBlogId(blog.getId())
				.stream()
				.map(Article::getCategory)
				.distinct()
				.collect(Collectors.toList());
		
		
		Sort sort = Sort.by("createdAt").ascending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Comment> pageComments = commentService
				.getCommentsByArticleId(pageable, articleId);
		

		List<DateDto> monthAndYear = listOfMonthsOfYear(LocalDateTime.now());

		model.addAttribute("monthAndYear", monthAndYear);
	
		boolean hasBlog = authService.isUserHasBlog(username);
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("pageComments", pageComments);
		model.addAttribute("article", article);
		model.addAttribute("articleTags", article.getTags());
		model.addAttribute("blog", blog);
		model.addAttribute("blogs", blogs);
		model.addAttribute("tags", tags);
		model.addAttribute("blogCategories", blogCategories);
		model.addAttribute("categories", categories);
		model.addAttribute("commentDto", new CommentDto());
		
		return "article";
	}
	
	private List<DateDto> listOfMonthsOfYear(LocalDateTime date) {
		LocalDate localDate = date.toLocalDate();
		List<DateDto> monthAndYear = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			DateDto dateDto = new DateDto();
			dateDto.setMonth(localDate.minusMonths(i).getMonth().name().toLowerCase());
			dateDto.setYear(localDate.minusMonths(i).getYear());
			monthAndYear.add(dateDto);
		}
		return monthAndYear;
	}
	
	
	@GetMapping("/articles/add")
	public String showArticleForm(Model model) throws ResourceNotFoundException {
		
		List<String> tags = tagService.getAllTagsName();
		
		List<String> categories = categoryService.getAllCategoriesName();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean hasBlog = authService.isUserHasBlog(username);
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("categories", categories);
		model.addAttribute("tags", tags);
		model.addAttribute("articleDto", new ArticleDto());
		model.addAttribute("categoryDto", new CategoryDto());
		model.addAttribute("tagDto", new TagDto());
		
		return ARTICLE_FORM;
	}
	
	@Transactional
	@PostMapping("/articles/add")
	public String addArticle(@Valid @ModelAttribute("articleDto") ArticleDto articleDto,   
			BindingResult bindingResult, Model model, Principal principal) throws ResourceNotFoundException {

		if (bindingResult.hasErrors()) {
			List<String> tags = tagService.getAllTagsName();
			List<String> categories = categoryService.getAllCategoriesName();
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
			String username = auth.getName();
			
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = authService.isUserHasBlog(username);
			boolean createButton = true;
			
			model.addAttribute("createButton", createButton);
			model.addAttribute("loggedUser", username);
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("categories", categories);
			model.addAttribute("tags", tags);
			model.addAttribute("categoryDto", new CategoryDto());
			model.addAttribute("tagDto", new TagDto());

			return ARTICLE_FORM;
		}

		UserEntity user = authService
				.findByUsername(principal.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found"));
		
		Blog blog = blogService
				.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("User with username " + principal.getName() + " does not have a blog"));

		Article article = articleService.addArticle(articleDto);
		article.setBlog(blog);
		article.setUser(user);
		
		return REDIRECT_ARTICLES + article.getId() + "/image/add";
	}
	
	@GetMapping("/articles/{articleId}/update")
	public String showUpdateArticleForm(@PathVariable Long articleId, Model model) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserUsername = auth.getName();
		Article article = articleService
				.getArticleById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));
		
		String articleOwnerUsername = article.getUser().getUsername();
		
		if (userSecurity.isOwner(articleOwnerUsername, loggedUserUsername)) {
			ArticleDto articleDto = articleService.articleToArticleDto(article);
			
			List<String> tags = tagService.getAllTagsName();
			
			List<String> categories = categoryService.getAllCategoriesName();
			authService.ifNotAnonymousUserGetIdToModel(model, loggedUserUsername);
			boolean hasBlog = authService.isUserHasBlog(loggedUserUsername);
			boolean createButton = true;
			model.addAttribute("createButton", createButton);
			model.addAttribute("loggedUser", loggedUserUsername);
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("articleId", articleId);
			model.addAttribute("categories", categories);
			model.addAttribute("tags", tags);
			model.addAttribute("articleDto", articleDto);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
		}
			
		return ARTICLE_UPDATE_FORM;
	}
	
	@Transactional
	@PostMapping("/articles/{articleId}/update")
	public String updateArticle(@PathVariable Long articleId, 
			@Valid @ModelAttribute("articleDto") ArticleDto articleDto,   
			BindingResult bindingResult, Model model,
			Principal principal) throws ResourceNotFoundException {

		if (bindingResult.hasErrors()) {
			List<String> tags = tagService.getAllTagsName();
								
			List<String> categories = categoryService.getAllCategoriesName();
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = authService.isUserHasBlog(username);
			
			model.addAttribute("loggedUser", username);
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("articleId", articleId);
			model.addAttribute("categories", categories);
			model.addAttribute("tags", tags);

			return ARTICLE_UPDATE_FORM;
		}
		articleService.updateArticle(articleId, articleDto);

		return REDIRECT_ARTICLES + articleId;
	}
	
	@GetMapping("/articles/{articleId}/delete")
	@Transactional
	public String deleteArticleById(@PathVariable Long articleId) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserUsername = auth.getName();
		Article article = articleService
				.getArticleById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));
		
		String articleOwnerUsername = article.getUser().getUsername();
		if (userSecurity.isOwner(articleOwnerUsername, loggedUserUsername)) {
			articleService.deleteArticleById(articleId);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
		}
		 
		return REDIRECT_BLOGS + article.getBlog().getId();
	}
	
	@GetMapping("/articles/{id}/image/add")
	public String showImageForm(@PathVariable Long id, Model model) throws ResourceNotFoundException {
		Article article = articleService.getArticleById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		boolean hasBlog = authService.isUserHasBlog(username);
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean createButton = false;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("article", article);

		return "image-article-form";
	}

	@Transactional
	@PostMapping("/articles/{id}/image/add")
	public String addImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException, IOException {
		Article article = articleService.getArticleById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
		ImageFile imageFile = ImageFile
				.builder()
				.file(file.getBytes())
				.filename(file.getOriginalFilename())
				.contentType(file.getContentType()).createdAt(LocalDate.now())
				.fileLength(file.getSize()).build();
		imageFileService.saveImageFile(imageFile);
		article.setImage(imageFile);

		return REDIRECT_BLOGS + article.getBlog().getId();
	}

	@GetMapping("/articles/{id}/image")
	public void showImage(@PathVariable Long id, HttpServletResponse response)
			throws IOException, ResourceNotFoundException {

		Article article = articleService.getArticleById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
		response.getOutputStream().write(article.getImage().getFile());
		response.getOutputStream().close();
	}
}
