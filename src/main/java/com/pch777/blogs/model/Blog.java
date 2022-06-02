package com.pch777.blogs.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="blogs")
public class Blog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String description;
	
	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "image_id")
	private ImageFile image;
	
	@OneToOne(cascade = {CascadeType.ALL})
	@MapsId
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@OneToMany(cascade = {CascadeType.ALL})
	@JoinColumn(name = "blog_id")
	private Set<Article> articles = new HashSet<>();

	public Blog(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	
}
