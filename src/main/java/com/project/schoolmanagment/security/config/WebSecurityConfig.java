package com.project.schoolmanagment.security.config;

import com.project.schoolmanagment.security.jwt.AuthTokenFilter;
import com.project.schoolmanagment.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;

	private final AuthEntryPointJwt authEntryPointJwt;


	/**
	 *
	 * @return our own token filter class that exist in jwt package
	 */
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter(){
		return new AuthTokenFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}


	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(){
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}




}
