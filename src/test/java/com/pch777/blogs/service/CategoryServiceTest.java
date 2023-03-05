package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.repository.ArticleRepository;
import com.pch777.blogs.repository.CategoryRepository;

@Import({ CategoryService.class })
@DataJpaTest
@AutoConfigureTestDatabase
class CategoryServiceTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private CategoryService categoryService;

	@Test
	void shouldAddingANewCategory() {
		// given
		givenCategory("category");

		// when
		categoryService.addCategory("new_category");
		List<Category> categories = categoryService.findAllCategories();
		Optional<Category> newCategory = categories.stream().filter(c -> c.getName().equals("new_category"))
				.findFirst();

		// then
		assertEquals(2, categories.size());
		assertTrue(newCategory.isPresent());
	}

	@Test
	void shouldNotAddingANewCategoryIfCategoryNameExists() {
		// given
		givenCategory("category");

		// when
		categoryService.addCategory("category");
		List<Category> categories = categoryService.findAllCategories();
		Optional<Category> newCategory = categories.stream().filter(c -> c.getName().equals("category")).findFirst();

		// then
		assertEquals(1, categories.size());
		assertTrue(newCategory.isPresent());
	}

	@Test
	void shouldFindAllCategories() {
		// given
		givenCategory("category");

		// when
		List<Category> categories = categoryService.findAllCategories();

		// then
		assertEquals(1, categories.size());
		assertFalse(categories.isEmpty());
	}

	@Test
	void categoryShouldExists() {
		// given
		Category category = givenCategory("category");

		// when
		// then
		assertTrue(categoryService.categoryExists(category.getName()));
	}

	@Test
	void categoryShouldNotExists() {
		// given
		givenCategory("category");

		// when
		// then
		assertFalse(categoryService.categoryExists("categories"));
	}

	@Test
	void shouldFindAllNamesOfCategoriesSortedByName() {
		// given
		Category category1 = givenCategory("old_category");
		Category category2 = givenCategory("category");
		Category category3 = givenCategory("new_category");

		// when
		List<String> names = categoryService.getAllCategoriesName();

		// then
		assertFalse(names.isEmpty());
		assertEquals(3, names.size());
		assertTrue(names.containsAll(List.of(category1.getName(), category2.getName(), category3.getName())));
		assertEquals(names.get(0), category2.getName());
	}

	@Test
	void shouldFindAllCategoriesSortedByName() {
		// given
		Category category1 = givenCategory("old_category");
		Category category2 = givenCategory("category");
		Category category3 = givenCategory("new_category");
		
		// when
		List<Category> names = categoryService.findAllCategoriesSortedByName();

		// then
		assertFalse(names.isEmpty());
		assertEquals(3, names.size());
		assertTrue(names.containsAll(List.of(category1, category2, category3)));
		assertEquals(names.get(0), category2);
	}

	@Test
	void shouldFindCategoryWithTheHighestNumberOfArticles() {
		// given
		givenCategory("old_category");
		givenCategory("category");
		Category category3 = givenCategory("new_category");
		
		Article article1 = new Article();
		Article article2 = new Article();
		articleRepository.save(article1);
		articleRepository.save(article2);
		category3.setArticles(Set.of(article1, article2));

		// when
		List<Category> topCategories = categoryService.findTopCategories(1);

		// then
		assertFalse(topCategories.isEmpty());
		assertEquals(1, topCategories.size());
		assertTrue(topCategories.contains(category3));

	}

	@Test
	void shouldFindCategoryByName() {
		// given
		givenCategory("old_category");
		givenCategory("category");
		givenCategory("new_category");
		
		//when
		Optional<Category> category = categoryService.findByName("old_category");
		
		//then
		assertTrue(category.isPresent());
		assertEquals("old_category", category.get().getName());
	}
	
	@Test
	void shouldNotFindCategoryByName() {
		// given
		givenCategory("old_category");
		givenCategory("category");
		givenCategory("new_category");
		
		//when
		Optional<Category> category = categoryService.findByName("category1");
		
		//then
		assertFalse(category.isPresent());
	}
	
	private Category givenCategory(String categoryName) {
		return categoryRepository.save(new Category(categoryName));
	}

}
