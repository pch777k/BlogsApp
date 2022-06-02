package com.pch777.blogs.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@ManyToMany(mappedBy = "tags", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnoreProperties("tags")
	private Set<Article> articles = new HashSet<>();
	
	public void addArticle(Article article) {
        articles.add(article);
        article.getTags().add(this);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.getTags().remove(this);
    }

	public Tag(String name) {
		this.name = name;
	}
    
    
 }
