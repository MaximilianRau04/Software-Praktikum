package com.sopra.eaplanner;

import jakarta.annotation.Nonnull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class EaplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EaplannerApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@Nonnull CorsRegistry registry) {
				// allow CORS requests for all resources and HTTP methods from the frontend origin
				registry.addMapping("/**")
						.allowedMethods("*")
						.allowedOriginPatterns("*")
						.allowCredentials(true);
			}
		};
	}
}
