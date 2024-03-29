# BlogsApp
Blogs app created with Spring Boot.

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Deploy on heroku](#deploy-on-heroku)
* [Database diagram](#database-diagram)
* [Features](#features)
* [Screenshots](#screenshots)

## General info
Using the Blogs application, you can create your own blog, write articles. Each user can have a maximum of one blog. Additionally, you can sort articles by category or specific tag. Screenshots of the application's operation are shown below. The application can be tested, it is implemented on 
[heroku](https://blogs-pch777.herokuapp.com/), data is randomly generated at application startup to facilitate testing.
	
## Technologies
- Java 8
- Spring Boot
- Thymeleaf
- Lombok
- Maven
- HTML 
- CSS 
- Bootstrap 4
- JavaScript
- PostgreSQL
- Swagger OpenAPI
- Bootstrap 4 Select Picker
- Summernote editor
  
## Setup
Clone this repostory to your desktop. Run applications using Spring Boot. You will then be able to access it at localhost:8080

## Deploy on heroku
https://blogs-pch777.herokuapp.com/

## Database diagram
![blogs-diagram](https://user-images.githubusercontent.com/56579554/174240153-0c2cd568-95b4-49bf-88ed-bc0bd9a152fe.jpg)

## Start page
- [Home page](#home-page)

## Features

### User
- [User registration with field validation](#user-registration-with-field-validation)
- Editing avatar

### Blog
- Adding a new blog
- Validation fields of blog
- Editing the blog
- [All blogs](#home-page)
- [Sample blog page](#sample-blog-page)

### Article
- Adding a new article
- Validation fields of article
- Editing the article
- Deleting the article
- All blog's articles](#main-page)
- All articles of specific category
- All articles of specific blog's category
- All articles of specific tag
- All articles of specific blog's tag
- Search engine in the articles
- [View of a specific article](#sample-article-with-comments) 

### Category
- Adding a new category

### Tag
- Adding a new tag

### Comment
- Adding, editing, deleting a comment

## Screenshots
#### User registration with field validation
https://user-images.githubusercontent.com/56579554/175824221-79b507ba-555b-437a-8ec5-07d17e283ab8.mp4
___
#### Home page 
![blogs-screenshot-home-page](https://user-images.githubusercontent.com/56579554/174538793-b2f5ade5-ee27-43a6-ad89-2cd3920135a9.jpg)
___
#### Sample blog page 
![blogs-screenshot-sample-blog](https://user-images.githubusercontent.com/56579554/174543836-ee46efa7-f0a9-4f6e-bcb9-cfc0988f84ec.jpg)
___
#### Sample article with comments
![blogs-screenshot-article](https://user-images.githubusercontent.com/56579554/173847894-bbd498f6-57d6-44bb-bd56-acd7071eba4c.jpg)
![blogs-comments](https://user-images.githubusercontent.com/56579554/173856124-b21cfd01-75cf-4ed3-9ac6-794001a957d0.jpg)
