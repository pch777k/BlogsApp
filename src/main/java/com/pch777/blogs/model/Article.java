package com.pch777.blogs.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String summary;
	
	@Column(length = 2147483647)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "image_id")
	private ImageFile image;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	@JsonIgnore
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name="blog_id")
	@JsonIgnore
	private Blog blog;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	@JsonIgnore
	private Category category;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable
	@JsonIgnoreProperties("articles")
	private Set<Tag> tags;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "article_id")
	private Set<Comment> comments;
	
	//@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	public void addTag(Tag tag) {
        tags.add(tag);
        tag.getArticles().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getArticles().remove(this);
    }
    
    public void removeTags() {
        Article self = this;
        tags.forEach(tag -> tag.getArticles().remove(self));
        tags.clear();
    }
	
}
