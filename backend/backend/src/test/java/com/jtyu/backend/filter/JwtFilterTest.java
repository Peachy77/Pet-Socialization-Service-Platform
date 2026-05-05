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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {
    private JwtFilter jwtFilter;

    @Mock
    private javax.servlet.FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter();
    }

    // ========== 路径放行测试 ==========

    @Test
    void testDoFilter_LoginPath_Success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/login");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        // 验证请求被放行，没有设置错误状态
        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void testDoFilter_RegisterPath_Success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/register");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void testDoFilter_UploadsPath_Success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/uploads/avatar.png");
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    // ========== OPTIONS 预检请求测试 ==========

    @Test
    void testDoFilter_OptionsRequest_ReturnsOk() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.setMethod("OPTIONS");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        // OPTIONS 请求不会继续执行 chain
        assertNull(chain.getRequest());
    }

    // ========== CORS 头测试 ==========

    @Test
    void testDoFilter_SetsCorsHeaders() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/login");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals("http://localhost:8081", response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, PUT, DELETE, PATCH, OPTIONS", response.getHeader("Access-Control-Allow-Methods"));
        assertEquals("Content-Type, token, Authorization", response.getHeader("Access-Control-Allow-Headers"));
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"));
    }

    // ========== Token 验证测试 ==========

    @Test
    void testDoFilter_NoToken_ReturnsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/me");
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());

        // 验证返回的内容是 NOT_LOGIN
        String content = response.getContentAsString();
        assertTrue(content.contains("NOT_LOGIN"));
    }

    @Test
    void testDoFilter_ValidTokenInAuthorizationHeader_Success() throws Exception {
        String token = JwtUtil.generateToken(1, "test@example.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());

        // 验证 userId 被存入了 request 属性
        MockHttpServletRequest mockRequest = (MockHttpServletRequest) chain.getRequest();
        assertEquals(1, mockRequest.getAttribute("currentUserId"));
    }

    @Test
    void testDoFilter_ValidTokenInTokenHeader_Success() throws Exception {
        String token = JwtUtil.generateToken(2, "user2@example.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("token", token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        MockHttpServletRequest mockRequest = (MockHttpServletRequest) chain.getRequest();
        assertEquals(2, mockRequest.getAttribute("currentUserId"));
    }

    @Test
    void testDoFilter_InvalidToken_ReturnsUnauthorized() throws Exception {
        String invalidToken = "invalid.token.string";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + invalidToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        String content = response.getContentAsString();
        assertTrue(content.contains("NOT_LOGIN"));
    }

    @Test
    void testDoFilter_EmptyToken_ReturnsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer ");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testDoFilter_AuthorizationHeaderWithoutBearer_ReturnsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/me");
        request.setMethod("GET");
        request.addHeader("Authorization", "SomeValue");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    // ========== 生命周期方法测试 ==========

    @Test
    void testInit() throws Exception {
        // 简单测试 init 方法不抛异常
        javax.servlet.FilterConfig filterConfig = mock(javax.servlet.FilterConfig.class);
        jwtFilter.init(filterConfig);
        // 如果没有异常，测试通过
        assertTrue(true);
    }

    @Test
    void testDestroy() {
        // 简单测试 destroy 方法不抛异常
        jwtFilter.destroy();
        assertTrue(true);
    }

}
