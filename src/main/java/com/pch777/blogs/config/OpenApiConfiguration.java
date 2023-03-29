package com.pch777.blogs.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@OpenAPIDefinition(
        info = @Info(
        	title = "Bargains API", 
        	description = "Blogs demo project. Using the app, you can create your own blog and write articles.",
        	version = "v1.0.0",
        	contact = @Contact(
                 name = "Pawel Chmiel-Kozdranski",
        	     email = "kozdranski@protonmail.com"))
)
public class OpenApiConfiguration {

}
