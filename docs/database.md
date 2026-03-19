# 数据库设计文档



## 数据表汇总

| 模块     | 表名      | 说明         |
| -------- | --------- | ------------ |
| 用户模块 | `user`    | 存储用户信息 |
| 社交模块 | `post`    | 动态帖子     |
| 社交模块 | `comment` | 评论         |
| 社交模块 | `like`    | 点赞记录     |
| 社交模块 | `follow`  | 关注关系     |
| 社交模块 | `message` | 聊天消息     |
| 服务模块 | `service` | 服务商户     |
| 服务模块 | `order`   | 预约订单     |



## ER图



![mermaid-diagram-2026-03-18-114855](C:\Users\余佳陶\Downloads\mermaid-diagram-2026-03-18-114855.png)

## 设计表结构

### 1、用户（user）

| 字段名          | 数据类型     | 约束                       | 说明             |
| :-------------- | :----------- | :------------------------- | :--------------- |
| user_id         | INT          | PRIMARY KEY AUTO_INCREMENT | 用户ID           |
| username        | VARCHAR(50)  | NOT NULL                   | 用户名           |
| password        | VARCHAR(255) | NOT NULL                   | 密码（加密存储） |
| email           | VARCHAR(100) | NOT NULL UNIQUE            | 邮箱             |
| avatar          | VARCHAR(255) | DEFAULT 'default.jpg'      | 头像URL          |
| bio             | VARCHAR(255) |                            | 个人简介         |
| follower_count  | INT          | DEFAULT 0                  | 粉丝数           |
| following_count | INT          | DEFAULT 0                  | 关注数           |

### 2、关注关系表（follow）

| 字段名      | 数据类型 | 约束                                               | 说明                     |
| :---------- | :------- | :------------------------------------------------- | :----------------------- |
| follow_id   | INT      | PRIMARY KEY AUTO_INCREMENT                         | 关注记录ID               |
| follower_id | INT      | NOT NULL                                           | 关注者ID（主动关注的人） |
| followee_id | INT      | NOT NULL                                           | 被关注者ID               |
| **约束**    |          | UNIQUE KEY (follower_id, followee_id)              | 不能重复关注             |
| **外键**    |          | FOREIGN KEY (follower_id) REFERENCES user(user_id) |                          |
| **外键**    |          | FOREIGN KEY (followee_id) REFERENCES user(user_id) |                          |

### 3、动态表（post）

| 字段名        | 数据类型 | 约束                                                         | 说明                                       |
| :------------ | :------- | :----------------------------------------------------------- | :----------------------------------------- |
| post_id       | INT      | PRIMARY KEY AUTO_INCREMENT                                   | 动态ID                                     |
| user_id       | INT      | NOT NULL                                                     | 发布者ID                                   |
| content       | TEXT     |                                                              | 文字内容                                   |
| images        | JSON     |                                                              | 图片URL数组，如 `["url1.jpg", "url2.jpg"]` |
| tags          | JSON     |                                                              | 话题标签数组，如 `["宠物美容", "柴犬"]`    |
| like_count    | INT      | DEFAULT 0                                                    | 点赞数                                     |
| comment_count | INT      | DEFAULT 0                                                    | 评论数                                     |
| create_time   | DATETIME | DEFAULT CURRENT_TIMESTAMP                                    | 发布时间                                   |
| update_time   | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP        | 更新时间                                   |
| **外键**      |          | FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE |                                            |

### 4、评论表（comment）

| 字段名      | 数据类型     | 约束                                                         | 说明       |
| :---------- | :----------- | :----------------------------------------------------------- | :--------- |
| comment_id  | INT          | PRIMARY KEY AUTO_INCREMENT                                   | 评论ID     |
| user_id     | INT          | NOT NULL                                                     | 评论者ID   |
| post_id     | INT          | NOT NULL                                                     | 所属动态ID |
| content     | VARCHAR(500) | NOT NULL                                                     | 评论内容   |
| create_time | DATETIME     | DEFAULT CURRENT_TIMESTAMP                                    | 评论时间   |
| update_time | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP        | 更新时间   |
| **外键**    |              | FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE |            |
| **外键**    |              | FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE |            |

### 5、点赞表（like）

| 字段名      | 数据类型 | 约束                                                         | 说明             |
| :---------- | :------- | :----------------------------------------------------------- | :--------------- |
| like_id     | INT      | PRIMARY KEY AUTO_INCREMENT                                   | 点赞记录ID       |
| user_id     | INT      | NOT NULL                                                     | 点赞用户ID       |
| post_id     | INT      | NOT NULL                                                     | 被点赞动态ID     |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP                                    | 点赞时间         |
| **约束**    |          | UNIQUE KEY (user_id, post_id)                                | 一人只能点赞一次 |
| **外键**    |          | FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE |                  |
| **外键**    |          | FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE |                  |

### 6、服务商户表（service）

| 字段名           | 数据类型     | 约束                                                  | 说明                                                         |
| :--------------- | :----------- | :---------------------------------------------------- | :----------------------------------------------------------- |
| service_id       | INT          | PRIMARY KEY AUTO_INCREMENT                            | 服务ID                                                       |
| name             | VARCHAR(100) | NOT NULL                                              | 商户名称                                                     |
| category         | ENUM         | NOT NULL                                              | 服务类型：grooming(美容)/walking(遛狗)/boarding(寄养)/sitting(托管)/vet(医院)/emergency(救助) |
| address          | VARCHAR(255) |                                                       | 详细地址                                                     |
| phone            | VARCHAR(20)  |                                                       | 联系电话                                                     |
| rating           | DECIMAL(2,1) | DEFAULT 0.0                                           | 评分（0.0-5.0）                                              |
| review_count     | INT          | DEFAULT 0                                             | 评价数量                                                     |
| business_hours   | JSON         |                                                       | 营业时间，如 `{"mon":"9:00-18:00", "tue":"9:00-18:00"}`      |
| description      | TEXT         |                                                       | 商户介绍                                                     |
| services_offered | JSON         |                                                       | 提供的服务项目及价格，如 `[{"name":"洗澡", "price":80}, {"name":"美容", "price":150}]` |
| create_time      | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 录入时间                                                     |
| update_time      | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间                                                     |

### 7、订单表（order）

| 字段名           | 数据类型      | 约束                                                         | 说明                                                         |
| :--------------- | :------------ | :----------------------------------------------------------- | :----------------------------------------------------------- |
| order_id         | INT           | PRIMARY KEY AUTO_INCREMENT                                   | 订单ID                                                       |
| user_id          | INT           | NOT NULL                                                     | 下单用户ID                                                   |
| service_id       | INT           | NOT NULL                                                     | 预约服务ID                                                   |
| appointment_time | DATETIME      | NOT NULL                                                     | 预约时间                                                     |
| remark           | VARCHAR(255)  |                                                              | 备注信息                                                     |
| status           | ENUM          | DEFAULT 'pending'                                            | 状态：pending(待确认)/confirmed(已确认)/completed(已完成)/cancelled(已取消) |
| price            | DECIMAL(10,2) |                                                              | 订单金额                                                     |
| create_time      | DATETIME      | DEFAULT CURRENT_TIMESTAMP                                    | 下单时间                                                     |
| update_time      | DATETIME      | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP        | 更新时间                                                     |
| **外键**         |               | FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE |                                                              |
| **外键**         |               | FOREIGN KEY (service_id) REFERENCES service(service_id) ON DELETE CASCADE |                                                              |

### 8. 消息表 (message)

| 字段名      | 数据类型     | 约束                                                         | 说明                                                         |
| :---------- | :----------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| message_id  | INT          | PRIMARY KEY AUTO_INCREMENT                                   | 消息ID                                                       |
| receiver_id | INT          | NOT NULL                                                     | 接收者ID                                                     |
| sender_id   | INT          |                                                              | 发送者ID（NULL表示系统消息）                                 |
| type        | ENUM         | NOT NULL                                                     | 消息类型：like(点赞)/comment(评论)/follow(关注)/system(系统)/order(订单) |
| content     | VARCHAR(500) | NOT NULL                                                     | 消息内容                                                     |
| related_id  | INT          |                                                              | 关联ID（如点赞的post_id、评论的comment_id）                  |
| is_read     | BOOLEAN      | DEFAULT FALSE                                                | 是否已读                                                     |
| create_time | DATETIME     | DEFAULT CURRENT_TIMESTAMP                                    | 发送时间                                                     |
| **外键**    |              | FOREIGN KEY (receiver_id) REFERENCES user(user_id) ON DELETE CASCADE |                                                              |
| **外键**    |              | FOREIGN KEY (sender_id) REFERENCES user(user_id) ON DELETE SET NULL |                                                              |
| **索引**    |              | INDEX idx_receiver (receiver_id, is_read, create_time)       | 查询用户未读消息                                             |