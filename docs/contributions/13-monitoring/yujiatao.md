# 监控配置贡献说明

姓名：余佳陶
学号：2312190209
日期：2026-05-27

## 我完成的工作

### 1. 健康检查端点
- [x] 创建 `HealthController.java`，实现 `/health` 接口
- [x] 返回 JSON 格式响应（status、timestamp、service、database）
- [x] 集成数据库连接状态检测（通过 `SELECT 1` 验证）

### 2. JWT 过滤器放行配置
- [x] 修改 `JwtFilter.java`，放行监控相关路径：
  - `/health` - 健康检查
  - `/actuator/**` - Actuator 监控端点
  - `/uploads/**` - 静态资源（图片）

### 3. Actuator 指标收集
- [x] 在 `pom.xml` 中添加 `spring-boot-starter-actuator` 依赖
- [x] 配置 `application.properties` 启用 metrics 端点：
  ```properties
  management.endpoints.web.exposure.include=health,metrics,prometheus
  management.endpoint.metrics.enabled=true
  ```

### 4. 结构化日志配置
- [x] 创建 `logback-spring.xml`，配置 JSON 格式日志输出
- [x] 日志包含时间戳、日志级别、线程名、类名、消息内容

## 遇到的问题和解决

### 问题1：添加 Actuator 依赖后 `/actuator/metrics` 返回 404
**原因**：依赖错误地放在了 `dependencyManagement` 中，未实际引入
**解决**：将 Actuator 依赖移至 `<dependencies>` 标签内

## 心得体会

通过本次监控配置作业，掌握了：

1. **Spring Boot Actuator** 的使用方法，了解其自动提供的指标收集能力
2. **Logback 配置**，实现 JSON 格式的结构化日志输出
3. **JWT 过滤器**的路径放行机制，学会如何正确配置监控端点的白名单
4. **Docker 环境**下的代码变更、镜像重建、服务重启流程