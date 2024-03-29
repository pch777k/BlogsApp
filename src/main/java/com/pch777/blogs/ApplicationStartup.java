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
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.generator.ArticleDataGenerator;
import com.pch777.blogs.generator.BlogDataGenerator;
import com.pch777.blogs.generator.BlogValuesProperties;
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

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApplicationStartup implements CommandLineRunner {

	private final TagRepository tagRepository;
	private final BlogRepository blogRepository;
	private final ArticleRepository articleRepository;
	private final CategoryRepository categoryRepository;
	private final UserEntityRepository userRepository;
	private final CommentRepository commentRepository;
	private final UserDataGenerator userGenerator;
	private final BlogDataGenerator blogGenerator;
	private final ArticleDataGenerator articleGenerator;
	private final CategoryDataGenerator categoryGenerator;
	private final CommentDataGenerator commentGenerator;
	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final Random random;
	private final BlogValuesProperties blogValuesProperties;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		getDefaultImage();
		generateUsersWithoutBlog(blogValuesProperties.getNumberOfUsersWithoutBlog());
		generateData(blogValuesProperties.getNumberOfUsersWithBlog(), blogValuesProperties.getMinNumberOfArticles(),
				blogValuesProperties.getMaxNumberOfArticles(), blogValuesProperties.getMinNumberOfComments(),
				blogValuesProperties.getMaxNumberOfComments());
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

			createRandomNumbersOfArticles(minNumberOfComments, maxNumberOfComments, user, blog, randomNumberOfArticles);
		}

	}

	private void createRandomNumbersOfArticles(int minNumberOfComments, int maxNumberOfComments, UserEntity user,
			Blog blog, int randomNumberOfArticles) throws IOException, ResourceNotFoundException {

		for (int j = 0; j < randomNumberOfArticles; j++) {
			Category category = categoryGenerator.generateCategory();
			String categoryName = category.getName();
			boolean categoryExists = categoryRepository.existsByNameIgnoreCase(categoryName);
			if (categoryExists) {
				category = categoryRepository.findByName(categoryName).orElseThrow(
						() -> new ResourceNotFoundException("Category with name " + categoryName + "not found"));
			} else {
				categoryRepository.save(category);
			}

			Set<String> tagNames = categoryGenerator.generateTagsByCategoryName(category.getName());
			Set<Tag> tags = tagNamesToTags(tagNames);
			Article article = articleGenerator.generateArticle(blog, user, category, tags);
			articleRepository.save(article);
			int randomNumberOfComments = generatorMethods.randomNumberBetweenMinAndMax(minNumberOfComments,
					maxNumberOfComments);
			createRandomNumbersOfCommentsForArticle(article, randomNumberOfComments);
		}
	}

	private Set<Tag> tagNamesToTags(Set<String> tagNames) {
		return tagNames.stream().map(name -> {
			boolean tagExists = tagRepository.existsByNameIgnoreCase(name);
			if (tagExists) {
				return tagRepository.findTagByName(name).get();
			}
			return tagRepository.save(new Tag(name));
		}).collect(Collectors.toSet());
	}

	private void createRandomNumbersOfCommentsForArticle(Article article, int randomNumberOfComments)
			throws IOException {
		for (int i = 0; i < randomNumberOfComments; i++) {
			Comment comment = commentGenerator.generateComment();
			comment.setArticle(article);
			comment.setUser(randomUser(userRepository.findAll()));
			comment.setCreatedAt(randomCommentDate(article.getCreatedAt()));
			commentRepository.save(comment);
		}
	}

	private UserEntity randomUser(List<UserEntity> users) {
		return users.get(random.nextInt(users.size()));
	}

	private LocalDateTime randomCommentDate(LocalDateTime articleCreatedAt) {
		Duration duration = Duration.between(articleCreatedAt, LocalDateTime.now());
		int minutesRange = (int) duration.toMinutes();
		return LocalDateTime.now().minusMinutes(random.nextInt(minutesRange));
	}

	private ImageFile getDefaultImage() throws IOException {
		ClassPathResource resource = new ClassPathResource(blogValuesProperties.getDefaultUserAvatarFilePath());

		InputStream inputStream = resource.getInputStream();
		File file = new File(blogValuesProperties.getDefaultUserAvatarFilePath());
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
