package com.pch777.blogs.generator;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Comment;

@Service
public class CommentDataGenerator {

	private final GeneratorMethods generatorMethods;
	private final String commentsFilePath;	
    private List<String> comments;
     
    public CommentDataGenerator(GeneratorMethods generatorMethods,  
    		@Value("${commentsFilePath}") String commentsFilePath,
			List<String> comments) {
		this.generatorMethods = generatorMethods;
		this.commentsFilePath = commentsFilePath;
		this.comments = comments;
	}

	public Comment generateComment() throws IOException {
    	Comment comment = new Comment();
        comment.setContent(getRandomCommentContent());
        return comment;
    }  
    
    private String getRandomCommentContent() throws IOException {
        return generatorMethods.getRandomItemFromList(getCommentContents());
    }
        
	private List<String> getCommentContents() throws IOException {
        if (comments.isEmpty()) {
            comments = generatorMethods.loadLines(commentsFilePath);
        }
        return comments;
    }

}
