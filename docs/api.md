
# API 设计文档

## 基础信息

- 响应格式：JSON
- 认证方式：JWT（除登录注册外，需在请求头携带 `Authorization: Bearer <token>`）

## 用户模块

### 1. 用户注册
- **URL**：`POST /auth/register`
- **请求体**：
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
}
```

- **响应**：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "test"
  }
}
```

### 2. 用户登录
- **URL**：`POST /auth/login`
- **请求体**：
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "jwt_token_string",
    "userId": 1,
    "username": "test"
  }
}
```

### 3. 获取用户信息
- **URL**：`GET /users/:userId`
- **响应**：
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "username": "test",
    "email": "test@example.com",
    "followers": 100,
    "following": 50
  }
}
```

### 4. 关注/取关用户
- **URL**：`POST /users/:userId/follow`
- **响应**：
```json
{
  "code": 200,
  "message": "关注成功"
}
```

---

## 社交动态模块

### 1. 发布动态
- **URL**：`POST /posts`
- **请求体**：
```json
{
  "content": "string",
  "images": ["url1", "url2"],
  "tags": ["tag1", "tag2"]
}
```

### 2. 获取动态列表
- **URL**：`GET /posts`
- **查询参数**：
  - `page`：页码（默认1）
  - `limit`：每页条数（默认10）
  - `sort`：排序方式（time/hot）
- **响应**：
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "postId": 1,
        "content": "...",
        "userId": 1,
        "username": "test",
        "likeCount": 20,
        "commentCount": 5,
        "createTime": "2024-01-01"
      }
    ]
  }
}
```

### 3. 点赞/取消点赞动态
- **URL**：`POST /posts/:postId/like`
- **响应**：
```json
{
  "code": 200,
  "message": "点赞成功"
}
```

---

## 宠物服务模块

### 1. 获取服务商户列表
- **URL**：`GET /services`
- **查询参数**：
  - `category`：服务类型（美容/寄养/遛狗等）
  - `page`：页码
- **响应**：
```json
{
  "code": 200,
  "data": [
    {
      "serviceId": 1,
      "name": "XX美容店",
      "address": "xxx",
      "phone": "123456",
      "rating": 4.5
    }
  ]
}
```

### 2. 创建预约订单
- **URL**：`POST /orders`
- **请求体**：
```json
{
  "serviceId": 1,
  "userId": 1,
  "appointmentTime": "2026-03-01 14:00",
  "remark": "string"
}
```

### 3. 获取用户订单列表
- **URL**：`GET /orders/user/:userId`

---

## 搜索模块

### 1. 全局搜索
- **URL**：`GET /search`
- **查询参数**：
  - `keyword`：搜索关键词
  - `type`：搜索类型（user/post/service）
- **响应**：
```json
{
  "code": 200,
  "data": {
    "users": [...],
    "posts": [...],
    "services": [...]
  }
}
```
