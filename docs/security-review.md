# 安全审查报告



## 1、AI 辅助安全审查  

审查范围  :后端核心代码（认证、数据库操作、配置）

审查文件清单

| 文件                     | 说明                                   |
| ------------------------ | -------------------------------------- |
| `JwtUtil.java`           | JWT 工具类（密钥管理、Token 生成解析） |
| `JwtFilter.java`         | 认证过滤器（鉴权逻辑、路径放行）       |
| `UserController.java`    | 用户控制器（登录/注册/用户操作）       |
| `UserMapper.java`        | 用户 Mapper（SQL 查询）                |
| `application.properties` | 配置文件（数据库、密钥等）             |

---

## 发现的问题

### 问题 1：JWT 密钥硬编码（高危）

- **文件位置**：`JwtUtil.java` 
- **问题代码**：
  
  ```java
  private static final Key SECRET_KEY = Keys.hmacShaKeyFor("mySuperSecretKeyForPetSocial1234567890abcdef".getBytes());
  ```

- **漏洞类型**：OWASP  – 不安全设计（硬编码密钥）
- **危害说明**：密钥明文写在代码中，如代码泄露，攻击者可伪造任意用户的 JWT Token

**修复方案**：从环境变量读取密钥

**修复后的代码**：

```java
@Component
public class JwtUtil {
    
    private static Key SECRET_KEY;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Token 有效期：7天
    private static final long EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000L;

    @PostConstruct
    public void init() {
        // 从配置读取的 Base64 密钥解码
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("JwtUtil 初始化完成，密钥长度: " + keyBytes.length + " bytes");
    }
```

### 问题 2：CORS 配置硬编码（中危）

- **文件位置**：`JwtFilter.java` 

- **问题代码**:

  ```java
  response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
  response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
  ```

- **漏洞类型**：OWASP 不安全设计（CORS 配置不当）
- **危害说明**：CORS 配置硬编码在 Filter 中，无法适应多环境，且允许了所有危险方法

**修复方案**：创建独立的 CorsConfig 类，从配置文件读取

**修复后的代码**（新建 `CorsConfig.java`）：

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${cors.allowed.origins:http://localhost:8081}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

并从 `JwtFilter.java` 中删除 CORS 相关代码。