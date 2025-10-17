-- ============================================================
-- SDU 二手市场 - 数据库迁移脚本
-- 将用户 ID 从 UUID (CHAR(36)) 改为自增 Long (BIGINT)
-- ============================================================

-- 注意：执行此脚本前请备份数据库！

-- ============================================================
-- 第一步：创建临时表和新表
-- ============================================================

-- 1. 创建新的 users 表（使用 BIGINT AUTO_INCREMENT）
CREATE TABLE users_new (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    nickname VARCHAR(50),
    campus VARCHAR(50),
    major VARCHAR(100),
    phone VARCHAR(20),
    wechat VARCHAR(50),
    bio TEXT,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 复制数据到新表（保留原有的 UUID 作为参考，但使用新的自增 ID）
INSERT INTO users_new (username, email, password_hash, avatar, nickname, campus, major, phone, wechat, bio, status, created_at, updated_at)
SELECT username, email, password_hash, avatar, nickname, campus, major, phone, wechat, bio, status, created_at, updated_at
FROM users
ORDER BY created_at ASC;

-- ============================================================
-- 第二步：更新关联表的外键
-- ============================================================

-- 创建映射表（用于保存旧 UUID 和新 ID 的对应关系）
CREATE TABLE user_id_mapping (
    old_user_id CHAR(36),
    new_user_id BIGINT,
    PRIMARY KEY (old_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 填充映射表
INSERT INTO user_id_mapping (old_user_id, new_user_id)
SELECT u.user_id, un.user_id
FROM users u
JOIN users_new un ON u.username = un.username;

-- 3. 更新 products 表
ALTER TABLE products ADD COLUMN seller_id_new BIGINT;
UPDATE products p
JOIN user_id_mapping m ON p.seller_id = m.old_user_id
SET p.seller_id_new = m.new_user_id;
ALTER TABLE products DROP COLUMN seller_id;
ALTER TABLE products RENAME COLUMN seller_id_new TO seller_id;
ALTER TABLE products ADD CONSTRAINT fk_products_seller FOREIGN KEY (seller_id) REFERENCES users_new(user_id);

-- 4. 更新 orders 表
ALTER TABLE orders ADD COLUMN buyer_id_new BIGINT, ADD COLUMN seller_id_new BIGINT;
UPDATE orders o
JOIN user_id_mapping m ON o.buyer_id = m.old_user_id
SET o.buyer_id_new = m.new_user_id;
UPDATE orders o
JOIN user_id_mapping m ON o.seller_id = m.old_user_id
SET o.seller_id_new = m.new_user_id;
ALTER TABLE orders DROP COLUMN buyer_id, DROP COLUMN seller_id;
ALTER TABLE orders RENAME COLUMN buyer_id_new TO buyer_id, RENAME COLUMN seller_id_new TO seller_id;
ALTER TABLE orders ADD CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users_new(user_id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_seller FOREIGN KEY (seller_id) REFERENCES users_new(user_id);

-- 5. 更新 comments 表
ALTER TABLE comments ADD COLUMN author_id_new BIGINT;
UPDATE comments c
JOIN user_id_mapping m ON c.author_id = m.old_user_id
SET c.author_id_new = m.new_user_id;
ALTER TABLE comments DROP COLUMN author_id;
ALTER TABLE comments RENAME COLUMN author_id_new TO author_id;
ALTER TABLE comments ADD CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users_new(user_id);

-- 6. 更新 favorites 表
ALTER TABLE favorites ADD COLUMN user_id_new BIGINT;
UPDATE favorites f
JOIN user_id_mapping m ON f.user_id = m.old_user_id
SET f.user_id_new = m.new_user_id;
ALTER TABLE favorites DROP COLUMN user_id;
ALTER TABLE favorites RENAME COLUMN user_id_new TO user_id;
ALTER TABLE favorites ADD CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users_new(user_id);

-- 7. 更新 messages 表
ALTER TABLE messages ADD COLUMN sender_id_new BIGINT, ADD COLUMN recipient_id_new BIGINT;
UPDATE messages m
JOIN user_id_mapping mp ON m.sender_id = mp.old_user_id
SET m.sender_id_new = mp.new_user_id;
UPDATE messages m
JOIN user_id_mapping mp ON m.recipient_id = mp.old_user_id
SET m.recipient_id_new = mp.new_user_id;
ALTER TABLE messages DROP COLUMN sender_id, DROP COLUMN recipient_id;
ALTER TABLE messages RENAME COLUMN sender_id_new TO sender_id, RENAME COLUMN recipient_id_new TO recipient_id;
ALTER TABLE messages ADD CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users_new(user_id);
ALTER TABLE messages ADD CONSTRAINT fk_messages_recipient FOREIGN KEY (recipient_id) REFERENCES users_new(user_id);

-- 8. 更新 recharges 表
ALTER TABLE recharges ADD COLUMN user_id_new BIGINT;
UPDATE recharges r
JOIN user_id_mapping m ON r.user_id = m.old_user_id
SET r.user_id_new = m.new_user_id;
ALTER TABLE recharges DROP COLUMN user_id;
ALTER TABLE recharges RENAME COLUMN user_id_new TO user_id;
ALTER TABLE recharges ADD CONSTRAINT fk_recharges_user FOREIGN KEY (user_id) REFERENCES users_new(user_id);

-- ============================================================
-- 第三步：替换原表
-- ============================================================

-- 删除原表
DROP TABLE users;

-- 重命名新表
RENAME TABLE users_new TO users;

-- 删除映射表
DROP TABLE user_id_mapping;

-- ============================================================
-- 完成
-- ============================================================

-- 验证迁移结果
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_products FROM products;
SELECT COUNT(*) as total_orders FROM orders;
SELECT COUNT(*) as total_comments FROM comments;
SELECT COUNT(*) as total_favorites FROM favorites;
SELECT COUNT(*) as total_messages FROM messages;
SELECT COUNT(*) as total_recharges FROM recharges;

-- 显示用户表结构
DESCRIBE users;

