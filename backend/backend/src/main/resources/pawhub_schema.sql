-- ============================================
-- PawHub 数据库完整建表脚本
-- 数据库: pawhub
-- ============================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `user_id` int NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(255) NOT NULL,
    `email` varchar(100) NOT NULL,
    `avatar` varchar(255) DEFAULT 'default.jpg',
    `bio` varchar(255) DEFAULT NULL,
    `follower_count` int DEFAULT '0',
    `following_count` int DEFAULT '0',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 关注关系表
CREATE TABLE IF NOT EXISTS `follow` (
    `follow_id` int NOT NULL AUTO_INCREMENT,
    `follower_id` int NOT NULL,
    `followee_id` int NOT NULL,
    PRIMARY KEY (`follow_id`),
    UNIQUE KEY `follower_followee` (`follower_id`, `followee_id`),
    FOREIGN KEY (`follower_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`followee_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 动态表
CREATE TABLE IF NOT EXISTS `post` (
    `post_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `content` text,
    `images` json DEFAULT NULL,
    `tags` json DEFAULT NULL,
    `like_count` int DEFAULT '0',
    `comment_count` int DEFAULT '0',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`post_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `comment_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `post_id` int NOT NULL,
    `content` varchar(500) NOT NULL,
    `images` json DEFAULT NULL,
    `parent_comment_id` int DEFAULT '0',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 点赞表（动态点赞）
CREATE TABLE IF NOT EXISTS `like` (
    `like_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `post_id` int NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`like_id`),
    UNIQUE KEY `user_post` (`user_id`, `post_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
    `like_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `comment_id` int NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`like_id`),
    UNIQUE KEY `user_comment` (`user_id`, `comment_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`comment_id`) REFERENCES `comment`(`comment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 服务商户表
CREATE TABLE IF NOT EXISTS `service` (
    `service_id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `category` enum('grooming','walking','boarding','sitting','vet','emergency') NOT NULL,
    `address` varchar(255) DEFAULT NULL,
    `images` json DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `rating` decimal(2,1) DEFAULT '0.0',
    `review_count` int DEFAULT '0',
    `business_hours` json DEFAULT NULL,
    `description` text,
    `services_offered` json DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
    `favorite_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `service_id` int NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`favorite_id`),
    UNIQUE KEY `user_service` (`user_id`, `service_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `service`(`service_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 商户评论表
CREATE TABLE IF NOT EXISTS `service_comment` (
    `review_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `service_id` int NOT NULL,
    `rating` decimal(2,1) DEFAULT NULL,
    `content` varchar(500) NOT NULL,
    `images` json DEFAULT NULL,
    `parent_review_id` int DEFAULT '0',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`),
    KEY `idx_service_id` (`service_id`, `create_time`),
    KEY `idx_parent` (`parent_review_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `service`(`service_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. 商户评论点赞表
CREATE TABLE IF NOT EXISTS `service_review_like` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `review_id` int NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_review` (`user_id`, `review_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`review_id`) REFERENCES `service_comment`(`review_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `order_id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `service_id` int NOT NULL,
    `project_name` varchar(100) DEFAULT NULL COMMENT '服务项目名称',
    `appointment_time` datetime NOT NULL,
    `remark` varchar(255) DEFAULT NULL,
    `status` enum('pending','confirmed','completed','cancelled') DEFAULT 'pending',
    `price` decimal(10,2) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`service_id`) REFERENCES `service`(`service_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. 消息表
CREATE TABLE IF NOT EXISTS `message` (
    `message_id` int NOT NULL AUTO_INCREMENT,
    `receiver_id` int NOT NULL,
    `sender_id` int DEFAULT NULL,
    `type` enum('like','comment','follow','system','order') NOT NULL,
    `content` varchar(500) NOT NULL,
    `related_id` int DEFAULT NULL,
    `is_read` tinyint(1) DEFAULT '0',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`message_id`),
    KEY `idx_receiver` (`receiver_id`, `is_read`, `create_time`),
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`sender_id`) REFERENCES `user`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. 私信表
CREATE TABLE IF NOT EXISTS `private_message` (
    `message_id` int NOT NULL AUTO_INCREMENT,
    `sender_id` int NOT NULL,
    `receiver_id` int NOT NULL,
    `content` varchar(500) NOT NULL,
    `images` json DEFAULT NULL,
    `is_read` tinyint(1) DEFAULT '0',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`message_id`),
    KEY `idx_conversation` (`sender_id`, `receiver_id`, `create_time`),
    FOREIGN KEY (`sender_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 建表完成
-- ============================================