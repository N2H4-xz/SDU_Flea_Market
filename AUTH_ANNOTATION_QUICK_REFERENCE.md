# @Auth 注解快速参考指南

## 📌 核心概念

### @Auth 注解
- **位置：** `org.stnhh.sdu_flea_market.annotation.Auth`
- **作用：** 标记需要 JWT token 认证的方法
- **运行时处理：** 由 `AuthAspect` 在方法执行前拦截处理

### AuthAspect
- **位置：** `org.stnhh.sdu_flea_market.aspect.AuthAspect`
- **作用：** 拦截所有带 @Auth 注解的方法，进行 token 验证
- **流程：**
  1. 从 Authorization 请求头提取 token
  2. 验证 token 有效性
  3. 解析 token 获取 userId
  4. 将 userId 存储在 request 属性中
  5. 继续执行目标方法

---

## 🔧 使用方法

### 步骤 1：导入注解
```java
import org.stnhh.sdu_flea_market.annotation.Auth;
```

### 步骤 2：在方法上添加注解
```java
@Auth
@PostMapping("/logout")
public ResponseEntity<Result> logout(HttpServletRequest request) {
    // 方法体
}
```

### 步骤 3：从 request 属性获取 userId
```java
String userId = (String) request.getAttribute("userId");
```

---

## 📋 完整示例

### 示例 1：简单的认证方法
```java
@Auth
@GetMapping("/profile")
public ResponseEntity<Result> getProfile(HttpServletRequest request) {
    try {
        String userId = (String) request.getAttribute("userId");
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseUtil.build(Result.success(profile, "获取成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
```

### 示例 2：带路径参数的认证方法
```java
@Auth
@PutMapping("/{productId}")
public ResponseEntity<Result> updateProduct(
        @PathVariable String productId,
        @RequestBody ProductRequest request,
        HttpServletRequest httpRequest) {
    try {
        String sellerId = (String) httpRequest.getAttribute("userId");
        ProductResponse response = productService.updateProduct(productId, sellerId, request);
        return ResponseUtil.build(Result.success(response, "更新成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
```

### 示例 3：带查询参数的认证方法
```java
@Auth
@GetMapping
public ResponseEntity<Result> listOrders(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer limit,
        HttpServletRequest httpRequest) {
    try {
        String userId = (String) httpRequest.getAttribute("userId");
        PageResponse<OrderResponse> response = orderService.listOrders(userId, page, limit, null, "buyer");
        return ResponseUtil.build(Result.success(response, "获取成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
```

---

## ⚠️ 常见错误

### ❌ 错误 1：忘记导入注解
```java
// 错误
@PostMapping("/logout")
public ResponseEntity<Result> logout(HttpServletRequest request) {
    // ...
}

// 正确
import org.stnhh.sdu_flea_market.annotation.Auth;

@Auth
@PostMapping("/logout")
public ResponseEntity<Result> logout(HttpServletRequest request) {
    // ...
}
```

### ❌ 错误 2：手动提取 token
```java
// 错误 - 不需要这样做
String token = TokenUtil.extractToken(request);
String userId = TokenUtil.extractUserIdFromToken(token);

// 正确 - 直接从 request 属性获取
String userId = (String) request.getAttribute("userId");
```

### ❌ 错误 3：忘记转换类型
```java
// 错误
String userId = request.getAttribute("userId");  // 返回 Object

// 正确
String userId = (String) request.getAttribute("userId");
```

---

## 🔐 认证流程图

```
请求到达
    ↓
Spring 检测到 @Auth 注解
    ↓
AuthAspect 拦截
    ↓
从 Authorization 头提取 token
    ↓
验证 token 有效性
    ↓
解析 token 获取 userId
    ↓
将 userId 存储在 request.setAttribute("userId", userId)
    ↓
继续执行目标方法
    ↓
方法从 request.getAttribute("userId") 获取 userId
    ↓
返回响应
```

---

## 📊 已修改的 Controller 方法

| Controller | 方法 | 状态 |
|-----------|------|------|
| AuthController | logout | ✅ |
| AuthController | changePassword | ✅ |
| UserController | getProfile | ✅ |
| UserController | updateProfile | ✅ |
| ProductController | createProduct | ✅ |
| ProductController | updateProduct | ✅ |
| ProductController | deleteProduct | ✅ |
| OrderController | createOrder | ✅ |
| OrderController | listOrders | ✅ |
| OrderController | getOrderDetail | ✅ |
| OrderController | updateOrderStatus | ✅ |
| CommentController | createComment | ✅ |
| CommentController | deleteComment | ✅ |
| FavoriteController | addFavorite | ✅ |
| FavoriteController | removeFavorite | ✅ |
| FavoriteController | listFavorites | ✅ |
| MessageController | getMessageHistory | ✅ |
| RechargeController | createRecharge | ✅ |
| RechargeController | getRechargeHistory | ✅ |

---

## 🧪 测试 Token

### 获取 Token
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 使用 Token 调用受保护的端点
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8081/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📚 相关文件

- `Auth.java` - 注解定义
- `AuthAspect.java` - AOP 切面实现
- `JWTUtil.java` - JWT 工具类
- `TokenUtil.java` - Token 提取工具
- 所有 Controller 文件

---

**最后更新：** 2025-10-17

