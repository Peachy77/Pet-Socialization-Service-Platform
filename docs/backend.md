# 后端说明文档

## 一、项目概述

本项目后端为宠物社交服务平台提供完整的 API 接口支持，采用 **Node.js + Express** 框架开发，数据库使用 **MySQL**，实现用户认证、社交动态、宠物服务等核心业务逻辑。后端遵循 RESTful API 设计规范，使用 JWT 进行身份验证，确保数据交互的安全性和可靠性。

## 二、模块功能介绍

### 2.1 用户模块

功能描述：
- 用户注册、登录
- JWT 令牌生成与验证
- 用户资料管理和个性化设置
- 用户关注、收藏、评论、点赞等行为支持
- 消息通知系统

### 2.2 社交动态模块

功能描述：
- 动态发布,支持文字、图片、话题标签等
- 动态列表获取，可以按照分页、按时间或者热度排序
- 动态详情查看
- 点赞/取消点赞帖子
- 社交评论功能等

### 2.3 宠物服务模块

功能描述：
- 提供多项宠物服务，如宠物美容、上门遛狗、临时寄养、宠物托管、紧急救助、就近宠物医院预约等
- 多家商户列表展示，及详情提供
- 在线预约、联系，订单管理等

### 2.4 搜索模块

功能描述：
- 全局搜索，可以根据用户、商户、帖子多类型关键词搜索
- 根据话题标签获取相关帖子

## 三、技术选型

| 技术                 | 作用         |
| -------------------- | ------------ |
| Node.js              | 运行时环境   |
| Express              | Web 应用框架 |
| MySQL                | 数据库       |
| JSON Web Token (JWT) | 用户身份认证 |

## 四、目录结构（大致规划）

```
backend/
|-config/	#配置类存放目录
	|-CorsConfig	#跨域配置类，允许前端跨域请求
|-controller/   #控制层，处理HTTP请求
   |-userController.java	# 用户模块接口
   |- commentController.java	# 评论模块接口
   |-serviceController.java	# 服务模块接口
   |-searchController.java	# 搜索模块接口
   ...
|-filter/       #过滤器/拦截器
   |-JwtFilter   #JWT 认证过滤器，拦截请求验证token
|-mapper/		# MyBatis 数据访问层（接口）	
	|-UserMapper.java        # 用户表数据库操作接口
	|-CommentMapper.java       # 评论表数据库操作接口
	|-LikeMapper.java         # 点赞记录表数据库操作接口
	
	
|-model/        #实体类
   |-User.java      #用户实体类
   |-Comment.java    #评论实体类
   |-Like.java   	# 点赞记录实体类
   |-Service.java   # 服务商户实体类 
   |-Order.java	# 订单实体类
   |-Message.java	# 信息通知实体类
   ...
|-service/     # 业务逻辑层   
	|-Impl/		#业务逻辑层实现类
		|-UserServiceImpl.java               # 用户业务实现类
		├── CommentServiceImpl.java              # 评论业务实现类
		├── LikeServiceImpl.java                  # 点赞业务实现类
		...
	|-UserService.java                   # 用户业务接口
	|-CommentService.java                  # 评论业务接口
	├── LikeService.java                      # 点赞业务接口
	...
|-utils/    # 工具类目录
	|-JwtUtil.java		# JWT 生成与解析工具类
	
	
```

## 五、运行方式



**1、启动服务**

点击**run**按钮