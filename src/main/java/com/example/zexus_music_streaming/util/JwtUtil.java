package com.example.zexus_music_streaming.util;

import com.example.zexus_music_streaming.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getParser().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return getParser().parseClaimsJws(token).getBody();
    }

    private JwtParser getParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("Decoded Secret Key: " + java.util.Base64.getEncoder().encodeToString(key.getEncoded())); // Added logging
        return key;
    }
}