# 闲鱼风二手物品交易平台 — 数据库设计文档

## 文档说明
- **数据库类型**: MySQL 8.0+
- **字符集**: utf8mb4 (支持中文和 emoji)
- **排序规则**: utf8mb4_unicode_ci (不区分大小写)
- **设计日期**: 2025-10-16
- **版本**: v1.0

---

## 1. 数据库表结构概览

### 核心表 (10 个)

| 表名 | 说明 | 
|------|------|----------|
| users | 用户表 | 
| products | 商品表 | 
| product_images | 商品图片表 | 
| orders | 订单表 | 
| comments | 留言表 | 
| favorites | 收藏表 | 
| messages | 私信表 | 
| recharges | 充值表 | 
| user_wallets | 用户钱包表 | 
| transaction_logs | 交易记录表 | 

---

## 2. 详细表结构说明

### 2.1 用户表 (users)

**用途**: 存储用户基本信息和认证数据

**关键字段**:
- `user_id`: UUID 主键，全局唯一
- `username`: 用户名，唯一索引，用于登录
- `email`: 邮箱，唯一索引，用于登录和通知
- `password_hash`: 密码哈希值，使用 bcrypt 或 argon2
- `status`: 用户状态 (active/inactive/banned)

**索引策略**:
- 主键: user_id
- 唯一索引: username, email
- 普通索引: campus, created_at, status, updated_at



---

### 2.2 商品表 (products)

**用途**: 存储商品基本信息

**关键字段**:
- `product_id`: UUID 主键
- `seller_id`: 卖家 ID，外键关联 users 表
- `category`: 商品分类 (电子产品/图书/服装/其他)
- `condition`: 商品成色 (全新/九成新/八成新/七成新/较旧)
- `view_count`: 浏览次数，用于热度排序
- `is_deleted`: 是否已删除 (逻辑删除)

**索引策略**:
- 主键: product_id
- 外键: seller_id
- 复合索引: (seller_id, created_at)
- 普通索引: category, campus, price, created_at, condition, is_deleted

**设计说明**:
- 使用逻辑删除 (is_deleted) 而不是物理删除
- 便于数据恢复和审计



---

### 2.3 商品图片表 (product_images)

**用途**: 存储商品的多张图片 URL

**关键字段**:
- `image_id`: UUID 主键
- `product_id`: 商品 ID，外键关联 products 表
- `image_url`: 图片 URL
- `is_thumbnail`: 是否为缩略图
- `sort_order`: 排序顺序

**设计说明**:
- 一个商品可以有多张图片 (最多 9 张)
- 缩略图用于列表展示
- sort_order 用于控制图片顺序



---

### 2.4 订单表 (orders)

**用途**: 存储订单信息

**关键字段**:
- `order_id`: UUID 主键
- `product_id`: 商品 ID，外键
- `buyer_id`: 买家 ID，外键
- `seller_id`: 卖家 ID，外键
- `status`: 订单状态 (pending_payment/paid/completed/cancelled)
- `payment_method`: 支付方式 (online/offline)
- `paid_at`: 支付时间
- `completed_at`: 完成时间

**索引策略**:
- 主键: order_id
- 外键: product_id, buyer_id, seller_id
- 复合索引: (buyer_id, created_at), (seller_id, created_at), (status, created_at)
- 普通索引: status, created_at



---

### 2.5 留言表 (comments)

**用途**: 存储商品留言 (公开评论)

**关键字段**:
- `comment_id`: UUID 主键
- `product_id`: 商品 ID，外键
- `author_id`: 作者 ID，外键
- `content`: 留言内容

**索引策略**:
- 复合索引: (product_id, created_at) 用于快速获取某商品的留言


---

### 2.8 收藏表 (favorites)

**用途**: 存储用户收藏的商品

**关键字段**:
- `favorite_id`: UUID 主键
- `user_id`: 用户 ID，外键
- `product_id`: 商品 ID，外键

**设计说明**:
- 复合唯一约束: (user_id, product_id) 防止重复收藏
- 用于快速查询用户收藏列表



---

### 2.7 私信表 (messages)

**用途**: 存储用户之间的私信

**关键字段**:
- `message_id`: UUID 主键
- `sender_id`: 发送者 ID，外键
- `recipient_id`: 接收者 ID，外键
- `content`: 消息内容
- `is_read`: 是否已读
- `read_at`: 阅读时间

**索引策略**:
- 复合索引: (sender_id, recipient_id, created_at) 用于获取对话历史
- 索引: (recipient_id, is_read) 用于获取未读消息

**设计说明**:
- 支持 WebSocket 实时消息
- 保存消息历史用于查询



---

### 2.8 充值表 (recharges)

**用途**: 存储用户充值记录

**关键字段**:
- `recharge_id`: UUID 主键
- `user_id`: 用户 ID，外键
- `amount`: 充值金额
- `status`: 充值状态 (pending/completed/failed)
- `payment_method`: 支付方式 (alipay/wechat/card)
- `payment_url`: 支付链接
- `transaction_id`: 第三方交易 ID
- `completed_at`: 完成时间



---

### 2.9 用户钱包表 (user_wallets)

**用途**: 存储用户账户余额

**关键字段**:
- `wallet_id`: UUID 主键
- `user_id`: 用户 ID，外键，唯一约束 (一个用户一个钱包)
- `balance`: 账户余额
- `total_recharged`: 总充值金额
- `total_spent`: 总消费金额

**设计说明**:
- 与 users 表一一对应
- 用于快速查询用户余额
- 支持事务性操作



---

### 2.10 交易记录表 (transaction_logs)

**用途**: 记录所有交易操作，用于审计和对账

**关键字段**:
- `log_id`: UUID 主键
- `user_id`: 用户 ID，外键
- `type`: 交易类型 (recharge/purchase/refund/withdrawal)
- `amount`: 交易金额
- `balance_before`: 交易前余额
- `balance_after`: 交易后余额
- `related_id`: 关联 ID (订单 ID 或充值 ID)
- `description`: 交易描述

**设计说明**:
- 记录每一笔交易，用于对账和审计
- 不可修改，只能插入
- 用于追踪资金流向



---

## 3. 关键设计决策

### 3.1 ID 设计
- **使用 UUID**: 而不是自增 ID
  - 优点: 分布式友好，隐私保护，无序列号泄露风险
  - 缺点: 占用空间较大 (36 字符)
  - 存储: CHAR(36) 固定长度

### 3.2 时间戳设计
- **使用 TIMESTAMP**: 而不是 DATETIME
  - 自动记录创建和更新时间
  - 支持时区转换
  - 占用空间小 (4 字节)

### 3.3 金额字段设计
- **使用 DECIMAL(10, 2)**: 精确到分
  - 不使用 FLOAT/DOUBLE (精度问题)
  - 范围: 0.01 - 9999999.99

### 3.4 枚举字段设计
- **使用 ENUM**: 对于有限的固定值
  - 节省空间
  - 提高查询性能
  - 例如: status, category, condition

### 3.5 外键约束
- **使用外键**: 保证数据完整性
- **级联删除**: 删除用户时自动删除相关数据
- **级联限制**: 删除商品时限制 (防止订单孤立)

### 3.6 索引策略
- **主键索引**: 所有表都有
- **唯一索引**: username, email, (user_id, product_id)
- **复合索引**: 用于常见查询组合
- **普通索引**: 用于过滤和排序字段

---

## 4. 性能优化建议

### 4.1 查询优化
```sql
-- 获取用户的订单列表 (使用复合索引)
SELECT * FROM orders 
WHERE buyer_id = ? AND created_at > ? 
ORDER BY created_at DESC 
LIMIT 20;

-- 获取商品列表 (使用多个索引)
SELECT * FROM products 
WHERE status = 'active' AND category = ? AND campus = ? 
ORDER BY created_at DESC 
LIMIT 20;
```

### 4.2 分区建议
对于大表 (>1M 行) 考虑按时间分区:
```sql
-- 按月份分区 (messages 表)
PARTITION BY RANGE (YEAR_MONTH(created_at)) (
  PARTITION p202501 VALUES LESS THAN (202502),
  PARTITION p202502 VALUES LESS THAN (202503),
  ...
);
```

### 4.3 缓存策略
- 不缓存

### 4.4 归档策略
- 全部保留

---

## 5. 数据一致性保证

### 5.1 事务处理
关键操作需要事务:
- 下单: 检查库存 -> 创建订单 -> 更新商品状态
- 支付: 更新订单状态 -> 更新钱包余额 -> 记录交易日志
- 充值: 创建充值记录 -> 更新钱包余额 -> 记录交易日志

### 5.2 并发控制
- 使用行级锁防止并发更新
- 钱包余额更新使用悲观锁
- 订单状态更新使用乐观锁 (版本号)

### 5.3 数据验证
- 金额必须 > 0
- 评分必须在 1-5 之间
- 用户名长度 3-20 字符
- 邮箱格式验证

---

## 6. 备份和恢复

### 6.1 备份策略
- **全量备份**: 每天凌晨 2 点
- **增量备份**: 每 6 小时
- **二进制日志**: 实时记录

### 6.2 恢复流程
1. 从最近的全量备份恢复
2. 应用增量备份
3. 应用二进制日志到故障时间点

### 6.3 备份存储
- 本地存储: 7 天
- 异地存储: 30 天
- 冷存储: 1 年

---

## 7. 监控指标

### 7.1 关键指标
- 表大小和行数
- 查询响应时间
- 慢查询日志
- 连接数
- 缓存命中率

### 7.2 告警阈值
- 表大小 > 10GB: 告警
- 查询时间 > 1s: 记录慢查询
- 连接数 > 80%: 告警
- 磁盘使用 > 80%: 告警

---

## 8. 扩展建议

### 8.1 短期 (0-6 个月)
- 添加用户等级表 (VIP/普通)
- 添加商品分类表 (支持多级分类)
- 添加举报表 (举报违规商品/用户)

### 8.2 中期 (6-12 个月)
- 添加推荐系统表 (用户行为、推荐记录)
- 添加优惠券表 (优惠券、使用记录)
- 添加积分系统表 (积分、兑换记录)

### 8.3 长期 (12+ 个月)
- 分库分表 (按用户 ID 或商品 ID)
- 读写分离 (主从复制)
- 分布式事务 (跨库事务)

---

## 9. SQL 执行示例

### 9.1 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS xianyu_platform 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE xianyu_platform;
```

### 9.2 执行建表脚本
```bash
mysql -u root -p xianyu_platform < database_schema.sql
```

### 9.3 验证表结构
```sql
-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC users;
DESC products;

-- 查看索引
SHOW INDEX FROM products;

-- 查看外键
SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_NAME = 'orders' AND CONSTRAINT_NAME != 'PRIMARY';
```

---

## 10. 常见查询示例

### 10.1 获取用户的商品列表
```sql
SELECT p.* FROM products p
WHERE p.seller_id = ? AND p.status = 'active'
ORDER BY p.created_at DESC
LIMIT 20;
```

### 10.2 获取商品的评价统计
```sql
SELECT 
  AVG(r.rating) as avg_rating,
  COUNT(r.review_id) as review_count,
  SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END) as five_star_count
FROM reviews r
WHERE r.product_id = ?;
```

### 10.3 获取用户的订单统计
```sql
SELECT 
  COUNT(*) as total_orders,
  SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_orders,
  SUM(amount) as total_amount
FROM orders
WHERE buyer_id = ?;
```

### 10.4 获取热门商品
```sql
SELECT p.* FROM products p
WHERE p.status = 'active'
ORDER BY p.view_count DESC, p.created_at DESC
LIMIT 20;
```

---

## 11. 版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| v1.0 | 2025-10-16 | 初始版本，包含 12 个核心表 |
| v2.0 | 2025-10-16 | 删除评价功能 (reviews, review_images 表)，删除上下架功能，改用逻辑删除 |

---

**文档维护者**: API Team  
**最后更新**: 2025-10-16  
**下一次审查**: 2025-11-16

