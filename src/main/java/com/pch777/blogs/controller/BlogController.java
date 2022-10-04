package com.pch777.blogs.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.dto.DateDto;
import com.pch777.blogs.exception.ForbiddenException;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.generator.ArticleValuesProperties;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
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

@Controller
@RequiredArgsConstructor
public class BlogController {

	private static final String BLOG_FORM = "blog-form";
	private static final String BLOG_UPDATE_FORM = "blog-update-form";
	private final BlogService blogService;
	private final ImageFileService imageFileService;
	private final ArticleService articleService;
	private final TagService tagService;
	private final CategoryService categoryService;
	private final CommentService commentService;
	private final UserSecurity userSecurity;
	private final AuthService authService;
	private final ArticleValuesProperties articleValuesProperties;

	@GetMapping({ "/", "/index" })
	public String listBlogs(Model model, @RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int pageArticle,
			@RequestParam(defaultValue = "6") int pageSize) throws ResourceNotFoundException {

		Pageable pageable = PageRequest.of(page - 1, pageSize);
		
		Page<Blog> pageBlogs = blogService.getAllBlogsByNameLike(pageable, keyword);

		List<Article> latestFiveArticles = articleService.getLatestArticles(articleValuesProperties.getNumberOfLatestArticles());

		List<Article> mostCommentedArticles = articleService.getMostCommentedArticles(articleValuesProperties.getNumberOfMostCommentedArticles());

		List<Category> topFourCategories = categoryService.findTopCategories(articleValuesProperties.getNumberOfTopCategories());

		List<Category> categories = categoryService.findAllCategoriesSortedByName();

		List<Tag> topSixTags = tagService.findTopTags(articleValuesProperties.getNumberOfTopTags());
		
		List<Tag> tags = tagService.findAllTagsSorted();

		List<Blog> blogs = blogService.findAllBlogs();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		authService.ifNotAnonymousUserGetIdToModel(model, username);

		boolean hasBlog = authService.isUserHasBlog(username);
		
		long totalBlogs = pageBlogs.getTotalElements();
		int totalArticles = articleService.getAllArticles().size();
		int totalUsers = authService.getTotalUsers();
		int totalComments = commentService.getAllComments().size();

		boolean searchKeyword = keyword.length() > 0;

		if (searchKeyword) {
			Pageable pageableArticlesFound = PageRequest.of(pageArticle - 1, pageSize);
			Page<Article> pageArticlesFound = articleService.getArticlesByNameLike(pageableArticlesFound, keyword);
			model.addAttribute("articlesFoundIsEmpty", pageArticlesFound.isEmpty());
			model.addAttribute("pageArticlesFound", pageArticlesFound);
		}
		boolean createButton = true;
		
		model.addAttribute("createButton", createButton);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("blogsFoundIsEmpty", pageBlogs.isEmpty());
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("mostCommentedArticles", mostCommentedArticles);
		model.addAttribute("keyword", keyword);
		model.addAttribute("latestArticles", latestFiveArticles);
		model.addAttribute("pageBlogs", pageBlogs);
		model.addAttribute("blogs", blogs);
		model.addAttribute("blogService", blogService);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageBlogs.getTotalPages());
		model.addAttribute("totalBlogs", totalBlogs);
		model.addAttribute("categories", categories);
		model.addAttribute("topFourCategories", topFourCategories);
		model.addAttribute("topSixTags", topSixTags);
		model.addAttribute("tags", tags);		
		model.addAttribute("totalArticles", totalArticles);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComments", totalComments);

		return "index";
	}

	@GetMapping("/blogs/{blogId}")
	public String getBlogById(Model model, @PathVariable Long blogId, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "6") int pageSize)
			throws ResourceNotFoundException {

		Blog blog = blogService
				.getBlogById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));

		Sort sort = Sort.by("createdAt").descending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Article> pageArticles = articleService.getAllArticlesByBlogId(pageable, blogId, keyword);

		List<Tag> blogTags = getTagsByBlogId(blogId);
		
		List<Tag> tags = tagService.findAllTagsSorted();

		List<Category> blogCategories = getCategoriesByBlogId(blogId);

		List<Category> categories = categoryService.findAllCategoriesSortedByName();

		List<DateDto> monthAndYear = listOfMonthsOfYear(LocalDateTime.now());

		List<Blog> blogs = blogService.findAllBlogs();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		authService.ifNotAnonymousUserGetIdToModel(model, username);

		boolean hasBlog = authService.isUserHasBlog(username);

		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("keyword", keyword);
		model.addAttribute("monthAndYear", monthAndYear);
		model.addAttribute("pageArticles", pageArticles);
		model.addAttribute("blog", blog);
		model.addAttribute("blogs", blogs);
		model.addAttribute("blogTags", blogTags);
		model.addAttribute("tags", tags);	
		model.addAttribute("categories", categories);
		model.addAttribute("blogCategories", blogCategories);

		return "blog";
	}

	@GetMapping("/blogs/{blogId}/{month}/{year}")
	public String getbBlogByIdAndArtclesByMonthAndYear(Model model, @PathVariable Long blogId, @PathVariable String month,
			@PathVariable int year, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "6") int pageSize)
			throws ResourceNotFoundException {

		Blog blog = blogService.getBlogById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));

		List<Article> articlesByMonth = articleService.getArticlesByBlogId(blogId).stream()
				.filter(a -> a.getCreatedAt().getMonth().name().equalsIgnoreCase(month))
				.filter(a -> a.getCreatedAt().getYear() == year)
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed()).collect(Collectors.toList());

		List<Tag> blogTags = getTagsByBlogId(blogId);
		
		List<Tag> tags = tagService.findAllTagsSorted();

		List<Category> blogCategories = getCategoriesByBlogId(blogId);

		List<Category> categories = categoryService.findAllCategoriesSortedByName();

		List<DateDto> monthAndYear = listOfMonthsOfYear(LocalDateTime.now());

		List<Blog> blogs = blogService.findAllBlogs();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);

		boolean hasBlog = authService.isUserHasBlog(username);

		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("monthAndYear", monthAndYear);
		model.addAttribute("articles", articlesByMonth);
		model.addAttribute("blog", blog);
		model.addAttribute("blogs", blogs);
		model.addAttribute("blogTags", blogTags);
		model.addAttribute("tags", tags);
		model.addAttribute("categories", categories);
		model.addAttribute("blogCategories", blogCategories);

		return "blog-date";
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

	@GetMapping("/blogs/{blogId}/category/{categoryName}")
	public String getBlogByIdAndArtclesByCategoryName(Model model, @PathVariable Long blogId, @PathVariable String categoryName,
			@RequestParam(defaultValue = "") String keyword, 
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "6") int pageSize) throws ResourceNotFoundException {

		Blog blog = blogService.getBlogById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));
		List<Blog> blogs = blogService.findAllBlogs();

		Sort sort = Sort.by("createdAt").descending();
		Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
		Page<Article> pageArticlesByCategory = articleService
				.getArticlesByBlogIdAndByCategory(pageable, blogId, categoryName.toLowerCase());

		List<Tag> blogTags = getTagsByBlogId(blogId);
		
		List<Tag> tags = tagService.findAllTagsSorted();

		List<Category> blogCategories = getCategoriesByBlogId(blogId);

		List<Category> categories = categoryService.findAllCategoriesSortedByName();
		
		List<DateDto> monthAndYear = listOfMonthsOfYear(LocalDateTime.now());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);

		boolean hasBlog = authService.isUserHasBlog(username);

		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("currentPage", page);
		model.addAttribute("currentSize", pageable.getPageSize());
		model.addAttribute("totalPages", pageArticlesByCategory.getTotalPages());
		model.addAttribute("monthAndYear", monthAndYear);
		model.addAttribute("pageArticles", pageArticlesByCategory);
		model.addAttribute("blog", blog);
		model.addAttribute("blogs", blogs);
		model.addAttribute("blogTags", blogTags);
		model.addAttribute("tags", tags);
		model.addAttribute("categories", categories);
		model.addAttribute("blogCategories", blogCategories);

		return "blog-category";
	}

	@GetMapping("/blogs/{blogId}/tag/{tagName}")
	public String getBlogByIdAndArticlesByTagName(Model model, @PathVariable Long blogId, @PathVariable String tagName)
			throws ResourceNotFoundException {

		Blog blog = blogService.getBlogById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));

		List<Blog> blogs = blogService.findAllBlogs();

		List<Article> articles = articleService.getArticlesByBlogId(blogId);

		Tag tag = tagService.findTagByName(tagName)
				.orElseThrow(() -> new ResourceNotFoundException("Tag with name " + tagName + " not found"));

		List<Article> articlesByTag = getArticlesByTagName(tag, articles);

		List<Tag> blogTags = getTagsByBlogId(blogId);
		
		List<Tag> tags = tagService.findAllTagsSorted();

		List<Category> blogCategories = getCategoriesByBlogId(blogId);
		List<Category> categories = categoryService.findAllCategoriesSortedByName();

		List<DateDto> monthAndYear = listOfMonthsOfYear(LocalDateTime.now());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean hasBlog = authService.isUserHasBlog(username);
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("monthAndYear", monthAndYear);
		model.addAttribute("articles", articlesByTag);
		model.addAttribute("blog", blog);
		model.addAttribute("blogs", blogs);
		model.addAttribute("blogTags", blogTags);
		model.addAttribute("tags", tags);
		model.addAttribute("categories", categories);
		model.addAttribute("blogCategories", blogCategories);

		return "blog-tag";
	}

	private List<Article> getArticlesByTagName(Tag tag, List<Article> articles) {

		return articles.stream().filter(a -> a.getTags().contains(tag))
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed()).collect(Collectors.toList());
	}

	@GetMapping("/blogs/add")
	public String showAddBlogForm(Model model) throws ResourceNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean hasBlog = authService.isUserHasBlog(username);
		boolean createButton = false;
		model.addAttribute("createButton", createButton);
		model.addAttribute("loggedUser", username);
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("blogDto", new BlogDto());

		return BLOG_FORM;
	}

	@Transactional
	@PostMapping("/blogs/add")
	public String createBlog(@Valid BlogDto blogDto, BindingResult bindingResult, Model model, Principal principal)
			throws ResourceNotFoundException {
			boolean blogNameExists = blogService.isBlogExist(blogDto.getName());
		if (blogNameExists) {
			
			authService.ifNotAnonymousUserGetIdToModel(model, principal.getName());
			boolean hasBlog = authService.isUserHasBlog(principal.getName());
			
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("exist", true);
			return BLOG_FORM;
		}

		if (bindingResult.hasErrors()) {
			authService.ifNotAnonymousUserGetIdToModel(model, principal.getName());
			boolean hasBlog = authService.isUserHasBlog(principal.getName());
			
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("exist", true);
			return BLOG_FORM;
		}

		Blog blog = new Blog();
		blog.setName(blogDto.getName());
		blog.setDescription(blogDto.getDescription());
		UserEntity user = authService
				.findByUsername(principal.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found"));
		blog.setUser(user);

		blogService.save(blog);
		return "redirect:/blogs/" + blog.getId() + "/image/add";

	}

	@GetMapping("/blogs/{blogId}/update")
	public String showUpdateBlogForm(@PathVariable Long blogId, Model model) throws ResourceNotFoundException {

		Blog blog = blogService
				.getBlogById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserUsername = auth.getName();
		String blogOwnerUsername = blog.getUser().getUsername();
		authService.ifNotAnonymousUserGetIdToModel(model, loggedUserUsername);
		
		
		if(userSecurity.isOwner(blogOwnerUsername, loggedUserUsername)) {
			BlogDto blogDto = blogService.blogToBlogDto(blog);

			boolean hasBlog = authService.isUserHasBlog(loggedUserUsername);
			
			boolean createButton = false;
			
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("createButton", createButton);
			model.addAttribute("blogDto", blogDto);
		} else {
			throw new ForbiddenException("You don't have permission to do it.");
		}
		return BLOG_UPDATE_FORM;
	}

	@PostMapping("/blogs/{blogId}/update")
	@Transactional
	public String updateBlog(@PathVariable Long blogId, @Valid @ModelAttribute("blogDto") BlogDto blogDto,
			BindingResult bindingResult, Model model, Principal principal) throws ResourceNotFoundException {

		if (bindingResult.hasErrors()) {
			authService.ifNotAnonymousUserGetIdToModel(model, principal.getName());
			boolean hasBlog = authService.isUserHasBlog(principal.getName());
			boolean createButton = false;
			
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("createButton", createButton);
			return BLOG_UPDATE_FORM;
		}
		boolean blogNameExists = blogService.isBlogExist(blogDto.getName());
		if (blogNameExists) {
			authService.ifNotAnonymousUserGetIdToModel(model, principal.getName());
			boolean hasBlog = authService.isUserHasBlog(principal.getName());
			boolean createButton = false;
			
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("createButton", createButton);
			model.addAttribute("exist", true);
			return BLOG_UPDATE_FORM;
		}

		Blog blog = blogService.getBlogById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found"));
		blog.setName(blogDto.getName());
		blog.setDescription(blogDto.getDescription());

		return "redirect:/blogs/" + blogId;
	}

	@GetMapping("/blogs/{id}/image/add")
	public String showImageForm(@PathVariable Long id, Model model) throws ResourceNotFoundException {
		Blog blog = blogService.getBlogById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + id + " not found"));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		authService.ifNotAnonymousUserGetIdToModel(model, username);
		boolean hasBlog = authService.isUserHasBlog(username);
		boolean createButton = false;
		
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("createButton", createButton);
		model.addAttribute("blog", blog);

		return "image-blog-form";
	}

	@Transactional
	@PostMapping("/blogs/{id}/image/add")
	public String addImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException, IOException {
		Blog blog = blogService.getBlogById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + id + " not found"));
		ImageFile imageFile = ImageFile.builder()
				.file(file.getBytes())
				.filename(file.getOriginalFilename())
				.contentType(file.getContentType()).createdAt(LocalDate.now())
				.fileLength(file.getSize())
				.build();
		imageFileService.saveImageFile(imageFile);
		blog.setImage(imageFile);

		return "redirect:/";
	}

	@GetMapping("/blogs/{id}/image")
	public void showImage(@PathVariable Long id, HttpServletResponse response)
			throws IOException, ResourceNotFoundException {

		Blog blog = blogService.getBlogById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + id + " not found"));
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
		response.getOutputStream().write(blog.getImage().getFile());
		response.getOutputStream().close();
	}

	private List<Category> getCategoriesByBlogId(Long blogId) {
		return articleService.getArticlesByBlogId(blogId).stream().map(Article::getCategory).distinct()
				.collect(Collectors.toList());
	}

	private List<Tag> getTagsByBlogId(Long blogId) {
		return articleService.getArticlesByBlogId(blogId)
				.stream()
				.map(Article::getTags)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toList());
	}

	

}
