package com.example.zexus_music_streaming.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTest {
    public static void main(String[] args) {
        String secret = "WcIgk71jB98g2H3PRBZNjyAXxyE0FcCuNI0Pu6NPWlU=";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWRtaW5JbnRlbGxpSjMiLCJpYXQiOjE3NDA5NTcyOTksImV4cCI6MTc0MTA0MzY5OX0.xaPcG_fFkBXjbX1FPUZDSUr_X9TzEIjiHDYux3sawyw";

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Token verified! Username: " + claims.getSubject());
        } catch (Exception e) {
            System.out.println("Verification failed: " + e.getMessage());
        }
    }
}
