package com.pch777.blogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.pch777.blogs.generator.ArticleDataGenerator;
import com.pch777.blogs.generator.BlogDataGenerator;
import com.pch777.blogs.generator.CategoryDataGenerator;
import com.pch777.blogs.generator.CommentDataGenerator;
import com.pch777.blogs.generator.UserDataGenerator;
import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.Comment;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.ArticleRepository;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.CategoryRepository;
import com.pch777.blogs.repository.CommentRepository;
import com.pch777.blogs.repository.TagRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;

@EnableJpaAuditing
@AllArgsConstructor
@SpringBootApplication
public class BlogsAppApplication implements CommandLineRunner {
	
	private final TagRepository tagRepository;
	private final BlogRepository blogRepository;
	private final ArticleRepository articleRepository;
	private final CategoryRepository categoryRepository;
//	private final CategoryService categoryService;
	private final UserEntityRepository userRepository;
//	private final ImageFileService imageFileService;
//	private final PasswordEncoder passwordEncoder;
	private final UserDataGenerator userGenerator;
	private final BlogDataGenerator blogGenerator;
	private final ArticleDataGenerator articleGenerator;
	private final CategoryDataGenerator categoryGenerator;
	private final CommentDataGenerator commentGenerator;
//	private final TagService tagService;
	private final CommentRepository commentRepository;
	private final ImageFileService imageFileService;
	private static final String DEFAULT_IMAGE_PATH = "classpath:static/img/default-avatar.png";
	private static final int NUMBER_OF_USERS_WITHOUT_BLOG = 10;
	private static final int NUMBER_OF_USERS_WITH_BLOG = 8;
	private static final int MAX_NUMBER_OF_ARTICLES = 6;
	private static final int MAX_NUMBER_OF_COMMENTS = 10;
	
	public static void main(String[] args) {
		SpringApplication.run(BlogsAppApplication.class, args);
	}
	@Override
	@Transactional
	public void run(String... args) throws Exception {
	/*	UserEntity leo = new UserEntity("Leo", "Messi", "leo", passwordEncoder.encode("1234"), Set.of("USER"));
		UserEntity luka = new UserEntity("Luka", "Doncic", "luka", passwordEncoder.encode("1234"), Set.of("USER"));
		UserEntity roger = new UserEntity("Roger", "Federer", "roger", passwordEncoder.encode("1234"), Set.of("USER"));

		File file = ResourceUtils.getFile("classpath:static/img/forest/forest01.jpg");
		if (file.exists()) {
			ImageFile imageFile = new ImageFile();
			imageFile.setFile(Files.readAllBytes(file.toPath()));
			imageFile.setFilename(file.getName());
			imageFile.setFileLength(file.length());

			imageFileService.saveImageFile(imageFile);
			leo.setImage(imageFile);

		}
		userRepository.save(leo);

		Blog blog = new Blog("Blog leo", "About leo");
		blog.setUser(leo);
		File fileBlog = ResourceUtils.getFile("classpath:static/img/forest/forest03.jpg");
		if (file.exists()) {
			ImageFile imageFile1 = new ImageFile();
			imageFile1.setFile(Files.readAllBytes(fileBlog.toPath()));
			imageFile1.setFilename(fileBlog.getName());
			imageFile1.setFileLength(fileBlog.length());

			imageFileService.saveImageFile(imageFile1);
			blog.setImage(imageFile1);

		}
		blogRepository.save(blog); */
		getDefaultImage(DEFAULT_IMAGE_PATH);
		generateUsersWithoutBlog(NUMBER_OF_USERS_WITHOUT_BLOG);
		generateData(NUMBER_OF_USERS_WITH_BLOG, MAX_NUMBER_OF_ARTICLES, MAX_NUMBER_OF_COMMENTS);

	}
	
	private void generateUsersWithoutBlog(int numberOfUsers) throws IOException {
		for (int k = 0; k < numberOfUsers; k++) {
			UserEntity user = userGenerator.generate();
			userRepository.save(user);
		}
	}

	public void generateData(int numberOfUsersWithBlog, 
			int maxNumberOfArticles, int maxNumberOfComments) throws IOException {

		Random random = new Random();
		
		for (int i = 0; i < numberOfUsersWithBlog; i++) {
			UserEntity user = userGenerator.generate();
			userRepository.save(user);
			Blog blog = blogGenerator.generateBlog();
			blog.setUser(user);
			blogRepository.save(blog);

			for (int j = 0; j < random.nextInt(1,maxNumberOfArticles); j++) {

				Article article = articleGenerator.generateArticle();
				article.setBlog(blog);
				article.setUser(user);
				Category category = categoryGenerator.generateCategory();

				if (categoryRepository.existsByName(category.getName())) {
					category = categoryRepository.findByName(category.getName()).orElseThrow();
				} else {
					categoryRepository.save(category);
				}

				Set<String> tagNames = categoryGenerator.generateTagsByCategoryName(category.getName());

				Set<Tag> tags = tagNames.stream().map(name -> {
					if (tagRepository.existsByName(name)) {
						return tagRepository.findTagByName(name).get();
					}
					return tagRepository.save(new Tag(name));
				}).collect(Collectors.toSet());

				article.setCategory(category);
				article.setTags(tags);
				articleRepository.save(article);
			
				for (int k = 0; k < random.nextInt(maxNumberOfComments); k++) {
					Comment comment = commentGenerator.generateComment();
					comment.setArticle(article);
					comment.setUser(randomUser(userRepository.findAll()));
					comment.setCreatedAt(randomCommentDate(article.getCreatedAt()));
					commentRepository.save(comment);
				}
			}
		}

	}

	private UserEntity randomUser(List<UserEntity> users) {
		Random random = new Random();
		return users.get(random.nextInt(users.size()));

	}

	private LocalDateTime randomCommentDate(LocalDateTime articleCreatedAt) {
		Random random = new Random();
		Duration duration = Duration.between(articleCreatedAt, LocalDateTime.now());
		long minutesRange = duration.toMinutes();
		return LocalDateTime.now().minusMinutes(random.nextLong(minutesRange));
	}
	
	private ImageFile getDefaultImage(String defaultImagePath) throws IOException {
    	File file = ResourceUtils.getFile(defaultImagePath);
    	ImageFile imageFile = new ImageFile();
       	if(file.exists()) {      		
       		imageFile.setFile(Files.readAllBytes(file.toPath()));
       		imageFile.setFilename(file.getName());
       		imageFile.setFileLength(file.length());     	
       		imageFileService.saveImageFile(imageFile);    				
       	}
       	return imageFile;
    }

}
