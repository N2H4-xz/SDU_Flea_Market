
# JWT Token 实现指南

## 概述
本指南说明如何在控制器中正确实现 JWT 令牌解析，从 token 中提取用户 ID。

## 当前实现状态

当前控制器使用如下占位方法：
```java
private String extractUserIdFromToken(String token) {
    // This is a placeholder - you'll need to implement JWT token parsing
    return "user_id_from_token";
}
```

## 推荐实现方案

### 方案一：使用 JWTUtil（推荐）

项目中已包含 `JWTUtil` 类，可扩展该类以支持 token 验证与用户 ID 提取：

```java
// In JWTUtil.java
public String getUserIdFromToken(String token, String key) {
    try {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(key))
                .build()
                .verify(token);
        return decodedJWT.getClaim("user_id").asString();
    } catch (Exception e) {
        throw new RuntimeException("Token验证失败或已过期");
    }
}
```

### 方案二：创建新的 TokenExtractor 工具类

可以创建一个独立的工具类来负责 token 的解析与校验：

```java
package org.stnhh.sdu_flea_market.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenExtractor {
    private static final String SECRET_KEY = "STPlayTableSecretKey";

    public static String extractUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            return decodedJWT.getClaim("user_id").asString();
        } catch (Exception e) {
            throw new RuntimeException("Token无效或已过期");
        }
    }

    public static boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 方案三：创建拦截器（最佳实践）

建议通过拦截器统一处理 token 的提取与校验，拦截器可在请求进入控制器前完成鉴权并将 userId 放入请求属性：

```java
package org.stnhh.sdu_flea_market.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.stnhh.sdu_flea_market.utils.TokenUtil;
import org.stnhh.sdu_flea_market.utils.TokenExtractor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip token validation for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/auth/register") || path.startsWith("/auth/login") || path.startsWith("/products")) {
            return true;
        }

        try {
            String token = TokenUtil.extractToken(request);
            String userId = TokenExtractor.extractUserIdFromToken(token);
            
            // Store userId in request attribute for later use
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\": 401, \"message\": \"未授权\"}");
            return false;
        }
    }
}
```

## 实施步骤

### 步骤 1：更新 JWTUtil 或 创建 TokenExtractor

选择上述方案之一并实现。

### 步骤 2：更新控制器

在每个控制器中替换占位方法：

**Before:**
```java
private String extractUserIdFromToken(String token) {
    return "user_id_from_token";
}
```

**After (Option 1 - Using JWTUtil):**
```java
@Autowired
private JWTUtil jwtUtil;

private String extractUserIdFromToken(String token) {
    return jwtUtil.getUserIdFromToken(token, "STPlayTableSecretKey");
}
```

**After (Option 2 - Using TokenExtractor):**
```java
private String extractUserIdFromToken(String token) {
    return TokenExtractor.extractUserIdFromToken(token);
}
```

**After (Option 3 - Using Interceptor):**
```java
private String extractUserIdFromToken(HttpServletRequest request) {
    return (String) request.getAttribute("userId");
}

// Update method signatures to accept HttpServletRequest
@GetMapping("/profile")
public ResponseEntity<Result> getProfile(HttpServletRequest request) {
    try {
        String userId = extractUserIdFromToken(request);
        // ... rest of the code
    }
}
```

### 步骤 3：更新所有控制器

将相同模式应用到以下控制器：
- AuthController
- UserController
- ProductController
- OrderController
- CommentController
- FavoriteController
- MessageController
- RechargeController

### 步骤 4：注册拦截器（若使用方案三）

更新 `WebConfig.java`：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/register", "/auth/login", "/products/**");
    }
}
```

## 测试

### 使用 cURL 测试

```bash
# Register
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirm_password": "password123"
  }'

# Login
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Use the token from login response
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Get profile
curl -X GET http://localhost:8081/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

## 安全注意事项

1. **密钥管理**
   - 将秘钥存放在环境变量中，避免写入代码中
   - 在不同环境（dev、staging、prod）使用不同的秘钥

2. **Token 过期策略**
   - 考虑实现 token 刷新机制
   - 设置合理的过期时间

3. **HTTPS**
   - 生产环境必须使用 HTTPS
   - 切勿通过 HTTP 明文传输 token

4. **Token 存储**
   - 在客户端安全存储 token
   - 尽量使用 HttpOnly Cookie

5. **校验**
   - 始终校验 token 签名
   - 检查 token 是否过期
   - 校验用户权限

## 故障排查

### Token 验证失败
- 检查秘钥是否与签发 token 时使用的秘钥一致
- 确认 token 是否已过期
- 确认 token 格式（Bearer 前缀）是否正确

### 未找到 User ID
- 确认 JWT 负载中包含 `"user_id"` 声明
- 检查声明名称是否完全匹配

### 拦截器未生效
- 确认已在 `WebConfig` 中注册拦截器
- 检查拦截路径模式是否正确
- 通过日志确认拦截器是否被调用


