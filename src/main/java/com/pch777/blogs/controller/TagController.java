package com.pch777.blogs.controller;

import java.util.Comparator;
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

import com.pch777.blogs.dto.TagDto;
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
public class TagController {

	private final TagService tagService;
	private final ArticleService articleService;
	private final CategoryService categoryService;
	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	private final CommentService commentService;
	private final AuthService authService;

	@GetMapping("/tags/add")
	public String showAddTagForm(Model model) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String email = auth.getName();

		// model.addAttribute("currentUser", userService.findUserByEmail(email));x
		model.addAttribute("tagDto", new TagDto());

		return "tag-form";
	}

	@PostMapping("/tags/add")
	public String addTag(@Valid @ModelAttribute("tagDto") TagDto tagDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {

			return "tag-form";
		}
		tagService.addTag(tagDto.getName());
		return "redirect:/";
	}

	@GetMapping("tags/{tagName}")
	public String getArticlesByCategoryName(Model model, @PathVariable String tagName)
			throws ResourceNotFoundException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String username = auth.getName();
		authService.ifNotAnonymousUserGetIdToModel(model, username);

		List<Article> articles = articleService.getAllArticles();
		
		Tag tag = tagService
				.findTagByName(tagName)
				.orElseThrow(() -> new ResourceNotFoundException("Tag with name " + tagName + " not found"));
		
		List<Article> articlesByTag = getArticlesByTagName(tag, articles);
		

		// List<Tag> tags = getTagsByBlogId(blogId);
		List<Article> latestFiveArticles = articleService.getAllArticles().stream()
				.sorted(Comparator.comparing(Article::getCreatedAt).reversed()).limit(5).collect(Collectors.toList());

		List<Category> categories = categoryService.findAllCategories().stream().sorted((o1, o2) -> {
			if (o1.getArticles().size() == o2.getArticles().size())
				return 0;
			else if (o1.getArticles().size() < o2.getArticles().size())
				return 1;
			else
				return -1;
		}).limit(4).collect(Collectors.toList());
		
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

		List<Tag> tags = tagService.findAllTags().stream().sorted((o1, o2) -> {
			if (o1.getArticles().size() == o2.getArticles().size())
				return 0;
			else if (o1.getArticles().size() < o2.getArticles().size())
				return 1;
			else
				return -1;
		}).limit(6).collect(Collectors.toList());

		List<Blog> blogs = blogRepository.findAll();
		
		int totalBlogs = blogs.size();
		int totalArticles = articleService.getAllArticles().size();
		int totalUsers = userRepository.findAll().size();
		int totalComments = commentService.getAllComments().size();
		boolean createButton = true;
		model.addAttribute("createButton", createButton);
		model.addAttribute("articles", articlesByTag);
		model.addAttribute("latestArticles", latestFiveArticles);
		// model.addAttribute("blog", blog);
		// model.addAttribute("tags", tags);
		model.addAttribute("categories", categories);
		model.addAttribute("topCategories", topCategories);
		model.addAttribute("tags", tags);
		model.addAttribute("totalBlogs", totalBlogs);
		model.addAttribute("totalArticles", totalArticles);
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalComments", totalComments);

		return "articles";
	}

	private List<Article> getArticlesByTagName(Tag tag, List<Article> articles) {

		return articles.stream().filter(a -> a.getTags().contains(tag)).collect(Collectors.toList());
	}
}
