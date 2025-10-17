# 快速参考指南

## 🎯 三大改动总结

### 1️⃣ 用户 ID 改为自增 Long

**之前：** `String userId` (UUID)  
**现在：** `Long userId` (自增)

**影响范围：**
- 所有实体类的 userId 相关字段
- 所有 VO 类的 user_id 相关字段
- 所有 Service 方法的 userId 参数

**示例：**
```java
// 之前
private String userId;

// 现在
@TableId(type = IdType.AUTO)
private Long userId;
```

---

### 2️⃣ 登录方式改为用户名登录

**之前：** 仅支持邮箱登录  
**现在：** 支持用户名或邮箱登录

**LoginRequest 变化：**
```java
// 之前
private String email;
private String password;

// 现在
private String username;  // 新增
private String email;
private String password;
```

**登录逻辑：**
```java
// 优先使用用户名，其次使用邮箱
if (username != null && !username.isEmpty()) {
    // 用用户名查询
} else if (email != null && !email.isEmpty()) {
    // 用邮箱查询
}
```

---

### 3️⃣ 移除 try-catch，使用全局异常处理

**之前：** 每个方法都有 try-catch  
**现在：** 异常由 GlobalExceptionHandler 处理

**代码对比：**
```java
// 之前
@PostMapping("/login")
public ResponseEntity<Result> login(@RequestBody LoginRequest request) {
    try {
        LoginResponse response = userService.login(request.getEmail(), request.getPassword());
        return Result.success(response, "登录成功");
    } catch (Exception e) {
        return Result.error(401, e.getMessage());
    }
}

// 现在
@PostMapping("/login")
public ResponseEntity<Result> login(@RequestBody LoginRequest request) {
    LoginResponse response = userService.login(request.getUsername(), request.getEmail(), request.getPassword());
    return Result.success(response, "登录成功");
}
```

---

## 📝 修改清单

### 实体类 (7 个)
- [ ] User.java - userId: String → Long
- [ ] Product.java - sellerId: String → Long
- [ ] Order.java - buyerId, sellerId: String → Long
- [ ] Comment.java - authorId: String → Long
- [ ] Favorite.java - userId: String → Long
- [ ] Message.java - senderId, recipientId: String → Long
- [ ] Recharge.java - userId: String → Long

### VO 类 (7 个)
- [ ] LoginResponse.java
- [ ] UserProfileResponse.java
- [ ] CommentResponse.java
- [ ] ProductResponse.java
- [ ] ProductListResponse.java
- [ ] OrderResponse.java
- [ ] MessageResponse.java

### Controller (8 个)
- [ ] AuthController.java
- [ ] UserController.java
- [ ] ProductController.java
- [ ] OrderController.java
- [ ] CommentController.java
- [ ] FavoriteController.java
- [ ] MessageController.java
- [ ] RechargeController.java

### Service (7 个)
- [ ] UserService.java & UserServiceImpl.java
- [ ] ProductService.java & ProductServiceImpl.java
- [ ] OrderService.java & OrderServiceImpl.java
- [ ] CommentService.java & CommentServiceImpl.java
- [ ] FavoriteService.java & FavoriteServiceImpl.java
- [ ] MessageService.java & MessageServiceImpl.java
- [ ] RechargeService.java & RechargeServiceImpl.java

---

## 🔍 关键方法签名变化

### UserService
```java
// 之前
LoginResponse login(String email, String password);
User getUserById(String userId);
UserProfileResponse getProfile(String userId);
void logout(String token);

// 现在
LoginResponse login(String username, String email, String password);
User getUserById(Long userId);
UserProfileResponse getProfile(Long userId);
void logout(Long userId);
```

### OrderService
```java
// 之前
Order createOrder(String buyerId, OrderRequest request);
PageResponse<OrderResponse> listOrders(String userId, ...);

// 现在
Order createOrder(Long buyerId, OrderRequest request);
PageResponse<OrderResponse> listOrders(Long userId, ...);
```

---

## 🚀 部署步骤

1. **编译项目**
   ```bash
   mvn clean install
   ```

2. **备份数据库**
   ```bash
   mysqldump -u root -p sdu_flea_market > backup.sql
   ```

3. **执行数据库迁移**
   ```bash
   mysql -u root -p sdu_flea_market < DATABASE_MIGRATION.sql
   ```

4. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

5. **测试登录**
   - 使用用户名登录
   - 使用邮箱登录
   - 验证所有端点

---

## ✅ 验证清单

- [ ] 编译无错误
- [ ] 所有单元测试通过
- [ ] 数据库迁移成功
- [ ] 用户名登录功能正常
- [ ] 邮箱登录功能正常
- [ ] 所有 API 端点正常
- [ ] 异常处理正常
- [ ] 性能测试通过

---

## 📞 常见问题

**Q: 旧数据怎么办？**  
A: 使用 DATABASE_MIGRATION.sql 脚本自动迁移，保留所有数据。

**Q: 能同时支持用户名和邮箱登录吗？**  
A: 可以，优先使用用户名，如果没有提供用户名则使用邮箱。

**Q: 为什么要改为 Long 类型？**  
A: 性能更好，占用空间更小，数据库查询更快。

**Q: 异常处理改变了吗？**  
A: 是的，现在所有异常由 GlobalExceptionHandler 统一处理。

