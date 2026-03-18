# 项目规则

本文件用于指导AI在本项目中的辅助开发行为，AI在生成代码时必须遵循以下规则。

本项目为Pawhub宠物社交服务平台，主要功能聚焦在社交活动和提供服务，前者包括养宠人士的动态发布和分享，用户可发布宠物照片、文字日记，支持话题标签，方便进行养宠心得分享和同好社交；后者包括宠物美容、上门遛狗、临时寄养、宠物托管、紧急救助等多项服务接入，通过与商户合作方式，构建一个完善的宠物社交服务平台。

系统采用**前后端分离架构**

## 技术栈

- 前端：Vue2+Vue Router+Axios+Element UI
- 后端：Java+Spring Boot+ MyBatis+Maven
- 数据库：MySQL

## 项目目录结构

前端

```
frontend/
├── public/                    # 静态资源
│   └── index.html             # HTML模板
├── src/
│   ├── assets/                # 图片、字体等资源
│   │   └── images/
│   ├── components/            # 通用组件
│   ├── views/                  # 页面组件
│   ├── router/                  # 路由配置
│   │   └── index.js
│   ├── App.vue                   # 根组件
│   └── main.js                   # 入口文件
├── package.json
└── vue.config.js                 # Vue配置文件

```

后端

```
backend/
├── src/main/java/com/pawhub/
│   ├── PawhubApplication.java    # 启动类
│   ├── controller/                # 控制层
│   │   ├── UserController.java    # 用户接口
│   │   ├── PostController.java    # 动态接口
│   │   ├── ServiceController.java # 服务接口
│   │   ├── OrderController.java   # 订单接口
│   │   └── SearchController.java  # 搜索接口
│   ├── service/                    # 业务层接口
│   │   ├── UserService.java
│   │   ├── PostService.java
│   │   ├── ServiceService.java
│   │   └── OrderService.java
│   ├── service/impl/               # 业务层实现
│   │   ├── UserServiceImpl.java
│   │   ├── PostServiceImpl.java
│   │   ├── ServiceServiceImpl.java
│   │   └── OrderServiceImpl.java
│   ├── mapper/                      # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── PostMapper.java
│   │   ├── ServiceMapper.java
│   │   └── OrderMapper.java
│   ├── model/                        # 实体类
│   │   ├── User.java
│   │   ├── Post.java
│   │   ├── Service.java
│   │   ├── Order.java
│   │   ├── Comment.java
│   │   └── Like.java
│   ├── config/                        # 配置类
│   │   ├── CorsConfig.java            # 跨域配置
│   ├── filter/                         # 过滤器
│   │   └── JwtFilter.java              # JWT认证过滤器
│   └── utils/                          # 工具类
│       ├── JwtUtil.java                # JWT工具
└── pom.xml                              # Maven依赖配置
```

## 代码规范

前端：

- 使用**Vue2 Options API**

- 每个组件必须包含：<template> <script> <style scoped>

- 页面组件放在 `views/`
- 可复用组件放在 `components/`
- 优先使用 **Element UI 组件**，避免全局污染
- 禁止内联 style和直接操作 DOM

后端：

- 采用Spring Boot 三层架构

  ```
  Controller -> Service -> Repository -> Database
  ```

- **Controller 层**

   负责 HTTP 请求,返回统一 JSON 格式

  示例：

  ```java
  @PostMapping("/register")
      public Result register(@RequestBody Map<String, String> data) {
  
          String account = data.get("account");
          String password = data.get("password");
          String checkPassword = data.get("checkPassword");
  
          if (StringUtils.isAnyBlank(account, password, checkPassword)) {
              return Result.error("账号或密码不能为空");
          }
  
          return stuService.register(account, password, checkPassword);
      }
  ```

  

- **Service 层**

  负责业务逻辑，不直接处理 HTTP

  分为接口和类

  示例：

  类ServiceImpl:

  ```java
  public boolean updatePassword(Long id, String password) {
          // 对密码进行加密
          String encryptedPassword = DigestUtils.md5DigestAsHex(
                  (SALT + password).getBytes(StandardCharsets.UTF_8)
          );
          return stuMapper.updatePassword(id, encryptedPassword) > 0;
      }
  ```

  接口Service:

  ```java
  boolean updatePassword(Long id, String password);
  ```

  

- **Repository 层**

   负责数据库操作,使用MyBatis

    示例：

  ```java
  @Insert("INSERT INTO stu(account, password, created_time) " +
              "VALUES(#{account}, #{password}, #{createdTime})")
      @Options(useGeneratedKeys = true, keyProperty = "id")
      int insert(Stu stu);
  ```



## 禁止事项

AI 不得：

- 修改数据库配置
- 修改核心配置文件
- 使用未说明的技术框架
- 直接写 SQL 在 Controller
- 在前端组件中直接写 API 请求
- 创建与项目结构不一致的目录