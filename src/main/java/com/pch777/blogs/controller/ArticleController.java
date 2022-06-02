package com.pch777.blogs.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
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
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.TagRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.AuthService;
import com.pch777.blogs.service.CategoryService;
import com.pch777.blogs.service.CommentService;
import com.pch777.blogs.service.ImageFileService;
import com.pch777.blogs.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ArticleController {
	
	private final ArticleService articleService;
	private final ImageFileService imageFileService;
	private final AuthService authService;
	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	private final CategoryService categoryService;
	private final TagRepository tagRepository;
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
	
		List<Blog> blogs = blogRepository.findAll();
			
		List<Tag> tags = articleService
			.getArticlesByBlogId(blog.getId())
			.stream()
			.map(a -> a.getTags())
			.flatMap(t -> t.stream())
			.distinct()
			.collect(Collectors.toList());
		
		List<Category> categories = categoryService
				.findAllCategories()
				.stream()
				.sorted(Comparator.comparing(Category::getName))
				.collect(Collectors.toList());
		
		
		
		List<Category> blogCategories = articleService
				.getArticlesByBlogId(blog.getId())
				.stream()
				.map(a -> a.getCategory())
				.distinct()
				.collect(Collectors.toList());
		
		
		Sort sort = Sort.by("createdAt").ascending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Comment> pageComments = commentService
				.getCommentsByArticleId(pageable, articleId);
		

		List<DateDto> monthAndYear = listOfMonthsOfYear(LocalDateTime.now());

		model.addAttribute("monthAndYear", monthAndYear);
	
		boolean hasBlog = isUserHasBlog(username);
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		//model.addAttribute("category", category);
//		model.addAttribute("commentDto", new CommentDto());
//		model.addAttribute("currentUser", userService.findUserByEmail(email));
//		model.addAttribute("totalComments", pageCommentsByArticleId.getTotalElements());
		model.addAttribute("pageComments", pageComments);
//		model.addAttribute("pageTotalComments", pageCommentsByArticleId.getSize());
//		model.addAttribute("pageSize", pageSize);
//		model.addAttribute("currentPage", page);
//		model.addAttribute("currentSize", pageable.getPageSize());
//		model.addAttribute("totalPages", pageCommentsByArticleId.getTotalPages());
		model.addAttribute("article", article);
		model.addAttribute("articleTags", article.getTags());
		model.addAttribute("blog", blog);
		model.addAttribute("blogs", blogs);
		model.addAttribute("tags", tags);
		model.addAttribute("blogCategories", blogCategories);
		model.addAttribute("categories", categories);
//		model.addAttribute("comments", comments);
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
		
		List<String> tags = tagRepository
				.findAll()
				.stream()
				.map(t -> t.getName())
				.collect(Collectors.toList());
		
		List<String> categories = categoryService
				.findAllCategories()
				.stream()
				.map(t -> t.getName())
				.collect(Collectors.toList());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean hasBlog = isUserHasBlog(username);
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("categories", categories);
		model.addAttribute("tags", tags);
		model.addAttribute("articleDto", new ArticleDto());
		model.addAttribute("categoryDto", new CategoryDto());
		model.addAttribute("tagDto", new TagDto());
		
		return "article-form";
	}
	
	private boolean isUserHasBlog(String username) {
		return blogRepository.findAll().stream().anyMatch(b -> b.getUser().getUsername().equals(username));
	}
	
	@Transactional
	@PostMapping("/articles/add")
	public String addArticle(//@PathVariable Long blogId, 
			@Valid @ModelAttribute("articleDto") ArticleDto articleDto,   
			BindingResult bindingResult, Model model,
			Principal principal) throws ResourceNotFoundException {

		if (bindingResult.hasErrors()) {
			List<String> tags = tagRepository
					.findAll()
					.stream()
					.map(t -> t.getName())
					.collect(Collectors.toList());
			
			List<String> categories = categoryService
					.findAllCategories()
					.stream()
					.map(t -> t.getName())
					.collect(Collectors.toList());
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			
			String username = auth.getName();
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = isUserHasBlog(username);
			boolean createButton = true;
			model.addAttribute("createButton", createButton);
			model.addAttribute("loggedUser", username);
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("categories", categories);
			model.addAttribute("tags", tags);
			model.addAttribute("categoryDto", new CategoryDto());
			model.addAttribute("tagDto", new TagDto());
		//	model.addAttribute("shops", shops);
		//	model.addAttribute("currentUser", userService.findUserByEmail(email));
			return "article-form";
		}

		UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found"));
		
		Blog blog = blogRepository
				.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with username " + principal.getName() + " not found"));

	//	Optional<Category> categoryOpt = categoryRepository.findByName(articleDto.getCategoryName());

		// articleDto.getTags().stream()
	//	System.out.println("title dto: " + articleDto.getTitle());
		Article article = articleService.addArticle(articleDto);
	
//		if (!categoryOpt.isPresent()) {
//			Category newCategory = new Category();
//			newCategory.setName(articleDto.getCategoryName());
//			categoryRepository.save(newCategory);
//			article.setCategory(newCategory);
//		} else {
//			article.setCategory(categoryOpt.get());
//		} 
		Category category = categoryService
				.findByName(articleDto.getCategoryName())
				.orElseThrow(() -> new ResourceNotFoundException("Category with name " + articleDto.getCategoryName() + " not found"));
	//	 article.setTags(articleDto.getTagsDto());
		article.setCategory(category);
		Set<Tag> tags = fetchTagsByNames(articleDto.getTagsDto());
		article.setTags(tags);
		// System.out.println("tags size: " + tags.size());
		// updateArticle(article, tags);
		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());
		article.setSummary(articleDto.getSummary());
		article.setCreatedAt(LocalDateTime.now());
		//article.setCategory(null)
		article.setBlog(blog);
		article.setUser(user);
		
		// articleDto.getTags().forEach(t -> t.addArticle(article));
		// article.setTags(articleDto.getTags());

		return "redirect:/articles/" + article.getId() + "/image/add";
	}
	
	@GetMapping("/articles/{articleId}/update")
	public String showUpdateArticleForm(@PathVariable Long articleId, Model model) throws ResourceNotFoundException {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String username = auth.getName();
		
//		UserEntity user = userRepository.findByUsername(username).orElseThrow(
//				() -> new UsernameNotFoundException("User with username " + username + " not found"));
		
		Article article = articleService
				.getArticleById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));
		
		ArticleDto articleDto = articleService.articleToArticleDto(article);
			
		List<String> tags = tagRepository
				.findAll()
				.stream()
				.map(t -> t.getName())
				.collect(Collectors.toList());
		
		List<String> categories = categoryService
				.findAllCategories()
				.stream()
				.map(t -> t.getName())
				.collect(Collectors.toList());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		boolean hasBlog = isUserHasBlog(username);
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("articleId", articleId);
		model.addAttribute("categories", categories);
		model.addAttribute("tags", tags);
		model.addAttribute("articleDto", articleDto);
		
		return "article-update-form";
	}
	
	@Transactional
	@PostMapping("/articles/{articleId}/update")
	public String updateArticle(@PathVariable Long articleId, 
			@Valid @ModelAttribute("articleDto") ArticleDto articleDto,   
			BindingResult bindingResult, Model model,
			Principal principal) throws ResourceNotFoundException {

		if (bindingResult.hasErrors()) {
			List<String> tags = tagRepository
					.findAll()
					.stream()
					.map(t -> t.getName())
					.collect(Collectors.toList());
			
			List<String> categories = categoryService
					.findAllCategories()
					.stream()
					.map(t -> t.getName())
					.collect(Collectors.toList());
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			boolean hasBlog = isUserHasBlog(username);
			
			model.addAttribute("loggedUser", username);
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("articleId", articleId);
			model.addAttribute("categories", categories);
			model.addAttribute("tags", tags);

			return "article-update-form";
		}
		
		Article article = articleService.getArticleById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + articleId + " not found"));;
		
		Category category = categoryService
				.findByName(articleDto.getCategoryName())
				.orElseThrow(() -> new ResourceNotFoundException("Category with name " + articleDto.getCategoryName() + " not found"));

		Set<Tag> tags = fetchTagsByNames(articleDto.getTagsDto());
		
		article.setCategory(category);
		article.setTags(tags);
		article.setSummary(articleDto.getSummary());
		article.setTitle(articleDto.getTitle());
		article.setContent(articleDto.getContent());

		return "redirect:/articles/" + article.getId();
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
		 
		return "redirect:/blogs/" + article.getBlog().getId();
	}

	private Set<Tag> fetchTagsByNames(Set<String> names) {
		return names.stream().map(name -> {
			tagService.addTag(name);
			return tagRepository.findTagByName(name).orElseThrow();
		}).collect(Collectors.toSet());
	}
	
	@GetMapping("/articles/{id}/image/add")
	public String showImageForm(@PathVariable Long id, Model model) throws ResourceNotFoundException {
		Article article = articleService.getArticleById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		boolean hasBlog = isUserHasBlog(username);
		boolean createButton = false;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("article", article);
		// model.addAttribute("imageFileDto", new ImageFileDto());

		return "image-article-form";
	}

	@Transactional
	@PostMapping("/articles/{id}/image/add")
	public String addImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException, IOException {
		Article article = articleService.getArticleById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
		ImageFile imageFile = ImageFile.builder().file(file.getBytes()).filename(file.getOriginalFilename())
				.contentType(file.getContentType()).createdAt(LocalDate.now()).fileLength(file.getSize()).build();
		imageFileService.saveImageFile(imageFile);
		article.setImage(imageFile);

		// model.addAttribute("blog", blog);
		// model.addAttribute("imageFileDto", new ImageFileDto());

		return "redirect:/blogs/" + article.getBlog().getId();
	}

	@GetMapping("/articles/{id}/image")
	public void showImage(@PathVariable Long id, HttpServletResponse response)
			throws ServletException, IOException, ResourceNotFoundException {

		Article article = articleService.getArticleById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
		response.getOutputStream().write(article.getImage().getFile());
		response.getOutputStream().close();
	}
}
