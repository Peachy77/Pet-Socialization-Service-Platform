# PawHub API 使用说明

## 1、API 概述

本 API 用于 **宠物社交服务平台（PawHub）** 的后端接口通信，提供用户管理、社交互动、帖子发布、评论点赞、商户服务、预约订单以及私信消息等功能。

API 基于 **RESTful 风格设计**，数据格式统一使用 **JSON**。

## 2、基础信息

### API 基本信息

- **API 名称**：Pet Socialization Service Platform API
- **版本**：1.0.0
- **协议**：HTTP / HTTPS
- **数据格式**：JSON

### 服务器地址

```
http://localhost:8000
```

示例完整请求：

```
GET http://localhost:8000/api/users
```

## 3、用户管理 API

### 3.1 获取用户列表

#### 接口

```
GET /api/users
```

#### 功能

获取系统中的所有用户列表。

#### 请求参数

无

#### 响应示例

```
[
  {
    "user_id": 1,
    "username": "alice",
    "email": "alice@example.com",
    "avatar": "avatar_url",
    "bio": "宠物爱好者",
    "follower_count": 10,
    "following_count": 5
  }
]
```

### 3.2 创建用户

#### 接口

```
POST /api/users
```

#### 请求体

```
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "123456"
}
```

#### 返回

```
201 Created
```

### 3.3 获取用户信息

#### 接口

```
GET /api/users/{id}
```

#### 路径参数

| 参数 | 类型    | 描述   |
| ---- | ------- | ------ |
| id   | integer | 用户ID |

#### 示例

```
GET /api/users/1
```

### 3.4 更新用户信息

#### 接口

```
PUT /api/users/{id}
```

#### 请求体

```
{
  "username": "alice_new",
  "email": "alice_new@example.com",
  "bio": "养了一只猫"
}
```

### 3.5 删除用户

#### 接口

```
DELETE /api/users/{id}
```

## 4、用户关注 API

### 4.1 关注用户

#### 接口

```
POST /api/follow
```

#### 请求体

```
{
  "follower_id": 1,
  "followee_id": 2
}
```

### 4.2 取消关注

#### 接口

```
DELETE /api/follow/{id}
```

## 5、帖子系统 API

### 5.1 获取帖子列表

#### 接口

```
GET /api/posts
```

#### 返回示例

```
[
  {
    "post_id": 1,
    "user_id": 1,
    "content": "今天带狗狗去公园玩",
    "images": ["img1.jpg"],
    "like_count": 12,
    "comment_count": 5,
    "create_time": "2026-03-20T12:00:00"
  }
]
```

### 5.2 创建帖子

#### 接口

```
POST /api/posts
```

#### 请求体

```
{
  "content": "我家猫咪好可爱",
  "images": ["cat1.jpg", "cat2.jpg"]
}
```

### 5.3 删除帖子

#### 接口

```
DELETE /api/posts/{id}
```

## 6、点赞系统 API

### 6.1点赞帖子

#### 接口

```
POST /api/posts/{id}/like
```

#### 示例

```
POST /api/posts/3/like
```

## 7、评论系统 API

### 7.1 获取评论

#### 接口

```
GET /api/posts/{id}/comments
```

#### 返回示例

```
[
  {
    "comment_id": 1,
    "user_id": 2,
    "post_id": 3,
    "content": "好可爱！",
    "create_time": "2026-03-21T10:00:00"
  }
]
```

### 7.2 发表评论

#### 接口

```
POST /api/posts/{id}/comments
```

#### 请求体

```
{
  "content": "太可爱了！"
}
```

### 7.3 删除评论

#### 接口

```
DELETE /api/comments/{id}
```

## 8、宠物服务商 API

### 8.1 获取服务商列表

#### 接口

```
GET /api/services
```

#### 返回示例

```
[
  {
    "service_id": 1,
    "name": "Happy Pet Grooming",
    "category": "宠物美容",
    "address": "上海市XX路",
    "phone": "123456789",
    "rating": 4.8
  }
]
```

## 9、商户评论 API

### 9.1 获取商户评论

```
GET /api/services/{id}/reviews
```

### 9.2 创建商户评论

```
POST /api/services/{id}/reviews
```

#### 请求体

```
{
  "rating": 5,
  "content": "服务很好"
}
```

## 10、收藏功能 API

### 10.1收藏商户

```
POST /api/favorites
```

#### 请求体

```
{
  "user_id": 1,
  "service_id": 2
}
```

## 11、订单预约 API

### 11.1 获取订单列表

```
GET /api/orders
```

### 11.2 创建预约订单

```
POST /api/orders
```

#### 请求体

```
{
  "user_id": 1,
  "service_id": 2,
  "appointment_time": "2026-03-25T10:00:00",
  "remark": "给狗狗洗澡"
}
```

## 12、消息系统 API

### 12.1 获取消息列表

```
GET /api/messages
```

## 13、私信系统 API

### 13.1 获取私信记录

```
GET /api/private_messages
```

### 13.2 发送私信

```
POST /api/private_messages
```

#### 请求体

```
{
  "sender_id": 1,
  "receiver_id": 2,
  "content": "你好，可以一起遛狗吗？"
}
```

## 14、状态码说明

| 状态码 | 含义           |
| ------ | -------------- |
| 200    | 请求成功       |
| 201    | 创建成功       |
| 400    | 请求参数错误   |
| 401    | 未授权         |
| 404    | 资源不存在     |
| 500    | 服务器内部错误 |

## 15、数据格式说明

所有 API 均使用：

```
Content-Type: application/json
```

示例请求头：

```
Content-Type: application/json
```