# 🔍 MySQL 保留字检查报告

**检查日期**: 2025-10-17  
**项目**: SDU 二手市场 (SDU Flea Market)  
**数据库**: MySQL 8.0+

---

## ⚠️ 发现的保留字

### 1️⃣ `condition` 字段 (products 表)

**位置**: `products` 表  
**类型**: 列名  
**状态**: ⚠️ **保留字**  
**风险**: 高

```sql
`condition` VARCHAR(50) NOT NULL COMMENT '商品成色'
```

**问题**: 
- `CONDITION` 是 MySQL 保留字
- 需要使用反引号 `` ` `` 包围
- 可能导致某些 ORM 工具出现问题

**建议**: 改为 `item_condition` 或 `product_condition`

---

### 2️⃣ `status` 字段 (多个表)

**位置**: 
- `users` 表
- `products` 表
- `orders` 表

**类型**: 列名  
**状态**: ⚠️ **保留字**  
**风险**: 中

```sql
-- users 表
`status` ENUM('active', 'inactive', 'banned') DEFAULT 'active'

-- products 表
`status` INT DEFAULT 0

-- orders 表
`status` ENUM('pending_payment', 'paid', 'completed', 'cancelled')
```

**问题**:
- `STATUS` 是 MySQL 保留字
- 需要使用反引号 `` ` `` 包围
- 在某些 SQL 查询中可能需要特殊处理

**建议**: 改为 `user_status`, `product_status`, `order_status`

---

### 3️⃣ `key` 关键字 (索引名)

**位置**: 多个表的索引定义  
**类型**: 索引名  
**状态**: ✅ **安全**

```sql
KEY `idx_campus` (`campus`)
KEY `idx_created_at` (`created_at`)
```

**说明**: 
- 索引名使用了 `KEY` 关键字，但这是 SQL 语法的一部分
- 索引名本身 (`idx_campus` 等) 不是保留字
- ✅ 无需修改

---

## 📊 保留字检查清单

### 用户表 (users)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| username | VARCHAR | ❌ | ✅ 安全 |
| password_hash | VARCHAR | ❌ | ✅ 安全 |
| nickname | VARCHAR | ❌ | ✅ 安全 |
| avatar | VARCHAR | ❌ | ✅ 安全 |
| campus | VARCHAR | ❌ | ✅ 安全 |
| major | VARCHAR | ❌ | ✅ 安全 |
| phone | VARCHAR | ❌ | ✅ 安全 |
| wechat | VARCHAR | ❌ | ✅ 安全 |
| bio | TEXT | ❌ | ✅ 安全 |
| **status** | ENUM | ⚠️ | ⚠️ **保留字** |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |
| updated_at | TIMESTAMP | ❌ | ✅ 安全 |

### 商品表 (products)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| seller_id | BIGINT | ❌ | ✅ 安全 |
| title | VARCHAR | ❌ | ✅ 安全 |
| description | TEXT | ❌ | ✅ 安全 |
| category | VARCHAR | ❌ | ✅ 安全 |
| price | DECIMAL | ❌ | ✅ 安全 |
| **condition** | VARCHAR | ⚠️ | ⚠️ **保留字** |
| campus | VARCHAR | ❌ | ✅ 安全 |
| **status** | INT | ⚠️ | ⚠️ **保留字** |
| view_count | INT | ❌ | ✅ 安全 |
| is_deleted | BOOLEAN | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |
| updated_at | TIMESTAMP | ❌ | ✅ 安全 |

### 商品图片表 (product_images)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| product_id | BIGINT | ❌ | ✅ 安全 |
| image_url | VARCHAR | ❌ | ✅ 安全 |
| is_thumbnail | BOOLEAN | ❌ | ✅ 安全 |
| sort_order | INT | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |

### 订单表 (orders)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| product_id | BIGINT | ❌ | ✅ 安全 |
| buyer_id | BIGINT | ❌ | ✅ 安全 |
| seller_id | BIGINT | ❌ | ✅ 安全 |
| amount | DECIMAL | ❌ | ✅ 安全 |
| quantity | INT | ❌ | ✅ 安全 |
| **status** | ENUM | ⚠️ | ⚠️ **保留字** |
| payment_method | ENUM | ❌ | ✅ 安全 |
| paid_at | TIMESTAMP | ❌ | ✅ 安全 |
| completed_at | TIMESTAMP | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |
| updated_at | TIMESTAMP | ❌ | ✅ 安全 |

### 留言表 (comments)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| product_id | BIGINT | ❌ | ✅ 安全 |
| author_id | BIGINT | ❌ | ✅ 安全 |
| content | VARCHAR | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |
| updated_at | TIMESTAMP | ❌ | ✅ 安全 |

### 收藏表 (favorites)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| user_id | BIGINT | ❌ | ✅ 安全 |
| product_id | BIGINT | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |

### 充值表 (recharges)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| user_id | BIGINT | ❌ | ✅ 安全 |
| amount | DECIMAL | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |
| updated_at | TIMESTAMP | ❌ | ✅ 安全 |

### 用户钱包表 (user_wallets)

| 字段 | 类型 | 保留字 | 状态 |
|------|------|--------|------|
| uid | BIGINT | ❌ | ✅ 安全 |
| user_id | BIGINT | ❌ | ✅ 安全 |
| balance | DECIMAL | ❌ | ✅ 安全 |
| created_at | TIMESTAMP | ❌ | ✅ 安全 |
| updated_at | TIMESTAMP | ❌ | ✅ 安全 |

---

## 📊 保留字统计

| 保留字 | 出现次数 | 表 | 风险等级 |
|--------|---------|-----|---------|
| `condition` | 1 | products | ⚠️ 高 |
| `status` | 3 | users, products, orders | ⚠️ 中 |

**总计**: 4 个字段使用了 MySQL 保留字

---

## 🔧 修复建议

### 方案 1: 使用反引号 (当前做法)

**优点**:
- ✅ 无需修改现有代码
- ✅ 数据库兼容性好

**缺点**:
- ❌ 某些 ORM 工具可能不支持
- ❌ 代码可读性降低

**示例**:
```sql
SELECT `condition`, `status` FROM products WHERE `status` = 0;
```

### 方案 2: 重命名字段 (推荐)

**优点**:
- ✅ 完全避免保留字问题
- ✅ 代码可读性更高
- ✅ ORM 工具完全兼容

**缺点**:
- ❌ 需要修改数据库和代码

**建议的新名称**:

| 原字段名 | 建议新名称 | 说明 |
|---------|-----------|------|
| `condition` | `item_condition` | 商品成色 |
| `status` (users) | `user_status` | 用户状态 |
| `status` (products) | `product_status` | 商品状态 |
| `status` (orders) | `order_status` | 订单状态 |

---

## ✅ 当前状态

**数据库**: 使用反引号包围保留字，可以正常工作  
**代码**: 需要检查 ORM 映射是否正确处理

---

## 🎯 建议

### 短期 (当前)
- ✅ 保持现状，使用反引号
- ✅ 确保所有 SQL 查询都使用反引号

### 长期 (建议)
- 🔄 考虑重命名字段以避免保留字
- 🔄 更新数据库和代码
- 🔄 提高代码可读性和兼容性

---

**检查结果**: ⚠️ **需要注意**  
**风险等级**: 中等

虽然当前使用反引号可以正常工作，但建议长期考虑重命名这些字段以提高代码质量和兼容性。

