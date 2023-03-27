package com.pch777.blogs.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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

import com.pch777.blogs.dto.TagDto;
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
public class TagController {

	private static final String TAG_FORM = "tag-form";
	private final TagService tagService;
	private final ArticleService articleService;
	private final CategoryService categoryService;
	private final BlogService blogService;
	private final CommentService commentService;
	private final AuthService authService;
	private final ArticleValuesProperties articleValuesProperties;

	@GetMapping("/tags/add")
	public String showAddTagForm(Model model) throws ResourceNotFoundException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);		
		boolean hasBlog = authService.isUserHasBlog(username);

		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("tagDto", new TagDto());

		return TAG_FORM;
	}

	@PostMapping("/tags/add")
	public String addTag(@Valid @ModelAttribute("tagDto") TagDto tagDto, 
			BindingResult bindingResult, Model model) throws ResourceNotFoundException {
		
		if (bindingResult.hasErrors()) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = authService.isUserHasBlog(username);
			
			model.addAttribute("hasBlog", hasBlog);
			return TAG_FORM;
		}
		
		if (tagService.tagExists(tagDto.getName())) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			authService.ifNotAnonymousUserGetIdToModel(model, username);
			boolean hasBlog = authService.isUserHasBlog(username);
			
			model.addAttribute("hasBlog", hasBlog);
			model.addAttribute("exist", true);
			return TAG_FORM;
		}
		tagService.addTag(tagDto.getName());
		return "redirect:/";
	}

	@GetMapping("tags/{tagName}")
	public String getArticlesByTagName(@PathVariable String tagName,
			@RequestParam(defaultValue = "") String keyword, Model model) throws ResourceNotFoundException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);

		List<Article> articles = articleService.getAllArticles().stream()
				.filter(a -> a.getTitle().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());

		Tag tag = tagService.findTagByName(tagName)
				.orElseThrow(() -> new ResourceNotFoundException("Tag with name " + tagName + " not found"));

		List<Article> articlesByTag = getArticlesByTagName(tag, articles);

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
		model.addAttribute("hasBlog", hasBlog);
		model.addAttribute("articles", articlesByTag);
		model.addAttribute("latestArticles", latestFiveArticles);
		model.addAttribute("blogs", blogs);
		model.addAttribute("categories", categories);
		model.addAttribute("topFourCategories", topCategories);
		model.addAttribute("topSixTags", topSixTags);
		model.addAttribute("tags", tags);
		model.addAttribute("totalBlogs", totalBlogs);
		model.addAttribute("totalArticles", totalArticles);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComments", totalComments);

		return "tag-articles";
	}

	private List<Article> getArticlesByTagName(Tag tag, List<Article> articles) {

		return articles
				.stream()
				.filter(a -> a.getTags().contains(tag))
				.collect(Collectors.toList());
	}
}
