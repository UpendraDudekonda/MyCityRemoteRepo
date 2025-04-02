package com.mycity.user.config;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtService {
	
	@Value("${jwt.secret.user}")
	private String secretkey;
	
	@Value("${jwt.expiry.user}")
	private long tokenexpiry;
	
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secretkey.getBytes());	
		}

	public String GenerateToken(Long UserId, String email, String role)
	{
		return Jwts.builder()
				.setSubject(email)
				.claim("userId",UserId)
				.claim("role", role) // Add the user's role as a claim
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + tokenexpiry))
				.signWith(getSigningKey(),SignatureAlgorithm.HS256)
				.compact();
	}
	
	public Claims ValidateToken(String token) {
		
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public Claims ExtractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
				
	}
	public String Extractemail(String token) { //Extract email 
		return ExtractClaim(token, Claims::getSubject);
	}
	
	public <T> T ExtractClaim(String token , Function<Claims , T> claimsresolver) {
		Claims claim = ExtractAllClaims(token);
		return claimsresolver.apply(claim);
	}

	
	
	public Long ExtractUserId(String token) {
		return ExtractClaim(token,claims -> claims.get("userId",Long.class));
	}
	
	public String ExtractUserRole(String token) {
		return ExtractClaim(token,claims ->claims.get("role",String.class));
	}
	
	public void addJwtCookie(HttpServletResponse response ,String token) {
		
		Cookie Jwtcookie = new Cookie("jwt",token);
		Jwtcookie.setHttpOnly(true);
		Jwtcookie.setSecure(true);
		Jwtcookie.setPath("/");
		Jwtcookie.setMaxAge(60*60);
		
		response.addCookie(Jwtcookie);
	}
	
	
	public void ClearCookie(HttpServletResponse response) {
		
		
		Cookie 	Jwtcookie = new Cookie("jwt",null);
		Jwtcookie.setMaxAge(0);
		Jwtcookie.setHttpOnly(true);
		Jwtcookie.setPath("/");
		Jwtcookie.setSecure(true);
		
		
		response.addCookie(Jwtcookie);		
	}
	
}