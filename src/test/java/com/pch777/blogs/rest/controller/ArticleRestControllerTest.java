package com.pch777.blogs.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blogs.config.SecurityConfig;
import com.pch777.blogs.dto.ArticleDto;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.security.UserEntityDetailsService;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ArticleService;
import com.pch777.blogs.service.CategoryService;
import com.pch777.blogs.service.ImageFileService;

@WebMvcTest(ArticleRestController.class)
@Import(SecurityConfig.class)
class ArticleRestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ArticleService articleService;
	
	@MockBean
	private CategoryService categoryService;
	
	@MockBean
	private ImageFileService imageFileService;
	
	@MockBean
	private UserEntityDetailsService userService;
	
	@MockBean
	private UserSecurity userSecurity;
	
	@MockBean
	private UserEntityRepository userRepository;
	
	@MockBean
	private BlogRepository blogRepository;
	
	@Test
	void shouldGetEmptyListOfArticles() throws Exception {
		
		when(articleService.getAllArticles()).thenReturn(List.of());
		
		mockMvc
			.perform(get("/api/articles"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}
	
	@Test
	void shouldGetAllArticles() throws Exception {
		Article firstArticle = givenArticle(1L, "first title of article", "first summary of article", "first content of article");
		Article secondArticle = givenArticle(2L, "second title of article", "second summary of article", "second content of article");
		
		when(articleService.getAllArticles()).thenReturn(List.of(firstArticle, secondArticle));
		
		mockMvc
			.perform(get("/api/articles"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$.[0].id").value(1L))
			.andExpect(jsonPath("$.[0].title").value("first title of article"))
			.andExpect(jsonPath("$.[0].summary").value("first summary of article"))
			.andExpect(jsonPath("$.[0].content").value("first content of article"))
			.andExpect(jsonPath("$.[1].id").value(2L))
			.andExpect(jsonPath("$.[1].content").value("second content of article"));
	}

	@Test
	void shouldGetArticleById() throws Exception {
		Article firstArticle = givenArticle(1L, "first title of article", "first summary of article", "first content of article");
		
		when(articleService.getArticleById(anyLong())).thenReturn(Optional.of(firstArticle));
		
		mockMvc
			.perform(get("/api/articles/{articleId}",1))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.title").value("first title of article"))
			.andExpect(jsonPath("$.summary").value("first summary of article"))
			.andExpect(jsonPath("$.content").value("first content of article"));	
	}
	
	@Test
	void shouldNotFoundArticleById() throws Exception {
	
		when(articleService.getArticleById(anyLong())).thenReturn(Optional.empty());
		
		mockMvc
			.perform(get("/api/articles/{articleId}",1))
			.andDo(print())
			.andExpect(status().isNotFound());	
	}

	@Test
	void shouldGetArticlesByBlogId() throws Exception {
		
		Article firstArticle = givenArticle(1L, "first title of article", "first summary of article", "first content of article");
		Article secondArticle = givenArticle(2L, "second title of article", "second summary of article", "second content of article");
		
		when(articleService.getArticlesByBlogId(1L)).thenReturn(List.of(firstArticle, secondArticle));
		
		mockMvc
			.perform(get("/api/{blogId}/articles",1L))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$.[0].id").value(1L))
			.andExpect(jsonPath("$.[0].title").value("first title of article"))
			.andExpect(jsonPath("$.[0].summary").value("first summary of article"))
			.andExpect(jsonPath("$.[0].content").value("first content of article"))
			.andExpect(jsonPath("$.[1].id").value(2L))
			.andExpect(jsonPath("$.[1].content").value("second content of article"));
	}

	@Test
	@WithMockUser
	void shouldAddArticle() throws JsonProcessingException, Exception {
		ArticleDto articleDto = givenArticleDto("title", "summary", "content", "category", Set.of("tag1", "tag2"));
		Article article = givenArticle(1L, articleDto.getTitle(), articleDto.getSummary(), articleDto.getContent());
//		article.setId(1L);
//		article.setTitle(articleDto.getTitle());
//		article.setSummary(articleDto.getSummary());
//		article.setContent(articleDto.getContent());
//		UserEntity user = givenUserEntity("username");
//		Blog blog = givenBlog(1L, "blog", "description");
//		blog.setUser(user);
//		Category category = new Category("category");
//		Principal mockPrincipal = Mockito.mock(Principal.class);
		
//		when(blogRepository.findById(anyLong())).thenReturn(Optional.of(blog));
//		when(userSecurity.isOwnerOrAdmin(blog.getUser().getUsername(), "username")).thenReturn(true);
		
//		when(userRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
//		when(categoryService.findByName(articleDto.getCategoryName())).thenReturn(Optional.of(category));
		
		when(articleService.createArticle(anyLong(),any(), any())).thenReturn(article);
		
	//	doReturn(article).when(articleService).createArticle(anyLong(),any(), any());
		
		mockMvc
			.perform(post("/api/{blogId}/articles", 1L)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(articleDto))
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("title"))
			.andExpect(jsonPath("$.summary").value("summary"))
			.andExpect(jsonPath("$.content").value("content"));
						
	}
	
	@Test
	void shouldNotAddArticleIFUserUnauthorized() throws JsonProcessingException, Exception {
		ArticleDto articleDto = givenArticleDto("title", "summary", "content", "category", Set.of("tag1", "tag2"));
		Article article = givenArticle(1L, articleDto.getTitle(), articleDto.getSummary(), articleDto.getContent());
	
		doReturn(article).when(articleService).createArticle(anyLong(),any(), any());
		
		mockMvc
			.perform(post("/api/{blogId}/articles", 1L)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(articleDto))
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isUnauthorized());
						
	}

	@Test
	void testChangeArticle() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteArticle() {
		fail("Not yet implemented");
	}

	@Test
	void testAddPhotoToBlog() {
		fail("Not yet implemented");
	}
	
	private Article givenArticle(Long id, String title, String summary, String content) {
		Article article = new Article();
		article.setId(id);
		article.setTitle(title);
		article.setSummary(summary);
		article.setContent(content);
		return article;
	}
	
	private ArticleDto givenArticleDto(String title, String summary, String content, String categoryName, Set<String> tagsDto) {
		ArticleDto articleDto = new ArticleDto();
		articleDto.setTitle(title);
		articleDto.setSummary(summary);
		articleDto.setContent(content);
		articleDto.setCategoryName(categoryName);
		articleDto.setTagsDto(tagsDto);
		
		return articleDto;
	}
	
	private Blog givenBlog(Long id, String name, String description) {
		Blog blog = new Blog();
		blog.setId(id);
		blog.setName(name);
		blog.setDescription(description);
		return blog;
	}
	
	private UserEntity givenUserEntity(String username) {
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(username);
		return userEntity;
	}

}
