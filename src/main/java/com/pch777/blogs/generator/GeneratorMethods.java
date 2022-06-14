package com.pch777.blogs.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GeneratorMethods {
	
	private final Random random;	
	
	List<String> loadLines(String filePath) throws IOException {
		
		InputStream resource = new ClassPathResource(filePath).getInputStream();
		try ( BufferedReader reader = new BufferedReader(
			      new InputStreamReader(resource)) ) {
		return	reader.lines().collect(Collectors.toList());
	
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while reading lines from file %s",filePath), e);
        }
    }
	
	String getRandomItemFromList(List<String> items) {
        String item = items.get(
                random.nextInt(
                        items.size()));
        items.remove(item);
        return item;
    }
	
	public int randomNumberBetweenMinAndMax(int minNumber, int maxNumber) {
		maxNumber++;
		return (int) ((Math.random() * (maxNumber - minNumber)) + minNumber);
	}
	
	long getRandomNumberOfMinutes(int minutesRange) {
        return random
        		.nextInt(minutesRange);
    }
				
}
