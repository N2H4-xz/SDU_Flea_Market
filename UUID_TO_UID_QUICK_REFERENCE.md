# UUID 到 UID 快速参考指南

## 🔄 主要变更

### 数据库

```sql
-- 旧
CREATE TABLE users (
  user_id CHAR(36) NOT NULL,
  PRIMARY KEY (user_id)
);

-- 新
CREATE TABLE users (
  uid BIGINT AUTO_INCREMENT NOT NULL,
  PRIMARY KEY (uid)
);
```

### Java 实体

```java
// 旧
@TableId(type = IdType.ASSIGN_UUID)
private String userId;

// 新
@TableId(type = IdType.AUTO)
private Long uid;
```

### 方法调用

```java
// 旧
String id = user.getUserId();
Long id = product.getProductId();

// 新
Long id = user.getUid();
Long id = product.getUid();
```

### API 端点

```bash
# 旧
GET /products/550e8400-e29b-41d4-a716-446655440000

# 新
GET /products/1
```

## 📋 受影响的文件

### 数据库
- `ducuments/database_schema.sql`

### 实体类 (10 个)
- User.java
- Product.java
- Order.java
- Comment.java
- Favorite.java
- Message.java
- Recharge.java
- ProductImage.java
- UserWallet.java
- TransactionLog.java

### VO 类 (7 个)
- ProductResponse.java
- ProductListResponse.java
- OrderResponse.java
- CommentResponse.java
- FavoriteResponse.java
- MessageResponse.java
- RechargeResponse.java

### Request 类 (2 个)
- OrderRequest.java
- FavoriteRequest.java

### Service 接口 (5 个)
- ProductService.java
- OrderService.java
- CommentService.java
- FavoriteService.java

### Service 实现 (7 个)
- UserServiceImpl.java
- ProductServiceImpl.java
- OrderServiceImpl.java
- CommentServiceImpl.java
- FavoriteServiceImpl.java
- MessageServiceImpl.java
- RechargeServiceImpl.java

### Controller (4 个)
- ProductController.java
- OrderController.java
- CommentController.java
- FavoriteController.java

## 🔍 查找和替换模式

### 在 IDE 中查找

```
// 查找所有 getProductId() 调用
getProductId()

// 查找所有 getOrderId() 调用
getOrderId()

// 查找所有 getCommentId() 调用
getCommentId()

// 查找所有 getFavoriteId() 调用
getFavoriteId()

// 查找所有 getMessageId() 调用
getMessageId()

// 查找所有 getRechargeId() 调用
getRechargeId()

// 查找所有 getImageId() 调用
getImageId()

// 查找所有 getWalletId() 调用
getWalletId()

// 查找所有 getLogId() 调用
getLogId()
```

### 替换为

```
// 替换为
getUid()
```

## 📊 类型映射

| 旧类型 | 新类型 | 说明 |
|--------|--------|------|
| String (UUID) | Long | 主键 ID |
| CHAR(36) | BIGINT | 数据库列 |
| IdType.ASSIGN_UUID | IdType.AUTO | MyBatis Plus 策略 |

## 🧪 测试用例

### 创建用户

```bash
POST /auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

# 响应中 user_id 应该是数字，如: 1, 2, 3...
```

### 创建商品

```bash
POST /products
Authorization: Bearer {token}
{
  "title": "Test Product",
  "price": 99.99,
  "category": "电子产品",
  "condition": "全新",
  "campus": "校区1"
}

# 响应中 product_id 应该是数字，如: 1, 2, 3...
```

### 获取商品详情

```bash
GET /products/1
# 使用数字 ID 而不是 UUID
```

### 创建订单

```bash
POST /orders
Authorization: Bearer {token}
{
  "product_id": 1,
  "quantity": 1,
  "payment_method": "online"
}

# product_id 应该是数字
```

## ⚠️ 常见错误

### 错误 1: 仍然使用 UUID

```java
// ❌ 错误
String productId = "550e8400-e29b-41d4-a716-446655440000";

// ✅ 正确
Long productId = 1L;
```

### 错误 2: 忘记更新方法签名

```java
// ❌ 错误
public void deleteProduct(String productId) { }

// ✅ 正确
public void deleteProduct(Long productId) { }
```

### 错误 3: 混合使用 String 和 Long

```java
// ❌ 错误
Long productId = Long.parseLong(request.getProduct_id()); // 如果 request 中是 String

// ✅ 正确
// 确保 request 中的 product_id 已经是 Long 类型
Long productId = request.getProduct_id();
```

### 错误 4: 忘记更新 VO 类

```java
// ❌ 错误
private String product_id;

// ✅ 正确
private Long product_id;
```

## 📝 检查清单

- [ ] 所有数据库表已更新
- [ ] 所有 PO 类已更新
- [ ] 所有 VO 类已更新
- [ ] 所有 Request 类已更新
- [ ] 所有 Service 接口已更新
- [ ] 所有 Service 实现已更新
- [ ] 所有 Controller 已更新
- [ ] 代码已编译
- [ ] 没有编译错误
- [ ] 没有 IDE 诊断错误
- [ ] 测试用例已通过
- [ ] 文档已更新

## 🚀 快速启动

```bash
# 1. 备份数据库
mysqldump -u root -p sdu_flea_market > backup.sql

# 2. 执行迁移
mysql -u root -p sdu_flea_market < ducuments/database_schema.sql

# 3. 编译
mvn clean compile

# 4. 启动
mvn spring-boot:run

# 5. 测试
curl http://localhost:8081/products
```

## 📞 需要帮助？

查看以下文档：
- `UUID_TO_UID_REFACTORING_SUMMARY.md` - 完整总结
- `UUID_TO_UID_MIGRATION_GUIDE.md` - 迁移指南
- `UUID_TO_UID_VERIFICATION_REPORT.md` - 验证报告

