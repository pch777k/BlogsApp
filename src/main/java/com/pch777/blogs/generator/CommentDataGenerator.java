package com.pch777.blogs.generator;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Comment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommentDataGenerator {

	private final GeneratorMethods generatorMethods;
	
	private static final String COMMENTS_FILE_PATH = "comments";
	
    private List<String> comments;

    private final Random random = new Random();    
	
    public Comment generateComment() throws IOException {
    	Comment comment = new Comment();
        comment.setContent(getRandomCommentContent());
        return comment;
    }  
    
    private String getRandomCommentContent() {
        return getRandom(getCommentContents());
    }
     
    private String getRandom(List<String> elements) {
 
        return elements.get(
                random.nextInt(
                        elements.size()));
    }
    
	private List<String> getCommentContents() {
        if (comments.isEmpty()) {
            comments = generatorMethods.loadLines(COMMENTS_FILE_PATH);
        }
        return comments;
    }

}
