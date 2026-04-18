package com.jtyu.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("mySuperSecretKeyForPetSocial1234567890abcdef".getBytes());

    // Token 有效期：7天（可根据需要调整）
    private static final long EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 JWT token（存入 userId 和 email）
     */
    public static String generateToken(Integer userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT 并返回 userId
     */
    public static Integer parseTokenAndGetUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Integer.class);
    }

    /**
     * 解析 JWT 并返回 Claims（用于获取更多信息）
     */
    public static Claims parseTokenReturnClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 token 是否有效（返回 true/false，不抛异常）
     */
    public static boolean validateToken(String token) {
        try {
            parseTokenAndGetUserId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
