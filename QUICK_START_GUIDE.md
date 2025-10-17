# 快速启动指南

**项目：** SDU 二手市场 (SDU_Flea_Market)  
**状态：** ✅ 已完成 @Auth 注解重构  
**编译状态：** ✅ 无错误

---

## 🚀 快速启动

### 1. 编译项目
```bash
mvn clean install
```

### 2. 运行项目
```bash
mvn spring-boot:run
```

或者直接运行 JAR 文件：
```bash
java -jar target/SDU_Flea_Market-1.0.0.jar
```

### 3. 访问应用
```
http://localhost:8081
```

---

## 🔐 认证流程

### 步骤 1：注册用户
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirm_password": "password123"
  }'
```

### 步骤 2：登录获取 Token
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "user123",
      "username": "testuser",
      "email": "test@example.com"
    }
  }
}
```

### 步骤 3：使用 Token 调用受保护的端点
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8081/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📚 API 端点列表

### 认证相关 (Authentication)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| POST | `/auth/register` | ❌ | 用户注册 |
| POST | `/auth/login` | ❌ | 用户登录 |
| POST | `/auth/logout` | ✅ | 用户登出 |
| POST | `/auth/change-password` | ✅ | 修改密码 |

### 用户相关 (User)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| GET | `/users/profile` | ✅ | 获取个人资料 |
| PUT | `/users/profile` | ✅ | 更新个人资料 |

### 商品相关 (Product)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| POST | `/products` | ✅ | 发布商品 |
| GET | `/products` | ❌ | 获取商品列表 |
| GET | `/products/{productId}` | ❌ | 获取商品详情 |
| PUT | `/products/{productId}` | ✅ | 更新商品 |
| DELETE | `/products/{productId}` | ✅ | 删除商品 |

### 订单相关 (Order)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| POST | `/orders` | ✅ | 创建订单 |
| GET | `/orders` | ✅ | 获取订单列表 |
| GET | `/orders/{orderId}` | ✅ | 获取订单详情 |
| PATCH | `/orders/{orderId}/status` | ✅ | 更新订单状态 |

### 评论相关 (Comment)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| POST | `/products/{productId}/comments` | ✅ | 创建评论 |
| GET | `/products/{productId}/comments` | ❌ | 获取评论列表 |
| DELETE | `/products/{productId}/comments/{commentId}` | ✅ | 删除评论 |

### 收藏相关 (Favorite)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| POST | `/favorites` | ✅ | 添加收藏 |
| DELETE | `/favorites/{productId}` | ✅ | 移除收藏 |
| GET | `/favorites` | ✅ | 获取收藏列表 |

### 消息相关 (Message)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| GET | `/messages/{userId}` | ✅ | 获取消息历史 |

### 充值相关 (Recharge)
| 方法 | 端点 | 认证 | 说明 |
|------|------|------|------|
| POST | `/recharge` | ✅ | 创建充值订单 |
| GET | `/recharge/history` | ✅ | 获取充值历史 |

---

## 🔧 配置文件

### application.yml
```yaml
server:
  port: 8081

spring:
  application:
    name: SDU_Flea_Market
  datasource:
    url: jdbc:mysql://localhost:3306/sdu_flea_market
    username: root
    password: 987654321
  data:
    redis:
      host: localhost
      port: 6379
      password: 123456

jwt:
  secret-key: "STPlayTableSecretKey"
  refresh-secret-key: "STPlayTableRefreshSecretKey"
```

---

## 🧪 测试

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn verify
```

---

## 📊 项目结构

```
src/main/java/org/stnhh/sdu_flea_market/
├── annotation/
│   └── Auth.java                    # @Auth 注解
├── aspect/
│   └── AuthAspect.java              # AOP 切面
├── controller/
│   ├── AuthController.java          # 认证控制器
│   ├── UserController.java          # 用户控制器
│   ├── ProductController.java       # 商品控制器
│   ├── OrderController.java         # 订单控制器
│   ├── CommentController.java       # 评论控制器
│   ├── FavoriteController.java      # 收藏控制器
│   ├── MessageController.java       # 消息控制器
│   └── RechargeController.java      # 充值控制器
├── service/
│   └── ...                          # 业务逻辑层
├── mapper/
│   └── ...                          # 数据访问层
├── data/
│   ├── po/                          # 持久化对象
│   └── vo/                          # 值对象
└── utils/
    ├── JWTUtil.java                 # JWT 工具
    ├── TokenUtil.java               # Token 工具
    └── ResponseUtil.java            # 响应工具
```

---

## 🐛 常见问题

### Q1: 如何获取 Token？
**A:** 调用 `/auth/login` 端点，使用邮箱和密码登录，响应中会包含 token。

### Q2: 如何使用 Token？
**A:** 在请求头中添加 `Authorization: Bearer <token>`。

### Q3: Token 过期了怎么办？
**A:** 使用 refreshToken 调用刷新端点获取新的 token。

### Q4: 如何测试受保护的端点？
**A:** 使用 curl 或 Postman，在请求头中添加 Authorization 头。

### Q5: 编译出错怎么办？
**A:** 运行 `mvn clean install` 清理并重新编译。

---

## 📞 技术支持

如有问题，请查看以下文档：
- `FINAL_VERIFICATION_REPORT.md` - 最终验证报告
- `AUTH_ANNOTATION_QUICK_REFERENCE.md` - 快速参考指南
- `BEFORE_AFTER_COMPARISON.md` - 修改前后对比

---

**最后更新：** 2025-10-17  
**项目状态：** ✅ 生产就绪

