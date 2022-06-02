package com.pch777.blogs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pch777.blogs.model.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query("select distinct a "
			+ " from Article a "
			+ " left join fetch a.tags "
			+ " left join fetch a.comments "
			+ " where a.blog.id = :id")
	List<Article> findArticlesByBlogId(@Param("id") Long id);
	
	@Query("select a "
			+ " from Article a "
		//	+ " left join fetch a.tags "
		//	+ " left join fetch a.comments "
			+ " WHERE a.blog.id = :id AND " 
			+ " lower(a.title) LIKE %:keyword%")
	Page<Article> findAllArticlesByBlogId(Pageable pageable, @Param("id") Long id, @Param("keyword") String keyword);
	
	@Override
	@Query("select distinct a "
			+ "from Article a "
			+ "left join fetch a.tags "
			+ "left join fetch a.comments")
	List<Article> findAll();
	
	@Query ("SELECT a "
			+ " FROM Article a "
		//	+ " left join fetch a.tags "
		//	+ " left join fetch a.comments "
			+ " WHERE lower(a.category.name) = :categoryName")
	Page<Article> findByCategory(Pageable pageable, @Param("categoryName") String categoryName);
	
	@Query ("SELECT a "
			+ " FROM Article a "
			+ " WHERE lower(a.title) LIKE %?1%")
	Page<Article> findArticlesByNameLike(Pageable pageable, String keyword);

	
	@Query ("SELECT a "
			+ " FROM Article a "
		//	+ " left join fetch a.tags "
		//	+ " left join fetch a.comments "
			+ " WHERE a.blog.id = :id AND "
			+ " lower(a.category.name) = :categoryName")
	Page<Article> findArticlesByBlogIdAndByCategory(Pageable pageable, @Param("id") Long id, @Param("categoryName") String categoryName);

//	@Query ("SELECT a "
//			+ " FROM Article a "
//			+ " WHERE a.blog.id = :id AND "
//			+ " lower(a.tag.name) = :tagName")
//	Page<Article> findArticlesByBlogIdAndByTag(Pageable pageable, @Param("id") Long id, @Param("tagName") String tagName);
	
	

}
