package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.repository.ArticleRepository;
import com.pch777.blogs.repository.TagRepository;

@Import({ TagService.class })
@DataJpaTest
@AutoConfigureTestDatabase
class TagServiceTest {

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private TagService tagService;

	@Test
	void givenNameOfTagShouldAddTag() {
		// given
		String tagName = "food";

		// when
		tagService.addTag(tagName);
		List<Tag> tags = tagRepository.findAll();

		// then
		assertFalse(tags.isEmpty());
		assertEquals(1, tags.size());

	}

	@Test
	void givenNameOfTagShouldNotAddTag() {
		// given
		String tagName1 = "food";
		String tagName2 = "Food";
		tagService.addTag(tagName1);
		// when
		tagService.addTag(tagName2);
		List<Tag> tags = tagRepository.findAll();

		// then
		assertFalse(tags.isEmpty());
		assertEquals(1, tags.size());

	}

	@Test
	void shouldFindAllTags() {
		// given
		givenTag("food");
		givenTag("sport");

		// when
		List<Tag> tags = tagService.findAllTags();

		// then
		assertFalse(tags.isEmpty());
		assertEquals(2, tags.size());

	}

	@Test
	void tagShouldExists() {
		// given
		Tag tag1 = givenTag("food");
		givenTag("sport");

		// when
		boolean result = tagService.tagExists(tag1.getName());

		// then
		assertTrue(result);

	}

	@Test
	void tagShouldNotExists() {
		// given
		givenTag("food");

		// when
		boolean result = tagService.tagExists("sport");

		// then
		assertFalse(result);

	}

	@Test
	void shouldGetAllTagsName() {
		// given
		Tag tag1 = givenTag("food");
		givenTag("sport");

		// when
		List<String> names = tagService.getAllTagsName();

		// then
		assertFalse(names.isEmpty());
		assertEquals(2, names.size());
		assertTrue(names.contains(tag1.getName()));
	}

	@Test
	void shouldFindOneTagWithTheHighestNumberOfArticles() {
		// given
		givenTag("food");
		Tag tag2 = givenTag("sport");

		Article article1 = new Article();
		Article article2 = new Article();
		articleRepository.save(article1);
		articleRepository.save(article2);

		tag2.setArticles(Set.of(article1, article2));

		// when
		List<Tag> tags = tagService.findTopTags(1);

		// then
		assertEquals(1, tags.size());
		assertEquals("sport", tags.get(0).getName());
	}

	@Test
	void shouldFindTwoTagsWithTheHighestNumberOfArticles() {
		// given
		Tag tag1 = givenTag("food");
		Tag tag2 = givenTag("sport");

		Article article1 = new Article();
		Article article2 = new Article();
		articleRepository.save(article1);
		articleRepository.save(article2);

		tag1.setArticles(Set.of(article1, article2));
		tag2.setArticles(Set.of(article1, article2));

		// when
		List<Tag> tags = tagService.findTopTags(2);

		// then
		assertEquals(2, tags.size());
		assertTrue(tags.contains(tag1));
	}

	@Test
	void testFindTagByName() {
		// given
		Tag tag1 = givenTag("food");
		givenTag("sport");

		// when
		Optional<Tag> resultTag = tagService.findTagByName("food");

		// then
		assertTrue(resultTag.isPresent());
		assertEquals(tag1, resultTag.get());
	}

	@Test
	void shouldFindAllTagsSorted() {
		// given
		Tag tag1 = givenTag("sport");
		Tag tag2 = givenTag("travel");
		Tag tag3 = givenTag("food");

		// when
		List<Tag> names = tagService.findAllTagsSorted();

		// then
		assertFalse(names.isEmpty());
		assertEquals(3, names.size());
		assertEquals(tag3, names.get(0));
		assertEquals(tag1, names.get(1));
		assertEquals(tag2, names.get(2));
	}
	
	private Tag givenTag(String name) {
		return tagRepository.save(new Tag(name));
	}

}
