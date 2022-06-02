package com.pch777.blogs.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GeneratorMethods {
	private final Random random = new Random();
	
	List<String> loadLines(String filePath) {
        try {
            return Files.readAllLines(
                    Paths.get(
                            new ClassPathResource(
                                    filePath)
                                    .getURI()));
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format(
                            "Error while reading lines from file %s",
                            filePath),
                    e);
        }
    }
	
	String getRandom(List<String> elements) {
        return elements.get(
                random.nextInt(
                        elements.size()));
    }
	
	
}
