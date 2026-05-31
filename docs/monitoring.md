# 监控配置说明

## 概述

本项目后端（Java Spring Boot）已配置基础监控能力，包括健康检查端点、结构化日志和基础指标收集。

## 1. 健康检查端点

### 端点信息
- **URL**: `GET /health`
- **返回格式**: JSON
- **认证要求**: 无需认证（已放行）

### 响应示例

```json
{
  "status": "UP", 
  "timestamp": 1779850463989,
  "service": "pawhub-backend",
  "database": "UP"
}
```

其中

- status :服务状态UP / DOWN
- timestamp:检查时间戳（毫秒）
- service:服务名称
- database:数据库连接状态：UP / DOWN

### 测试命令

```
curl http://localhost:8080/health
```



## 2.日志管理

### 日志格式

- **格式类型**: JSON 结构化日志
- **配置位置**: `src/main/resources/logback-spring.xml`
- **日志级别**: INFO（生产环境）

### 日志输出示例

```bash
backend-1 |{"timestamp":"2026-05-30 14:47:25.824","Level":"INFO","thread":"main","logger":"o.a.coyote.http11.Http11NioProtocol","message":"Initializing ProtocolHandler["http -nio-8080"]"}
backend-1|"timestamp":"2026-05-30 14:47:25.824","Level":"INFo","thread":"main""logger":"o.a.catalina.core.Standardservice","message":"starting service [Tomcat]"}
backend-1 |{"timestamp":"2026-05-30 14:47:25.825","Level":"INFO","thread":"main","logger":"o.a.catalina.core.StandardEngine","message":"Starting Servlet engine:[Apache Tomca t/9.0.69]"}
```

### 查看日志

```bash
# Docker 环境
docker compose logs backend --tail 50
```

## 3. 基础指标收集

### 技术方案

使用 **Spring Boot Actuator** + **Micrometer** 实现指标收集，自动采集应用运行数据。

### 配置说明

**依赖配置**（`pom.xml`）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**应用配置**（`application.properties`）:

```properties
# Actuator 配置
management.endpoints.web.exposure.include=health,metrics
management.metrics.export.prometheus.enabled=true
management.endpoint.metrics.enabled=true
management.endpoint.health.show-details=always
```

### 响应示例

```bash
{"status":"UP","components":{"db":{"status":"UP","details":{"database":"MySQL","validationQuery":"isValid()"}},"diskSpace":{"status":"UP","details":{"total":1081101176832,"free":1018065494016,"threshold":10485760,"exists":true}},"ping":{"status":"UP"}}}
```

### 测试命令

```bash
# 查看所有可用指标
curl http://localhost:8080/actuator/metrics

# 查看 HTTP 请求指标
curl http://localhost:8080/actuator/metrics/http.server.requests

# 查看 JVM 内存
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# 查看 CPU 使用率
curl http://localhost:8080/actuator/metrics/process.cpu.usage
```

### 收集指标

- 错误率
  - http.server.requests按状态码分组,通过 `status` 标签过滤
- 请求计数和响应时间
  - http.server.requests:HTTP 请求总数及响应时间分布
  - http.server.requests.count:请求计数
  - http.server.requests.duration:请求持续时间（毫秒）

