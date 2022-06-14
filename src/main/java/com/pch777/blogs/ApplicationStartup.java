package com.pch777.blogs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.generator.ArticleDataGenerator;
import com.pch777.blogs.generator.BlogDataGenerator;
import com.pch777.blogs.generator.CategoryDataGenerator;
import com.pch777.blogs.generator.CommentDataGenerator;
import com.pch777.blogs.generator.GeneratorMethods;
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

@Component
@PropertySource("classpath:values.properties")
public class ApplicationStartup implements CommandLineRunner {

	private final TagRepository tagRepository;
	private final BlogRepository blogRepository;
	private final ArticleRepository articleRepository;
	private final CategoryRepository categoryRepository;
	private final UserEntityRepository userRepository;
	private final UserDataGenerator userGenerator;
	private final BlogDataGenerator blogGenerator;
	private final ArticleDataGenerator articleGenerator;
	private final CategoryDataGenerator categoryGenerator;
	private final CommentDataGenerator commentGenerator;
	private final CommentRepository commentRepository;
	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final int numberOfUsersWithoutBlog;
	private final int numberOfUsersWithBlog;
	private final int minNumberOfArticles;
	private final int maxNumberOfArticles;
	private final int minNumberOfComments;
	private final int maxNumberOfComments;
	private final String defaultUserAvatarFilePath;
	
	public ApplicationStartup(TagRepository tagRepository, 
			BlogRepository blogRepository,
			ArticleRepository articleRepository, 
			CategoryRepository categoryRepository,
			UserEntityRepository userRepository, 
			UserDataGenerator userGenerator, 
			BlogDataGenerator blogGenerator,
			ArticleDataGenerator articleGenerator, 
			CategoryDataGenerator categoryGenerator,
			CommentDataGenerator commentGenerator, 
			CommentRepository commentRepository,
			ImageFileService imageFileService, 
			GeneratorMethods generatorMethods, 
			@Value("${numberOfUsersWithoutBlog}") int numberOfUsersWithoutBlog,
			@Value("${numberOfUsersWithBlog}") int numberOfUsersWithBlog, 
			@Value("${minNumberOfArticles}") int minNumberOfArticles, 
			@Value("${maxNumberOfArticles}") int maxNumberOfArticles, 
			@Value("${minNumberOfComments}") int minNumberOfComments,
			@Value("${maxNumberOfComments}") int maxNumberOfComments, 
			@Value("${defaultUserAvatarFilePath}") String defaultUserAvatarFilePath) {
		this.tagRepository = tagRepository;
		this.blogRepository = blogRepository;
		this.articleRepository = articleRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.userGenerator = userGenerator;
		this.blogGenerator = blogGenerator;
		this.articleGenerator = articleGenerator;
		this.categoryGenerator = categoryGenerator;
		this.commentGenerator = commentGenerator;
		this.commentRepository = commentRepository;
		this.imageFileService = imageFileService;
		this.generatorMethods = generatorMethods;
		this.numberOfUsersWithoutBlog = numberOfUsersWithoutBlog;
		this.numberOfUsersWithBlog = numberOfUsersWithBlog;
		this.minNumberOfArticles = minNumberOfArticles;
		this.maxNumberOfArticles = maxNumberOfArticles;
		this.minNumberOfComments = minNumberOfComments;
		this.maxNumberOfComments = maxNumberOfComments;
		this.defaultUserAvatarFilePath = defaultUserAvatarFilePath;
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		getDefaultImage();
		generateUsersWithoutBlog(numberOfUsersWithoutBlog);
		generateData(numberOfUsersWithBlog, minNumberOfArticles, 
				maxNumberOfArticles, minNumberOfComments, maxNumberOfComments);

	}

	private void generateUsersWithoutBlog(int numberOfUsers) throws IOException {
		for (int i = 0; i < numberOfUsers; i++) {
			UserEntity user = userGenerator.generate();
			userRepository.save(user);
		}
	}

	public void generateData(int numberOfUsersWithBlog, int minNumberOfArticles, int maxNumberOfArticles,
			int minNumberOfComments, int maxNumberOfComments) throws IOException, ResourceNotFoundException {

		for (int i = 0; i < numberOfUsersWithBlog; i++) {
			UserEntity user = userGenerator.generate();
			userRepository.save(user);
			Blog blog = blogGenerator.generateBlog();
			blog.setUser(user);
			blogRepository.save(blog);
			int randomNumberOfArticles = generatorMethods.randomNumberBetweenMinAndMax(minNumberOfArticles,
					maxNumberOfArticles);

			for (int j = 0; j < randomNumberOfArticles; j++) {

				Article article = articleGenerator.generateArticle();
				article.setBlog(blog);
				article.setUser(user);
				Category category = categoryGenerator.generateCategory();
				String categoryName = category.getName();
				if (categoryRepository.existsByName(categoryName)) {

					category = categoryRepository.findByName(categoryName).orElseThrow(
							() -> new ResourceNotFoundException("Category with name " + categoryName + "not found"));
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
				int randomNumberOfComments = generatorMethods.randomNumberBetweenMinAndMax(minNumberOfComments,
						maxNumberOfComments);
				for (int k = 0; k < randomNumberOfComments; k++) {
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
		int minutesRange = (int) duration.toMinutes();
		return LocalDateTime.now().minusMinutes(random.nextInt(minutesRange));
	}

	private ImageFile getDefaultImage() throws IOException {
		ClassPathResource resource = new ClassPathResource(defaultUserAvatarFilePath);

		InputStream inputStream = resource.getInputStream();
		File file = new File(defaultUserAvatarFilePath);
		FileUtils.copyInputStreamToFile(inputStream, file);
		ImageFile imageFile = new ImageFile();
		if (file.exists()) {
			imageFile.setFile(Files.readAllBytes(file.toPath()));
			imageFile.setFilename(file.getName());
			imageFile.setFileLength(file.length());
			imageFileService.saveImageFile(imageFile);
		}
		return imageFile;
	}

}
