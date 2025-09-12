package com.example.F5_Backend.util;

import com.example.F5_Backend.entities.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24 * 7; // 7 days
    private static SecretKey KEY;
    private static String SECRET;
//    private static final SecretKey KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    public static String createAccessToken(Users user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(Map.of(
                        "user_id", user.getId(),
                        "email", user.getEmail(),
                        "role_id", user.getUserRole().getId(),
                        "status_id", user.getStatus().getId(),
                        "gender_id", user.getGender().getId(),
                        "fname", user.getFname(),
                        "lname", user.getLname()
                ))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
    }

    public static Long extractUserId(String token) {
        return Long.valueOf(parse(token).getBody().get("user_id").toString());
    }

    public static String extractEmail(String token) {
        return parse(token).getBody().getSubject();
    }

    public static boolean isTokenExpired(String token) {
        Date expiration = parse(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

    public static boolean validateToken(String token, Long expectedUserId) {
        try {
            Claims claims = parse(token).getBody();
            return claims.getSubject().equals(String.valueOf(expectedUserId)) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    @Value("${app.jwt.secret}")
    public void setSecret(String secret) {
        if (SECRET != null) {
            return;
        }
        JwtUtil.SECRET = secret;
        JwtUtil.KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
