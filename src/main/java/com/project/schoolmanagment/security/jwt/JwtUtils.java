package com.project.schoolmanagment.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);


	@Value("${backendapi.app.jwtExpirationMs}")
	private long jwtExpirations;

	@Value("${backendapi.app.jwtSecret}")
	private String jwtSecret;

	public boolean validateJwt(String jwtToken){
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
		}
	}

	/**
	 * @param username as String
	 * @return JWT signed with algorithm and our jwtSecret key
	 */
	public String generateTokenFromUsername(String username){
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtExpirations))
				.signWith(SignatureAlgorithm.ES512,jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token){
		return Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

}
