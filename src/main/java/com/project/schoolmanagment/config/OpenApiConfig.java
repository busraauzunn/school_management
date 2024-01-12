package com.project.schoolmanagment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(info = @Info(title = "StudentManagement API", version = "1.0.0"),
		security =@SecurityRequirement(name = "Bearer"))
@SecurityScheme(name = "Bearer", type = SecuritySchemeType.HTTP, scheme ="Bearer")
public class OpenApiConfig {
	//this is my remote directory manual changes
	
	//this is my another changes from local env.
	
	//this is third changes from local env.

	//now another one from browser.
	
	//this changes come from samet branch.

	//changes in develop from other developer.
}
