package com.jtyu.backend.filter;


import com.alibaba.fastjson.JSONObject;
import com.jtyu.backend.model.Result;
import com.jtyu.backend.utils.JwtUtil;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class JwtFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("JwtFilter init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("JwtFilter拦截到请求");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 1. 处理 CORS 跨域
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, token, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 2. OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("放行 OPTIONS 预检请求");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("JwtFilter 拦截到请求: " + method + " " + uri);

        // 放行静态资源（图片）
        if (uri.startsWith("/uploads/")) {
            System.out.println("放行静态资源: " + uri);
            chain.doFilter(request, response);
            return;
        }

        // 3. 放行的路径列表（不需要 token）
        if ("/users/login".equals(uri) ||
                "/users/register".equals(uri)) {
            System.out.println("放行路径: " + uri);
            chain.doFilter(request, response);
            return;
        }

        System.out.println("需要验证的路径: " + uri);

        // 4. 获取请求头中的 token（前端用 Authorization: Bearer xxx 格式）
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 也兼容直接传 token 头的方式
        if (!StringUtils.hasLength(token)) {
            token = request.getHeader("token");
        }

        System.out.println("收到的 token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        // 5. 判断令牌是否存在
        if (!StringUtils.hasLength(token)) {
            System.out.println("未找到token，返回未登录信息");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result result = Result.error("NOT_LOGIN");
            String noLogin = JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(noLogin);
            return;
        }

        // 6. 解析 token，如果解析失败，返回未登录信息
        try {
            System.out.println("开始解析 token");
            Integer userId = JwtUtil.parseTokenAndGetUserId(token);
            System.out.println("token 解析成功，userId: " + userId);
            // 将 userId 存入 request 属性，供 Controller 使用
            request.setAttribute("currentUserId", userId);
        } catch (Exception e) {
            System.out.println("token 解析失败: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result result = Result.error("NOT_LOGIN");
            String noLogin = JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(noLogin);
            return;
        }

        System.out.println("JWT 验证通过，放行请求: " + uri);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        System.out.println("JwtFilter destroy");
    }
}
