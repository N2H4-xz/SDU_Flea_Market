# 测试建议

## 🧪 单元测试

### 1. UserService 测试

```java
@Test
public void testLoginWithUsername() {
    // 测试用用户名登录
    LoginResponse response = userService.login("testuser", null, "password123");
    assertNotNull(response);
    assertNotNull(response.getToken());
}

@Test
public void testLoginWithEmail() {
    // 测试用邮箱登录
    LoginResponse response = userService.login(null, "test@example.com", "password123");
    assertNotNull(response);
    assertNotNull(response.getToken());
}

@Test
public void testLoginWithInvalidCredentials() {
    // 测试无效凭证
    assertThrows(RuntimeException.class, () -> {
        userService.login("testuser", null, "wrongpassword");
    });
}

@Test
public void testGetProfileWithLongUserId() {
    // 测试获取用户资料（使用 Long userId）
    UserProfileResponse profile = userService.getProfile(1L);
    assertNotNull(profile);
    assertEquals(1L, profile.getUser_id());
}
```

### 2. ProductService 测试

```java
@Test
public void testCreateProductWithLongSellerId() {
    // 测试创建商品（使用 Long sellerId）
    ProductRequest request = new ProductRequest();
    request.setTitle("Test Product");
    request.setPrice(new BigDecimal("99.99"));
    
    Product product = productService.createProduct(1L, request);
    assertNotNull(product);
    assertEquals(1L, product.getSellerId());
}
```

### 3. OrderService 测试

```java
@Test
public void testCreateOrderWithLongBuyerId() {
    // 测试创建订单（使用 Long buyerId）
    OrderRequest request = new OrderRequest();
    request.setProduct_id("product123");
    
    Order order = orderService.createOrder(1L, request);
    assertNotNull(order);
    assertEquals(1L, order.getBuyerId());
}
```

---

## 🔗 集成测试

### 1. 登录流程测试

```bash
# 测试用户名登录
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 预期响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "user_id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "token": "eyJhbGc...",
    "expires_in": 86400
  }
}
```

### 2. 邮箱登录测试

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 3. 获取用户资料测试

```bash
curl -X GET http://localhost:8081/users/profile \
  -H "Authorization: Bearer <token>"

# 预期响应
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "user_id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "Test User",
    ...
  }
}
```

### 4. 创建商品测试

```bash
curl -X POST http://localhost:8081/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "iPhone 13",
    "description": "Used iPhone 13",
    "price": 5000,
    "category": "电子产品",
    "condition": "九成新",
    "campus": "中心校区"
  }'
```

---

## 🔍 异常处理测试

### 1. 测试全局异常处理

```bash
# 测试无效的 token
curl -X GET http://localhost:8081/users/profile \
  -H "Authorization: Bearer invalid_token"

# 预期响应（由 GlobalExceptionHandler 处理）
{
  "code": 403,
  "message": "Token 无效，请检查后重试"
}
```

### 2. 测试缺少必要参数

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "password": "password123"
  }'

# 预期响应
{
  "code": 400,
  "message": "用户名或邮箱不能为空"
}
```

---

## 📊 性能测试

### 1. 数据库查询性能

```sql
-- 测试用户 ID 查询性能
-- UUID 查询（旧方式）
SELECT * FROM users WHERE user_id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890';

-- Long 查询（新方式）
SELECT * FROM users WHERE user_id = 1;

-- 新方式应该更快
```

### 2. 并发测试

```bash
# 使用 Apache Bench 进行并发测试
ab -n 1000 -c 100 http://localhost:8081/products

# 预期：响应时间 < 100ms
```

---

## ✅ 测试清单

### 功能测试
- [ ] 用户名登录
- [ ] 邮箱登录
- [ ] 用户名和邮箱都为空时的错误处理
- [ ] 密码错误时的错误处理
- [ ] 获取用户资料
- [ ] 更新用户资料
- [ ] 修改密码
- [ ] 登出

### 商品相关
- [ ] 创建商品
- [ ] 获取商品列表
- [ ] 获取商品详情
- [ ] 更新商品
- [ ] 删除商品

### 订单相关
- [ ] 创建订单
- [ ] 获取订单列表
- [ ] 获取订单详情
- [ ] 更新订单状态

### 异常处理
- [ ] 无效 token
- [ ] 过期 token
- [ ] 缺少必要参数
- [ ] 资源不存在
- [ ] 权限不足

### 性能测试
- [ ] 单用户响应时间 < 100ms
- [ ] 并发 100 用户响应时间 < 500ms
- [ ] 数据库查询时间 < 10ms

---

## 🐛 常见问题排查

### 问题 1: Token 验证失败
**症状：** 所有需要认证的请求都返回 403  
**排查步骤：**
1. 检查 token 是否正确
2. 检查 token 是否过期
3. 检查 AuthAspect 是否正确配置
4. 检查 JWTUtil 的 SECRET_KEY 是否一致

### 问题 2: 用户名登录不工作
**症状：** 用户名登录返回"用户名或邮箱错误"  
**排查步骤：**
1. 检查用户名是否存在
2. 检查密码是否正确
3. 检查 UserServiceImpl.login() 方法逻辑
4. 检查数据库中的用户数据

### 问题 3: 异常处理不工作
**症状：** 异常没有被 GlobalExceptionHandler 捕获  
**排查步骤：**
1. 检查 GlobalExceptionHandler 是否被扫描
2. 检查异常类型是否被处理
3. 检查 @RestControllerAdvice 注解是否正确
4. 检查异常处理方法的签名

---

## 📝 测试报告模板

```
测试日期：2025-10-17
测试人员：[名字]
测试环境：[环境描述]

测试结果：
- 功能测试：✅ 通过 / ❌ 失败
- 异常处理：✅ 通过 / ❌ 失败
- 性能测试：✅ 通过 / ❌ 失败

发现的问题：
1. [问题描述]
2. [问题描述]

建议：
1. [建议]
2. [建议]

签名：________________
```

---

**祝测试顺利！** 🎉

