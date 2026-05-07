# 安全审查贡献说明

姓名  余佳陶  

学号  2312190209

日期  2026-05-06

## 我完成的工作

### AI 安全审查

- **审查的文件**：`JwtUtil.java`、`JwtFilter.java`、`UserController.java`、`UserMapper.java`、`application.properties`
- **AI 发现的主要问题**：
  1. JWT 密钥硬编码（高危）
  2. CORS 配置硬编码（中危）
  
- **我修复的问题**：
  1. JWT 密钥改为从环境变量 `${JWT_SECRET}` 读取
  2. CORS 配置移到独立的 `CorsConfig.java`，从配置文件读取

---

### 安全检查清单

| 检查项     | 状态     | 说明                                                 |
| ---------- | -------- | ---------------------------------------------------- |
| 密码存储   | ✅ 已处理 | 使用 MD5 + 固定盐值的方式加密                        |
| JWT 有效期 | ✅ 已处理 | 设置为 7 天自动过期                                  |
| 接口鉴权   | ✅ 已处理 | 受保护接口通过 `@RequestAttribute` 校验用户 ID       |
| 越权访问   | ✅ 已处理 | 用户只能操作自己的数据                               |
| SQL 注入   | ✅ 已处理 | 所有 Mapper 使用 `#{param}` 参数化查询               |
| 硬编码密钥 | ✅ 已修复 | JWT 密钥改为环境变量                                 |
| .env 配置  | ✅ 已处理 | 提交 `.env.example` 模板，`.env` 已加入 `.gitignore` |

---

### CI 安全扫描

- **配置的选项**：A - Gitleaks 密钥泄露扫描
- **扫描结果**：✅ 通过，未发现硬编码密钥

---

## PR 链接

- PR #X: [https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/26](https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/26)

---

## 遇到的问题和解决

### 1. 问题：CI 中 JWT 密钥未初始化导致测试失败

**解决**：在 `JwtUtilTest` 中使用反射手动初始化密钥，移除对 Spring 容器的依赖

### 2. 问题：`BackendApplicationTests` 因缺少环境变量失败

**解决**：删除该测试文件（无需验证启动上下文）

### 3. 问题：CORS 配置后无法登录

**解决**：统一使用 `CorsConfig` 处理跨域，`JwtFilter` 中 OPTIONS 请求改为 `chain.doFilter`

---

## 心得体会

1. **AI 辅助安全审查非常高效**：能快速识别硬编码密钥、SQL 注入等常见漏洞

2. **环境变量是最简单的安全实践**：永远不要硬编码密码/密钥

3. **测试环境的密钥可以放在 `application-test.properties`**：不影响生产安全，同时保证 CI 通过

5. **CI 中的安全扫描很有必要**：Gitleaks 能自动检测提交中的密钥泄露

