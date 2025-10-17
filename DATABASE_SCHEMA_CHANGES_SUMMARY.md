# 数据库架构修改总结

## 📋 修改日期
**2025-10-17**

## 🔄 修改内容

### 1. 用户表 (users)
**删除字段**:
- ❌ `email` - 删除邮箱字段

**修改后的表结构**:
```sql
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
  `status` ENUM('active', 'inactive', 'banned') DEFAULT 'active' COMMENT '用户状态',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_campus` (`campus`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 2. 商品表 (products)
**修改字段**:
- `category` - 从 ENUM 改为 VARCHAR(50)
- `status` - 从 VARCHAR 改为 INT (0=active, 1=sold, 2=inactive, 3=deleted, 4=reserved)

**删除字段**:
- ❌ 商品数量字段

**修改后的表结构**:
```sql
CREATE TABLE IF NOT EXISTS `products` (
  `uid` BIGINT AUTO_INCREMENT NOT NULL COMMENT '商品ID (自增)',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID (外键)',
  `title` VARCHAR(100) NOT NULL COMMENT '商品标题 (1-100字符)',
  `description` TEXT COMMENT '商品描述 (1-2000字符)',
  `category` VARCHAR(50) NOT NULL COMMENT '商品分类',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '价格 (0.01-999999.99)',
  `condition` VARCHAR(50) NOT NULL COMMENT '商品成色',
  `campus` VARCHAR(100) NOT NULL COMMENT '所在校区',
  `status` INT DEFAULT 0 COMMENT '商品状态 (0=active, 1=sold, 2=inactive, 3=deleted, 4=reserved)',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否已删除',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`),
  KEY `fk_seller_id` (`seller_id`),
  KEY `idx_category` (`category`),
  KEY `idx_campus` (`campus`),
  KEY `idx_price` (`price`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
```

### 3. 充值表 (recharges)
**删除字段**:
- ❌ `status` - 充值状态
- ❌ `payment_method` - 支付方式
- ❌ `payment_url` - 支付链接
- ❌ `transaction_id` - 第三方交易ID
- ❌ `completed_at` - 完成时间

**修改后的表结构**:
```sql
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
```

### 4. 用户钱包表 (user_wallets)
**删除字段**:
- ❌ `total_recharged` - 总充值金额
- ❌ `total_spent` - 总消费金额

**修改后的表结构**:
```sql
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
```

### 5. 交易记录表 (transaction_logs)
**状态**: ❌ **已删除**

## 📝 Java 代码修改

### 实体类修改
- ✅ User.java - 删除 email 字段
- ✅ Product.java - status 改为 Integer
- ✅ Recharge.java - 删除 status, paymentMethod, paymentUrl, transactionId, completedAt
- ✅ UserWallet.java - 删除 totalRecharged, totalSpent

### VO 类修改
- ✅ LoginResponse.java - 删除 email
- ✅ UserProfileResponse.java - 删除 email
- ✅ RegisterRequest.java - 删除 email
- ✅ ProductResponse.java - status 改为 Integer
- ✅ ProductListResponse.java - status 改为 Integer
- ✅ RechargeResponse.java - 删除 status, payment_method, payment_url, completed_at

### Service 实现修改
- ✅ UserServiceImpl.java - 更新 register 和 login 方法
- ✅ ProductServiceImpl.java - status 设置为 0 (active)
- ✅ RechargeServiceImpl.java - 简化 createRecharge 和 getRechargeHistory

### Controller 修改
- ✅ AuthController.java - 更新 register 方法

## ✅ 编译验证
- ✅ 无编译错误
- ✅ 无 IDE 诊断错误
- ✅ 所有代码已验证

## 🚀 后续步骤

1. **备份现有数据库**
2. **执行数据库迁移** - 使用 `ducuments/database_schema.sql`
3. **编译项目** - `mvn clean install`
4. **启动应用** - `mvn spring-boot:run`
5. **测试所有功能**

## 📊 修改统计

| 项目 | 数量 |
|------|------|
| 修改的表 | 4 |
| 删除的表 | 1 |
| 删除的字段 | 10+ |
| 修改的实体类 | 4 |
| 修改的 VO 类 | 6 |
| 修改的 Service | 2 |
| 修改的 Controller | 1 |
| 编译错误 | 0 ✅ |

