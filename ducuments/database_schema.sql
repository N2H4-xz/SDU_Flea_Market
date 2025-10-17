-- 闲鱼风二手物品交易平台 数据库建表语句
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 更新日期: 2025-10-17
-- 修改: 所有 UUID 改为自增 uid (BIGINT)
-- 修改: 删除邮箱、简化表结构、删除私信表

-- ============================================
-- 1. 用户表 (users)
-- ============================================
CREATE TABLE IF NOT EXISTS `users` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '用户ID (自增)',
  `username` VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名 (3-20字符)',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(500) COMMENT '头像URL',
  `campus` VARCHAR(100) COMMENT '校区',
  `major` VARCHAR(100) COMMENT '专业',
  `phone` VARCHAR(20) COMMENT '电话号码',
  `wechat` VARCHAR(100) COMMENT '微信号',
  `bio` TEXT COMMENT '个人简介',
  `user_status` ENUM('active', 'inactive', 'banned') DEFAULT 'active' COMMENT '用户状态',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_campus` (`campus`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_user_status` (`user_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 商品表 (products)
-- ============================================
CREATE TABLE IF NOT EXISTS `products` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '商品ID (自增)',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID (外键)',
  `title` VARCHAR(100) NOT NULL COMMENT '商品标题 (1-100字符)',
  `description` TEXT COMMENT '商品描述 (1-2000字符)',
  `category` VARCHAR(50) NOT NULL COMMENT '商品分类',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '价格 (0.01-999999.99)',
  `item_condition` VARCHAR(50) NOT NULL COMMENT '商品成色',
  `campus` VARCHAR(100) NOT NULL COMMENT '所在校区',
  `product_status` INT DEFAULT 0 COMMENT '商品状态 (0=active, 1=sold, 2=inactive, 3=deleted, 4=reserved)',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否已删除',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  KEY `fk_seller_id` (`seller_id`),
  KEY `idx_category` (`category`),
  KEY `idx_campus` (`campus`),
  KEY `idx_price` (`price`),
  KEY `idx_product_status` (`product_status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 3. 商品图片表 (product_images)
-- ============================================
CREATE TABLE IF NOT EXISTS `product_images` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '图片ID (自增)',
  `product_id` BIGINT NOT NULL COMMENT '商品ID (外键)',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `is_thumbnail` BOOLEAN DEFAULT FALSE COMMENT '是否为缩略图',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uid`),
  KEY `fk_product_id` (`product_id`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_product_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- ============================================
-- 4. 订单表 (orders)
-- ============================================
CREATE TABLE IF NOT EXISTS `orders` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '订单ID (自增)',
  `product_id` BIGINT NOT NULL COMMENT '商品ID (外键)',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID (外键)',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID (外键)',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
  `quantity` INT DEFAULT 1 COMMENT '购买数量',
  `order_status` ENUM('pending_payment', 'paid', 'completed', 'cancelled') DEFAULT 'pending_payment' COMMENT '订单状态',
  `payment_method` ENUM('online', 'offline') NOT NULL COMMENT '支付方式',
  `paid_at` TIMESTAMP NULL COMMENT '支付时间',
  `completed_at` TIMESTAMP NULL COMMENT '完成时间',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  KEY `fk_product_id` (`product_id`),
  KEY `fk_buyer_id` (`buyer_id`),
  KEY `fk_seller_id` (`seller_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_orders_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`uid`) ON DELETE RESTRICT,
  CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`uid`) ON DELETE RESTRICT,
  CONSTRAINT `fk_orders_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`uid`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 5. 留言表 (comments)
-- ============================================
CREATE TABLE IF NOT EXISTS `comments` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '留言ID (自增)',
  `product_id` BIGINT NOT NULL COMMENT '商品ID (外键)',
  `author_id` BIGINT NOT NULL COMMENT '作者ID (外键)',
  `content` VARCHAR(500) NOT NULL COMMENT '留言内容 (1-500字符)',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  KEY `fk_product_id` (`product_id`),
  KEY `fk_author_id` (`author_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_comments_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_comments_author` FOREIGN KEY (`author_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品留言表';

-- ============================================
-- 6. 收藏表 (favorites)
-- ============================================
CREATE TABLE IF NOT EXISTS `favorites` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '收藏ID (自增)',
  `user_id` BIGINT NOT NULL COMMENT '用户ID (外键)',
  `product_id` BIGINT NOT NULL COMMENT '商品ID (外键)',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `fk_product_id` (`product_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorites_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 7. 充值表 (recharges)
-- ============================================
CREATE TABLE IF NOT EXISTS `recharges` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '充值ID (自增)',
  `user_id` BIGINT NOT NULL COMMENT '用户ID (外键)',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '充值金额',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  KEY `fk_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_recharges_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值表';

-- ============================================
-- 8. 用户钱包表 (user_wallets)
-- ============================================
CREATE TABLE IF NOT EXISTS `user_wallets` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '钱包ID (自增)',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID (外键)',
  `balance` DECIMAL(10, 2) DEFAULT 0 COMMENT '账户余额',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  KEY `fk_user_id` (`user_id`),
  CONSTRAINT `fk_wallets_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';


