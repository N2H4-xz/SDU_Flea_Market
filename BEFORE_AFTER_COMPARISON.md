# 修改前后对比

## 📊 代码对比示例

### 示例 1：AuthController - logout 方法

#### ❌ 修改前
```java
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@PostMapping("/logout")
public ResponseEntity<Result> logout(HttpServletRequest request) {
    try {
        // 手动提取 token
        String token = TokenUtil.extractToken(request);
        // 调用服务进行登出
        userService.logout(token);
        return ResponseUtil.build(Result.ok());
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
```

#### ✅ 修改后
```java
import org.stnhh.sdu_flea_market.annotation.Auth;

@Auth  // ← 添加注解
@PostMapping("/logout")
public ResponseEntity<Result> logout(HttpServletRequest request) {
    try {
        // 从请求属性中获取userId（由AuthAspect设置）
        String userId = (String) request.getAttribute("userId");
        // 调用服务进行登出
        userService.logout(userId);
        return ResponseUtil.build(Result.ok());
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
```

**改进：**
- ✅ 添加 @Auth 注解
- ✅ 移除手动 token 提取
- ✅ 从 request 属性获取 userId
- ✅ 代码更简洁

---

### 示例 2：UserController - getProfile 方法

#### ❌ 修改前
```java
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@GetMapping("/profile")
public ResponseEntity<Result> getProfile(HttpServletRequest request) {
    try {
        // 从请求头中提取JWT令牌
        String token = TokenUtil.extractToken(request);
        String userId = extractUserIdFromToken(token);

        // 获取用户个人资料
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseUtil.build(Result.success(profile, "获取成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}

private String extractUserIdFromToken(String token) {
    // 使用TokenUtil中的方法提取用户ID
    return TokenUtil.extractUserIdFromToken(token);
}
```

#### ✅ 修改后
```java
import org.stnhh.sdu_flea_market.annotation.Auth;

@Auth  // ← 添加注解
@GetMapping("/profile")
public ResponseEntity<Result> getProfile(HttpServletRequest request) {
    try {
        // 从请求属性中获取userId（由AuthAspect设置）
        String userId = (String) request.getAttribute("userId");

        // 获取用户个人资料
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseUtil.build(Result.success(profile, "获取成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
// ← 移除 extractUserIdFromToken() 方法
```

**改进：**
- ✅ 添加 @Auth 注解
- ✅ 移除 extractUserIdFromToken() 方法
- ✅ 代码行数减少 5 行
- ✅ 逻辑更清晰

---

### 示例 3：ProductController - createProduct 方法

#### ❌ 修改前
```java
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@PostMapping
public ResponseEntity<Result> createProduct(@RequestBody ProductRequest request, HttpServletRequest httpRequest) {
    try {
        // 从请求头中提取JWT令牌
        String token = TokenUtil.extractToken(httpRequest);
        String sellerId = extractUserIdFromToken(token);

        // 创建商品
        Product product = productService.createProduct(sellerId, request);
        return ResponseUtil.build(Result.success(product, "商品发布成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}

private String extractUserIdFromToken(String token) {
    return TokenUtil.extractUserIdFromToken(token);
}
```

#### ✅ 修改后
```java
import org.stnhh.sdu_flea_market.annotation.Auth;

@Auth  // ← 添加注解
@PostMapping
public ResponseEntity<Result> createProduct(@RequestBody ProductRequest request, HttpServletRequest httpRequest) {
    try {
        // 从请求属性中获取userId（由AuthAspect设置）
        String sellerId = (String) httpRequest.getAttribute("userId");

        // 创建商品
        Product product = productService.createProduct(sellerId, request);
        return ResponseUtil.build(Result.success(product, "商品发布成功"));
    } catch (Exception e) {
        return ResponseUtil.build(Result.error(400, e.getMessage()));
    }
}
// ← 移除 extractUserIdFromToken() 方法
```

**改进：**
- ✅ 添加 @Auth 注解
- ✅ 移除手动 token 提取逻辑
- ✅ 代码行数减少 5 行

---

## 📈 整体改进

### 代码行数对比

| 指标 | 修改前 | 修改后 | 变化 |
|------|--------|--------|------|
| 总代码行数 | ~850 | ~790 | -60 |
| extractUserIdFromToken 方法 | 8 个 | 0 个 | -8 |
| TokenUtil 导入 | 8 个 | 0 个 | -8 |
| @Auth 注解 | 0 个 | 19 个 | +19 |
| Auth 导入 | 0 个 | 8 个 | +8 |

### 复杂度对比

| 指标 | 修改前 | 修改后 |
|------|--------|--------|
| 认证逻辑位置 | 8 个地方 | 1 个地方 (AuthAspect) |
| 重复代码 | 是 | 否 |
| 易于维护 | 困难 | 容易 |
| 错误处理 | 分散 | 统一 |

---

## 🎯 关键改进点

### 1. 代码复用
**修改前：** 每个 controller 都有 extractUserIdFromToken() 方法  
**修改后：** 认证逻辑集中在 AuthAspect

### 2. 清晰的意图
**修改前：** 需要查看方法体才能知道是否需要认证  
**修改后：** @Auth 注解明确表示需要认证

### 3. 易于维护
**修改前：** 修改认证逻辑需要改 8 个地方  
**修改后：** 只需修改 AuthAspect

### 4. 错误处理
**修改前：** 每个方法都有 try-catch  
**修改后：** 认证错误由 AuthAspect 统一处理

### 5. 代码质量
**修改前：** 代码重复，难以维护  
**修改后：** DRY 原则，易于维护

---

## 🔄 工作流程对比

### 修改前的流程
```
1. 客户端发送请求
2. Controller 方法接收请求
3. 手动调用 TokenUtil.extractToken()
4. 手动调用 extractUserIdFromToken()
5. 执行业务逻辑
6. 返回响应
```

### 修改后的流程
```
1. 客户端发送请求
2. Spring 检测到 @Auth 注解
3. AuthAspect 自动拦截
4. 自动提取和验证 token
5. 自动将 userId 存储在 request 属性
6. Controller 方法接收请求
7. 从 request 属性获取 userId
8. 执行业务逻辑
9. 返回响应
```

---

## ✨ 最终效果

✅ **代码更简洁** - 减少了 ~60 行代码  
✅ **易于维护** - 认证逻辑集中在一个地方  
✅ **清晰的意图** - @Auth 注解明确表示需要认证  
✅ **错误处理统一** - 所有认证错误由 AuthAspect 处理  
✅ **遵循 DRY 原则** - 没有重复代码  
✅ **遵循 SOLID 原则** - 单一职责原则

---

**修改完成时间：** 2025-10-17

