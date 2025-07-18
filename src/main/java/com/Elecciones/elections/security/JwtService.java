package com.Elecciones.elections.security;

import com.Elecciones.elections.domain.UserApp;
import com.Elecciones.elections.dto.UserOut;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    
    @Value("${secret.code}")
    private String SECRET;
    
    public String generateToken(UserOut user) {
        return Jwts.builder()
                .setSubject(user.email())
                .claim("userId", user.id())
                .claim("name", user.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3000000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
    
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    public boolean isTokenValid(String token, UserApp user) {
        String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isTokenExpired(token);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
    
    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}