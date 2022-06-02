package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserDataGenerator {

	private final PasswordEncoder passwordEncoder;
	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	
	private static final String FIRST_NAMES_FILE_PATH = "firstNames";
	private static final String LAST_NAMES_FILE_PATH = "lastNames";
	private static final String USERNAMES_FILE_PATH = "usernames";
	private static final String IMAGE_FILE_PATH = "user-images";

    private List<String> firstNames;
    private List<String> lastNames;
    private List<String> usernames;
    private List<String> imagePaths;
    private final Random random = new Random();
    

    public UserEntity generate() throws IOException {
    	UserEntity user = new UserEntity();
        user.setFirstName(getRandomFirstName());
        user.setLastName(getRandomLastName());
        user.setUsername(getRandomUsername());
        user.setPassword(passwordEncoder.encode("1234"));
        user.setRoles(Set.of("USER"));
        user.setImage(getRandomImage());
        return user;
    }
    
    private ImageFile getRandomImage() throws IOException {
    	String imagePath = getRandomImagePath(getImagesPath());
    	File file = ResourceUtils.getFile(imagePath);
    	ImageFile imageFile = new ImageFile();
       	if(file.exists()) {      		
       		imageFile.setFile(Files.readAllBytes(file.toPath()));
       		imageFile.setFilename(file.getName());
       		imageFile.setFileLength(file.length());     	
       		imageFileService.saveImageFile(imageFile);    				
       	}
       	return imageFile;
    }
    
    
    private String getRandomFirstName() {
        return getRandom(getFirstNames());
    }

    private String getRandomLastName() {
        return getRandom(getLastNames());
    }
    
    private String getRandomUsername() {
        return getRandomUsernameData(getUsernames());
    }
    
    private String getRandomUsernameData(List<String> elements) {
        String username = elements.get(
                random.nextInt(
                        elements.size()));
        elements.remove(username);
        return username;
        
    }
    
    private String getRandomImagePath(List<String> elements) {
        String imagePath = elements.get(
                random.nextInt(
                        elements.size()));
        elements.remove(imagePath);
        return imagePath;
        
    }
    
    private String getRandom(List<String> elements) {
        return elements.get(
                random.nextInt(
                        elements.size()));
    }
    
    
    private List<String> getUsernames() {
        if (usernames.isEmpty()) {
        	usernames = generatorMethods.loadLines(USERNAMES_FILE_PATH);
        }
        return usernames;
    }
    
    private List<String> getImagesPath() {
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(IMAGE_FILE_PATH);
        }
        return imagePaths;
    }
    
	private List<String> getFirstNames() {
        if (firstNames.isEmpty()) {
            firstNames = generatorMethods.loadLines(FIRST_NAMES_FILE_PATH);
        }
        return firstNames;
    }

    private List<String> getLastNames() {
        if (lastNames.isEmpty()) {
            lastNames = generatorMethods.loadLines(LAST_NAMES_FILE_PATH);
        }
        return lastNames;
    }
}
