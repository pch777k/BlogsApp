package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.service.ImageFileService;

@Service
public class UserDataGenerator {

	private final PasswordEncoder passwordEncoder;
	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final String firstNamesFilePath;
	private final String lastNamesFilePath;
	private final String usernamesFilePath;
	private final String userImageFilePath;
	private final String passwordForGeneratedUsers;
	private final String defaultRole;
    private List<String> firstNames;
    private List<String> lastNames;
    private List<String> usernames;
    private List<String> imagePaths;

    public UserDataGenerator(PasswordEncoder passwordEncoder, 
    		ImageFileService imageFileService,
			GeneratorMethods generatorMethods, 
			@Value("${firstNamesFilePath}") String firstNamesFilePath,
			@Value("${lastNamesFilePath}") String lastNamesFilePath,
			@Value("${usernamesFilePath}") String usernamesFilePath, 
			@Value("${userImageFilePath}") String userImageFilePath, 
			@Value("${passwordForGeneratedUsers}") String passwordForGeneratedUsers, 
			@Value("${defaultRole}") String defaultRole,
			List<String> firstNames, 
			List<String> lastNames, 
			List<String> usernames, 
			List<String> imagePaths) {
		this.passwordEncoder = passwordEncoder;
		this.imageFileService = imageFileService;
		this.generatorMethods = generatorMethods;
		this.firstNamesFilePath = firstNamesFilePath;
		this.lastNamesFilePath = lastNamesFilePath;
		this.usernamesFilePath = usernamesFilePath;
		this.userImageFilePath = userImageFilePath;
		this.passwordForGeneratedUsers = passwordForGeneratedUsers;
		this.defaultRole = defaultRole;
		this.firstNames = firstNames;
		this.lastNames = lastNames;
		this.usernames = usernames;
		this.imagePaths = imagePaths;
	}

	public UserEntity generate() throws IOException {
    	UserEntity user = new UserEntity();
        user.setFirstName(getRandomFirstName());
        user.setLastName(getRandomLastName());
        user.setUsername(getRandomUsername());
        user.setPassword(passwordEncoder.encode(passwordForGeneratedUsers));
		user.setRoles(roles());
        user.setImage(getRandomImage());
        return user;
    }

	private Set<String> roles() {
		Set<String> roles = new HashSet<>();
		roles.add(defaultRole);
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
        if (usernames.isEmpty()) {
        	usernames = generatorMethods.loadLines(usernamesFilePath);
        }
        return usernames;
    }
    
    private List<String> getImagesPath() throws IOException {
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(userImageFilePath);
        }
        return imagePaths;
    }
    
	private List<String> getFirstNames() throws IOException {
        if (firstNames.isEmpty()) {
            firstNames = generatorMethods.loadLines(firstNamesFilePath);
        }
        return firstNames;
    }

    private List<String> getLastNames() throws IOException {
        if (lastNames.isEmpty()) {
            lastNames = generatorMethods.loadLines(lastNamesFilePath);
        }
        return lastNames;
    }
}
