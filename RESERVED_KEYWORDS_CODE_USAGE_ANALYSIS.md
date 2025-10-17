# 📊 MySQL 保留字 - 代码使用情况分析

**分析日期**: 2025-10-17  
**项目**: SDU 二手市场 (SDU Flea Market)

---

## 🔍 保留字使用情况

### 1️⃣ `condition` 字段 (商品成色)

**数据库**: `products` 表  
**Java 字段**: `Product.condition`  
**类型**: String

#### 使用位置

| 文件 | 位置 | 用途 | 状态 |
|------|------|------|------|
| Product.java | 第 25 行 | 实体字段定义 | ✅ |
| ProductResponse.java | 第 18 行 | VO 字段定义 | ✅ |
| ProductListResponse.java | 第 ? 行 | VO 字段定义 | ✅ |
| ProductServiceImpl.java | 第 106 行 | 方法参数 | ✅ |
| ProductServiceImpl.java | 第 124-125 行 | 查询条件 | ✅ |
| ProductServiceImpl.java | 第 147 行 | 字段赋值 | ✅ |
| ProductController.java | 第 42 行 | 请求参数 | ✅ |

#### 代码示例

**Product 实体**:
```java
private String condition;  // 商品成色
```

**ProductServiceImpl 查询**:
```java
if (condition != null && !condition.isEmpty()) {
    wrapper.eq("condition", condition);  // ⚠️ 使用保留字
}
```

**ProductListResponse 映射**:
```java
item.setCondition(product.getCondition());
```

#### 风险评估

- ⚠️ **风险等级**: 高
- ⚠️ **问题**: 在 SQL 查询中使用保留字
- ✅ **当前状态**: MyBatis Plus 自动处理，可正常工作
- ⚠️ **潜在问题**: 某些 ORM 工具或原生 SQL 可能出现问题

---

### 2️⃣ `status` 字段 (状态)

#### 2.1 User 表中的 status

**数据库**: `users` 表  
**Java 字段**: `User.status`  
**类型**: String  
**值**: 'active', 'inactive', 'banned'

**使用位置**:
- User.java (第 28 行) - 实体字段
- UserServiceImpl.java - 设置用户状态

**代码示例**:
```java
user.setStatus("active");  // 设置用户状态
```

#### 2.2 Product 表中的 status

**数据库**: `products` 表  
**Java 字段**: `Product.status`  
**类型**: Integer  
**值**: 0=active, 1=sold, 2=inactive, 3=deleted, 4=reserved

**使用位置**:
- Product.java (第 27 行) - 实体字段
- ProductResponse.java (第 23 行) - VO 字段
- ProductListResponse.java - VO 字段
- ProductServiceImpl.java (第 109 行) - 查询条件
- ProductServiceImpl.java (第 44 行) - 设置状态

**代码示例**:
```java
// 查询条件
wrapper.eq("status", "active");  // ⚠️ 使用保留字

// 设置状态
product.setStatus(0);  // 0=active
```

#### 2.3 Order 表中的 status

**数据库**: `orders` 表  
**Java 字段**: `Order.status`  
**类型**: String  
**值**: 'pending_payment', 'paid', 'completed', 'cancelled'

**使用位置**:
- Order.java (第 24 行) - 实体字段
- OrderResponse.java - VO 字段
- OrderServiceImpl.java (第 85 行) - 查询条件
- OrderServiceImpl.java (第 141 行) - 设置状态

**代码示例**:
```java
// 查询条件
if (status != null && !status.isEmpty()) {
    wrapper.eq("status", status);  // ⚠️ 使用保留字
}

// 设置状态
order.setStatus("pending_payment");
```

---

## 📊 保留字使用统计

| 保留字 | 表 | 字段类型 | 使用次数 | 风险 |
|--------|-----|---------|---------|------|
| `condition` | products | String | 7 | ⚠️ 高 |
| `status` | users | String | 2 | ⚠️ 中 |
| `status` | products | Integer | 5 | ⚠️ 中 |
| `status` | orders | String | 4 | ⚠️ 中 |

**总计**: 18 处使用保留字

---

## ✅ 当前工作状态

### MyBatis Plus 处理

MyBatis Plus 会自动为保留字添加反引号，所以当前代码可以正常工作：

```sql
-- MyBatis Plus 自动转换
SELECT `condition`, `status` FROM products WHERE `status` = 0;
```

### 代码兼容性

| 工具/框架 | 兼容性 | 说明 |
|----------|--------|------|
| MyBatis Plus | ✅ 完全兼容 | 自动处理反引号 |
| Spring Data JPA | ⚠️ 可能有问题 | 需要特殊配置 |
| 原生 SQL | ❌ 需要反引号 | 必须手动添加 |
| 某些 IDE | ⚠️ 可能警告 | 代码检查可能报错 |

---

## 🔧 修复方案

### 方案 1: 保持现状 (当前)

**优点**:
- ✅ 无需修改代码
- ✅ 无需修改数据库
- ✅ MyBatis Plus 自动处理

**缺点**:
- ❌ 代码可读性差
- ❌ 某些工具可能不兼容
- ❌ 不符合最佳实践

### 方案 2: 重命名字段 (推荐)

**建议的新名称**:

| 原字段 | 新字段 | 表 |
|--------|--------|-----|
| `condition` | `item_condition` | products |
| `status` | `user_status` | users |
| `status` | `product_status` | products |
| `status` | `order_status` | orders |

**修改范围**:

1. **数据库修改**:
   ```sql
   ALTER TABLE products CHANGE COLUMN `condition` `item_condition` VARCHAR(50);
   ALTER TABLE users CHANGE COLUMN `status` `user_status` ENUM(...);
   ALTER TABLE products CHANGE COLUMN `status` `product_status` INT;
   ALTER TABLE orders CHANGE COLUMN `status` `order_status` ENUM(...);
   ```

2. **Java 实体修改**:
   - Product.java: `condition` → `itemCondition`
   - User.java: `status` → `userStatus`
   - Product.java: `status` → `productStatus`
   - Order.java: `status` → `orderStatus`

3. **VO 类修改**:
   - ProductResponse.java
   - ProductListResponse.java
   - OrderResponse.java
   - 等等

4. **Service 实现修改**:
   - ProductServiceImpl.java
   - UserServiceImpl.java
   - OrderServiceImpl.java
   - 等等

5. **Controller 修改**:
   - ProductController.java
   - OrderController.java
   - 等等

**工作量**: 中等 (约 30-40 个文件需要修改)

---

## 📋 修改清单 (如果选择方案 2)

### 数据库修改
- [ ] 修改 products 表: `condition` → `item_condition`
- [ ] 修改 users 表: `status` → `user_status`
- [ ] 修改 products 表: `status` → `product_status`
- [ ] 修改 orders 表: `status` → `order_status`

### Java 实体修改
- [ ] Product.java: 修改 condition 和 status 字段
- [ ] User.java: 修改 status 字段
- [ ] Order.java: 修改 status 字段

### VO 类修改
- [ ] ProductResponse.java
- [ ] ProductListResponse.java
- [ ] OrderResponse.java
- [ ] UserProfileResponse.java
- [ ] LoginResponse.java

### Service 修改
- [ ] ProductServiceImpl.java
- [ ] UserServiceImpl.java
- [ ] OrderServiceImpl.java
- [ ] CommentServiceImpl.java
- [ ] FavoriteServiceImpl.java
- [ ] RechargeServiceImpl.java

### Controller 修改
- [ ] ProductController.java
- [ ] OrderController.java
- [ ] UserController.java

---

## 🎯 建议

### 短期 (当前)
- ✅ 保持现状，使用 MyBatis Plus 自动处理
- ✅ 代码可以正常工作
- ✅ 无需立即修改

### 长期 (建议)
- 🔄 考虑重命名字段以避免保留字
- 🔄 提高代码质量和可维护性
- 🔄 增强与其他工具的兼容性

---

## ✅ 总结

| 项目 | 状态 |
|------|------|
| 当前功能 | ✅ 正常工作 |
| 代码兼容性 | ⚠️ 需要注意 |
| 最佳实践 | ❌ 不符合 |
| 建议行动 | 🔄 长期考虑重命名 |

**风险等级**: 中等  
**紧急程度**: 低 (当前可正常工作)  
**优先级**: 低 (可作为后续优化项)

