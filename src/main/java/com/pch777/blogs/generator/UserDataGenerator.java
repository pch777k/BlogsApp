package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.service.ImageFileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDataGenerator {

	private final PasswordEncoder passwordEncoder;
	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final UserValuesProperties userValuesProperties;

	public UserEntity generate() throws IOException {
    	UserEntity user = new UserEntity();
        user.setFirstName(getRandomFirstName());
        user.setLastName(getRandomLastName());
        user.setUsername(getRandomUsername());
        user.setPassword(passwordEncoder.encode(userValuesProperties.getPasswordForGeneratedUsers()));
		user.setRoles(roles());
        user.setImage(getRandomImage());
        return user;
    }

	private Set<String> roles() {
		Set<String> roles = new HashSet<>();
		roles.add(userValuesProperties.getDefaultRole());
		return roles;
	}
    
    private ImageFile getRandomImage() throws IOException {
    	String imagePath = generatorMethods.getRandomItemFromList(getImagesPath());
    	ClassPathResource resource = new ClassPathResource(imagePath);

    	InputStream inputStream = resource.getInputStream();
    	File file = new File(imagePath);
    	FileUtils.copyInputStreamToFile(inputStream, file);
    	
    	ImageFile imageFile = new ImageFile();
       	if(file.exists()) {      		
       		imageFile.setFile(Files.readAllBytes(file.toPath()));
       		imageFile.setFilename(file.getName());
       		imageFile.setFileLength(file.length());     	
       		imageFileService.saveImageFile(imageFile);    				
       	}
       	return imageFile;
    }
      
    private String getRandomFirstName() throws IOException {
        return generatorMethods.getRandomItemFromList(getFirstNames());
    }

    private String getRandomLastName() throws IOException {
        return generatorMethods.getRandomItemFromList(getLastNames());
    }
    
    private String getRandomUsername() throws IOException {
        return generatorMethods.getRandomItemFromList(getUsernames());
    }  
    
    private List<String> getUsernames() throws IOException {
    	List<String> usernames = new ArrayList<>();
        if (usernames.isEmpty()) {
        	usernames = generatorMethods.loadLines(userValuesProperties.getUsernamesFilePath());
        }
        return usernames;
    }
    
    private List<String> getImagesPath() throws IOException {
    	List<String> imagePaths = new ArrayList<>();
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(userValuesProperties.getUserImageFilePath());
        }
        return imagePaths;
    }
    
	private List<String> getFirstNames() throws IOException {
		List<String> firstNames = new ArrayList<>();
        if (firstNames.isEmpty()) {
            firstNames = generatorMethods.loadLines(userValuesProperties.getFirstNamesFilePath());
        }
        return firstNames;
    }

    private List<String> getLastNames() throws IOException {
    	List<String> lastNames = new ArrayList<>();
        if (lastNames.isEmpty()) {
            lastNames = generatorMethods.loadLines(userValuesProperties.getLastNamesFilePath());
        }
        return lastNames;
    }
}
