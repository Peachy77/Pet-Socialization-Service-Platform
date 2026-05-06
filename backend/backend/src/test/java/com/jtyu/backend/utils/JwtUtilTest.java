package com.jtyu.backend.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

public class JwtUtilTest {
    @Test
    void testGenerateToken_Success() {
        String token = JwtUtil.generateToken(1, "test@example.com");

        assertNotNull(token);
        assertTrue(token.length() > 0);
        // token 格式应该是 xxx.yyy.zzz
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testParseTokenAndGetUserId_Success() {
        String token = JwtUtil.generateToken(123, "user@example.com");

        Integer userId = JwtUtil.parseTokenAndGetUserId(token);

        assertEquals(123, userId);
    }

    @Test
    void testParseTokenAndGetUserId_WithDifferentUser() {
        String token = JwtUtil.generateToken(456, "another@example.com");

        Integer userId = JwtUtil.parseTokenAndGetUserId(token);

        assertEquals(456, userId);
    }

    @Test
    void testParseTokenAndGetUserId_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.string";

        assertThrows(Exception.class, () -> {
            JwtUtil.parseTokenAndGetUserId(invalidToken);
        });
    }

    @Test
    void testParseTokenReturnClaims_Success() {
        String token = JwtUtil.generateToken(789, "claims@example.com");

        Claims claims = JwtUtil.parseTokenReturnClaims(token);

        assertNotNull(claims);
        assertEquals(789, claims.get("userId"));
        assertEquals("claims@example.com", claims.get("email"));
        assertEquals("789", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void testParseTokenReturnClaims_InvalidToken_ThrowsException() {
        String invalidToken = "bad.token.format";

        assertThrows(Exception.class, () -> {
            JwtUtil.parseTokenReturnClaims(invalidToken);
        });
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        String token = JwtUtil.generateToken(1, "test@example.com");

        boolean result = JwtUtil.validateToken(token);

        assertTrue(result);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "this.is.a.fake.token";

        boolean result = JwtUtil.validateToken(invalidToken);

        assertFalse(result);
    }

    @Test
    void testValidateToken_EmptyToken_ReturnsFalse() {
        boolean result = JwtUtil.validateToken("");

        assertFalse(result);
    }

    @Test
    void testValidateToken_NullToken_ReturnsFalse() {
        // 修正：validateToken 内部会捕获异常并返回 false，不会抛出异常
        boolean result = JwtUtil.validateToken(null);

        assertFalse(result);
    }

    @Test
    void testGenerateAndParse_MultipleTokens() {
        // 生成多个 token，验证互不干扰
        String token1 = JwtUtil.generateToken(1, "user1@test.com");
        String token2 = JwtUtil.generateToken(2, "user2@test.com");

        Integer userId1 = JwtUtil.parseTokenAndGetUserId(token1);
        Integer userId2 = JwtUtil.parseTokenAndGetUserId(token2);

        assertEquals(1, userId1);
        assertEquals(2, userId2);
        assertNotEquals(token1, token2);
    }
}
