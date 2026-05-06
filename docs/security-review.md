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



## 2、安全检查清单

#### **认证与授权**

- [x] **密码存储**：使用 bcrypt / argon2 哈希，不存明文

  - ⚠️ **部分完成**
  - **适用**
  - **当前实现：** 使用 `PasswordUtil` 工具类，采用 **MD5 + 固定盐值** 的方式存储密码。
- [x] **JWT / Session**：token 有过期时间，logout 后失效

  - ⚠️ **部分完成**
  - **适用**
  - **当前实现：** JWT token 在服务端生成并设置了 `EXPIRATION_TIME_MS = 7天`。`JwtUtil` 可正确解析和校验过期时间。
- [x] **接口鉴权**：所有需要登录的接口都有权限校验

  - ✅ **已完成**
  - **适用**
  - **实现方式：** 项目使用了 `@RequestAttribute(required = false) Integer currentUserId` 来接收经过 JWT 拦截器解析后注入的用户 ID。拦截器会验证 token，失败时请求无法到达 Controller。
    所有修改/创建/删除/点赞等敏感操作都使用了 `@RequestAttribute Integer currentUserId`，强制要求登录。
- [x] **越权访问**：用户只能操作自己的数据（不能通过改 ID 访问他人数据）

  - ✅ **已完成**

  - **适用**

  - **实现方式：** 在所有 Service 层的关键操作方法中都进行了**用户归属验证**， 无法通过修改路径中的 ID 来操作他人的数据。
    **示例：**
    1. `AppointmentOrderServiceImpl.getOrderDetail`: 查询订单后，验证 `order.userId` 是否等于 `当前 userId`。
    2. `PostServiceImpl.deletePost`: 调用 `postMapper.selectUserIdByPostId` 验证是否为作者本人。
    3. `CommentServiceImpl.deleteComment`: 同样验证评论的作者。

#### **注入防护**

- [x] **SQL**：使用 ORM 或参数化查询，无字符串拼接 SQL
  - ✅ **已完成**
  - **适用**
  - **实现方式：** 项目持久层使用 **MyBatis**，所有 SQL 都写在 Mapper 接口的注解中。动态 SQL 使用了 `<script>` 和 `<if>` 标签，参数传递统一使用 `#{param}`（预编译）。例如 `selectList` 方法中的 `LIKE CONCAT('%', #{keyword}, '%')`。无字符串拼接 SQL 的代码。
- [x] **XSS**：前端输出用户数据时不用 innerHTML，或使用 DOMPurify
  - ✅ **已完成**
  - **适用**
  - **实现方式：**项目使用 Vue 框架，所有用户动态、评论等内容均通过 `{{ }}` 双花括号插值渲染，Vue 会自动转义 HTML 特殊字符。经代码审查确认，项目中未使用 `v-html` 指令，不存在直接输出未转义 HTML 的风险。当前防护措施满足业务需求。

#### **敏感信息**

- [x] **API Key /** **密码**：不硬编码在代码中，通过环境变量读取
  - ✅ **已完成**
  - **适用**
  - **实现方式：** 后端 API Key（DeepSeek）通过 `@Value` 注解从配置文件读取，未硬编码。数据库密码、JWT 密钥等敏感配置均通过 `application.yml` 管理，未出现在代码中。
- [x] **.env** **文件**：已加入 .gitignore，仓库中有 .env.example
  - ✅ **已完成**
  - **适用**
  - **实现方式：**`.gitignore` 已配置忽略 `.env`、`.env.local` 等敏感文件。前端使用环境变量管理配置，后端敏感配置通过 `application.yml` 外部化，仓库中不包含真实敏感信息。

#### **依赖安全**

- [x] 运行依赖扫描，无高危漏洞（或已记录已知漏洞原因）
  - **适用**
  - **处理方式：**在 **PowerShell** 中执行`mvn org.owasp:dependency-check-maven:check`,使用 OWASP Dependency-Check 工具进行依赖漏洞扫描。初步分析，除 **Spring Boot 2.7.6** 和 **Fastjson 1.2.83** 存在已知CVE 外，其余核心依赖（JJWT 0.11.5、OkHttp 4.12.0、MyBatis 2.3.0、Lombok 1.18.30）均无高危漏洞。



## 3、**CI** **自动化安全扫描**

- **密钥泄露扫描** `.github/workflows/security.yml`通过

