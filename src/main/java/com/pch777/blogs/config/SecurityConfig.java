package com.pch777.blogs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pch777.blogs.security.UserEntityDetailsService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserEntityDetailsService detailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
				.antMatchers("/api/auth", "/v3/api-docs/").permitAll()
				.antMatchers(HttpMethod.GET, "/*", "/index", "/blogs/**","/api/blogs","/api/articles", "/api/articles/*").permitAll()
				.antMatchers(HttpMethod.GET, "/api/blogs/*/articles", "/api/blogs/", "/api/blogs/*","/api/*/articles").permitAll()
				.antMatchers(HttpMethod.GET, "/articles/**", "/articles/*/image").permitAll()
				.antMatchers(HttpMethod.GET, "/categories/*","/api/categories", "/api/categories/*").permitAll()
				.antMatchers(HttpMethod.GET, "/comments/*").permitAll()
				.antMatchers(HttpMethod.GET, "/tags/*", "/api/tags/").permitAll()
				.antMatchers("/register", "/process_register", "/login", "/users/*/image").permitAll()
				.antMatchers(HttpMethod.POST, "/api/auth").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.defaultSuccessUrl("/").permitAll()
			.and().logout()
				.logoutSuccessUrl("/").permitAll();
		http.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		http.httpBasic();

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// Web resources
		web.ignoring().antMatchers("/css/**");
		web.ignoring().antMatchers("/js/**");
		web.ignoring().antMatchers("/img/**");
		web.ignoring().antMatchers("/api/auth/**");
		web.ignoring().antMatchers("/v3/api-docs/**");
		web.ignoring().antMatchers("configuration/**");
		web.ignoring().antMatchers("/swagger*/**");
		web.ignoring().antMatchers("/webjars/**");
		web.ignoring().antMatchers("/swagger-ui/**");

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(detailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
