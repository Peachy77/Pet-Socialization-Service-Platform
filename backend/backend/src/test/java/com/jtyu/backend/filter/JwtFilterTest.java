package com.jtyu.backend.filter;

import com.alibaba.fastjson.JSONObject;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {
    private JwtFilter jwtFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;


    @BeforeEach
    void setUp() throws Exception {
        // 手动初始化 JWT 密钥（解决 Key argument cannot be null 问题）
//        String testSecret = "TXlTdXBlclNlY3JldEtleUZvclBldFNvY2lhbDEyMzQ1Njc4OTBhYmNkZWY=";
        String testSecret = System.getenv().getOrDefault("JWT_SECRET", "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3Q=");
        byte[] keyBytes = Base64.getDecoder().decode(testSecret);
        Key secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

        // 通过反射设置 JwtUtil 的静态 SECRET_KEY 字段
        Field secretKeyField = JwtUtil.class.getDeclaredField("SECRET_KEY");
        secretKeyField.setAccessible(true);
        secretKeyField.set(null, secretKey);

        jwtFilter = new JwtFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
    }

    // ========== 路径放行测试 ==========

    @Test
    void testDoFilter_LoginPath_Success() throws Exception {
        request.setRequestURI("/users/login");
        request.setMethod("POST");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void testDoFilter_RegisterPath_Success() throws Exception {
        request.setRequestURI("/users/register");
        request.setMethod("POST");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void testDoFilter_UploadsPath_Success() throws Exception {
        request.setRequestURI("/uploads/avatar.png");
        request.setMethod("GET");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    // ========== OPTIONS 预检请求测试 ==========

    @Test
    void testDoFilter_OptionsRequest_ReturnsOk() throws Exception {
        request.setRequestURI("/api/test");
        request.setMethod("OPTIONS");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        // OPTIONS 请求会继续执行 chain
        assertNotNull(chain.getRequest());
    }

    // ========== Token 验证测试 ==========

    @Test
    void testDoFilter_NoToken_ReturnsUnauthorized() throws Exception {
        request.setRequestURI("/users/me");
        request.setMethod("GET");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());

        String content = response.getContentAsString();
        assertTrue(content.contains("NOT_LOGIN"));
    }

    @Test
    void testDoFilter_ValidTokenInAuthorizationHeader_Success() throws Exception {
        String token = JwtUtil.generateToken(1, "test@example.com");

        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + token);

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());

        MockHttpServletRequest mockRequest = (MockHttpServletRequest) chain.getRequest();
        assertEquals(1, mockRequest.getAttribute("currentUserId"));
    }

    @Test
    void testDoFilter_ValidTokenInTokenHeader_Success() throws Exception {
        String token = JwtUtil.generateToken(2, "user2@example.com");

        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("token", token);

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        MockHttpServletRequest mockRequest = (MockHttpServletRequest) chain.getRequest();
        assertEquals(2, mockRequest.getAttribute("currentUserId"));
    }

    @Test
    void testDoFilter_InvalidToken_ReturnsUnauthorized() throws Exception {
        String invalidToken = "invalid.token.string";

        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + invalidToken);

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        String content = response.getContentAsString();
        assertTrue(content.contains("NOT_LOGIN"));
    }

    @Test
    void testDoFilter_EmptyToken_ReturnsUnauthorized() throws Exception {
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer ");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testDoFilter_AuthorizationHeaderWithoutBearer_ReturnsUnauthorized() throws Exception {
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "SomeValue");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    // ========== 受保护路径需要 token 测试 ==========

    @Test
    void testDoFilter_ProtectedPathWithoutToken_ReturnsUnauthorized() throws Exception {
        request.setRequestURI("/posts");
        request.setMethod("GET");

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testDoFilter_ProtectedPathWithValidToken_Success() throws Exception {
        String token = JwtUtil.generateToken(1, "test@example.com");

        request.setRequestURI("/posts");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + token);

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        MockHttpServletRequest mockRequest = (MockHttpServletRequest) chain.getRequest();
        assertEquals(1, mockRequest.getAttribute("currentUserId"));
    }

    // ========== 生命周期方法测试 ==========

    @Test
    void testInit() throws Exception {
        javax.servlet.FilterConfig filterConfig = org.mockito.Mockito.mock(javax.servlet.FilterConfig.class);
        jwtFilter.init(filterConfig);
        assertTrue(true);
    }

    @Test
    void testDestroy() {
        jwtFilter.destroy();
        assertTrue(true);
    }

}
