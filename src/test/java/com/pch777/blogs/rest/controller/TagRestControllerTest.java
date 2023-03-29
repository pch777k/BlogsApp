package com.pch777.blogs.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static  org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blogs.config.SecurityConfig;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.security.UserEntityDetailsService;
import com.pch777.blogs.service.TagService;

@WebMvcTest({TagRestController.class})
@Import(SecurityConfig.class)
class TagRestControllerTest {

	private static final String TAGS_BASE_PATH ="/api/tags/";
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	private TagService tagService;
	
	@MockBean
	private UserEntityDetailsService userDetailsService;
	
	@Test
	void shouldGetAllTaga() throws Exception {
		Tag firstTag = givenTag("firstTag");
		Tag secondTag = givenTag("secondTag");
		Tag thirdTag = givenTag("thirdTag");
		
		when(tagService.findAllTags()).thenReturn(List.of(firstTag, secondTag, thirdTag));
		
		mockMvc.perform(get(TAGS_BASE_PATH))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)));
				
	}
	
	private Tag givenTag(String name) {
		Tag tag = new Tag();
		tag.setName(name);
		return tag;
	}

}
