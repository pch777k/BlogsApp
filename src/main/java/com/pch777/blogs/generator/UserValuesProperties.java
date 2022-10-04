package com.pch777.blogs.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@PropertySource("classpath:values.properties")
public class UserValuesProperties {
	
	private String firstNamesFilePath;
	private String lastNamesFilePath;
	private String usernamesFilePath;
	private String userImageFilePath;
	private String passwordForGeneratedUsers;
	private String defaultRole;
	
	public UserValuesProperties(@Value("${firstNamesFilePath}") String firstNamesFilePath,
			@Value("${lastNamesFilePath}") String lastNamesFilePath,
			@Value("${usernamesFilePath}") String usernamesFilePath, 
			@Value("${userImageFilePath}") String userImageFilePath, 
			@Value("${passwordForGeneratedUsers}") String passwordForGeneratedUsers, 
			@Value("${defaultRole}") String defaultRole	) {
		this.firstNamesFilePath = firstNamesFilePath;
		this.lastNamesFilePath = lastNamesFilePath;
		this.usernamesFilePath = usernamesFilePath;
		this.userImageFilePath = userImageFilePath;
		this.passwordForGeneratedUsers = passwordForGeneratedUsers;
		this.defaultRole = defaultRole;
	}
	
	
}
