# SDU二手市场API - 完整实现

## 📋 项目概述

这是基于提供的API文档和数据库设计文档完成的SDU二手市场RESTful API的完整实现。该项目使用Spring Boot 3.5.6、MyBatis Plus和MySQL构建。

## ✅ 实现状态

### 已完成的组件

#### 1. 实体类（10个PO类）
- ✅ User - 用户账户和个人资料信息
- ✅ Product - 商品列表
- ✅ ProductImage - 商品图片
- ✅ Order - 购买订单
- ✅ Comment - 商品评论
- ✅ Favorite - 用户收藏
- ✅ Message - 私人消息
- ✅ Recharge - 钱包充值记录
- ✅ UserWallet - 用户钱包余额
- ✅ TransactionLog - 交易历史

#### 2. 数据传输对象（20+个VO/DTO类）
- ✅ 认证DTOs（注册、登录、修改密码）
- ✅ 用户资料DTOs
- ✅ 商品DTOs（请求、响应、列表）
- ✅ 订单DTOs（请求、响应、状态更新）
- ✅ 评论DTOs
- ✅ 收藏DTOs
- ✅ 消息DTOs
- ✅ 充值DTOs
- ✅ PageResponse（通用分页包装器）

#### 3. 映射器/仓储层（10个映射器）
- ✅ 所有映射器都继承自MyBatis Plus的BaseMapper
- ✅ 自动CRUD操作支持
- ✅ 自定义查询支持已准备好

#### 4. 服务层（7个服务）
- ✅ UserService - 认证和个人资料管理
- ✅ ProductService - 商品CRUD和过滤
- ✅ OrderService - 订单管理和基于角色的访问控制
- ✅ CommentService - 评论管理
- ✅ FavoriteService - 收藏管理
- ✅ MessageService - 消息历史
- ✅ RechargeService - 充值管理

#### 5. 控制器层（8个控制器）
- ✅ AuthController - 4个端点
- ✅ UserController - 2个端点
- ✅ ProductController - 5个端点
- ✅ OrderController - 4个端点
- ✅ CommentController - 3个端点
- ✅ FavoriteController - 3个端点
- ✅ MessageController - 1个端点
- ✅ RechargeController - 2个端点

**总计：24个API端点已实现**

## 🚀 快速开始

### 前置条件
- Java 17+
- MySQL 8.0+
- Redis
- Maven 3.6+

### 安装步骤

1. **克隆仓库**
```bash
cd d:\project1\SDU_Flea_Market
```

2. **配置数据库**
更新 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xianyu_platform
    username: root
    password: your_password
```

3. **配置Redis**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

4. **构建项目**
```bash
mvn clean install
```

5. **运行应用**
```bash
mvn spring-boot:run
```

API将在 `http://localhost:8081` 可用

## 📚 API端点

### 认证 (POST /auth)
- `POST /auth/register` - 用户注册
- `POST /auth/login` - 用户登录
- `POST /auth/logout` - 用户登出
- `POST /auth/change-password` - 修改密码

### 用户资料 (GET/PUT /users)
- `GET /users/profile` - 获取用户资料
- `PUT /users/profile` - 更新用户资料

### 商品 (GET/POST/PUT/DELETE /products)
- `POST /products` - 创建商品
- `GET /products` - 列出商品（支持过滤）
- `GET /products/{productId}` - 获取商品详情
- `PUT /products/{productId}` - 更新商品
- `DELETE /products/{productId}` - 删除商品

### 订单 (GET/POST/PATCH /orders)
- `POST /orders` - 创建订单
- `GET /orders` - 列出订单
- `GET /orders/{orderId}` - 获取订单详情
- `PATCH /orders/{orderId}/status` - 更新订单状态

### 评论 (GET/POST/DELETE /products/{productId}/comments)
- `POST /products/{productId}/comments` - 创建评论
- `GET /products/{productId}/comments` - 列出评论
- `DELETE /products/{productId}/comments/{commentId}` - 删除评论

### 收藏 (GET/POST/DELETE /favorites)
- `POST /favorites` - 添加收藏
- `DELETE /favorites/{productId}` - 删除收藏
- `GET /favorites` - 列出收藏

### 消息 (GET /messages)
- `GET /messages/{userId}` - 获取消息历史

### 充值 (POST/GET /recharge)
- `POST /recharge` - 创建充值订单
- `GET /recharge/history` - 获取充值历史

## 🔐 认证

所有受保护的端点都需要在Authorization头中包含JWT令牌：
```
Authorization: Bearer {token}
```

## 📝 重要实现说明

### 1. JWT令牌提取
控制器使用占位符方法进行令牌提取。您需要实现：
```java
private String extractUserIdFromToken(String token) {
    // TODO: 实现JWT令牌解析
    return TokenExtractor.extractUserIdFromToken(token);
}
```

详见 `JWT_IMPLEMENTATION_GUIDE.md` 获取详细的实现说明。

### 2. 数据库架构
您需要创建数据库表。使用 `Database_Design_Document.md` 中的架构或创建迁移脚本。

### 3. 文件上传
商品图片上传功能尚未实现。您需要：
- 在ProductController中添加文件上传处理
- 实现图片存储（本地或云存储）
- 更新ProductService以处理图片URL

### 4. WebSocket实时消息
消息端点目前仅支持REST。对于实时消息：
- 在 `/ws/messages` 实现WebSocket端点
- 添加消息广播逻辑
- 更新MessageService以支持实时操作

## 🛠️ 项目结构

```
src/main/java/org/stnhh/sdu_flea_market/
├── controller/          # REST控制器（8个文件）
├── service/             # 服务接口（7个文件）
│   └── impl/           # 服务实现（7个文件）
├── mapper/             # MyBatis Plus映射器（10个文件）
├── data/
│   ├── po/            # 实体类（10个文件）
│   └── vo/            # DTOs和响应对象（20+个文件）
├── config/            # Spring配置
├── utils/             # 工具类
└── cache/             # 缓存实现
```

## 🔍 主要功能

1. **用户管理**
   - 带邮箱验证的注册
   - 使用JWT令牌的安全登录
   - 个人资料管理
   - 密码修改功能

2. **商品管理**
   - 创建、读取、更新、删除商品
   - 商品过滤和搜索
   - 浏览次数跟踪
   - 软删除支持

3. **订单管理**
   - 订单创建和跟踪
   - 多种支付方式
   - 订单状态管理
   - 买家/卖家基于角色的访问控制

4. **社交功能**
   - 带分页的商品评论
   - 收藏/书签功能
   - 私人消息

5. **财务功能**
   - 钱包充值功能
   - 交易日志
   - 多种支付方式

6. **分页**
   - 所有列表端点都支持分页
   - 可配置的页面大小
   - 响应中包含总数

## 📖 文档文件

- `IMPLEMENTATION_SUMMARY.md` - 详细的实现概述
- `JWT_IMPLEMENTATION_GUIDE.md` - JWT令牌实现指南
- `ducuments/API_Documentation.md` - 完整的API规范
- `ducuments/Database_Design_Document.md` - 数据库架构设计

## ⚠️ 待办事项

1. **实现JWT令牌提取** - 关键
2. **创建数据库架构** - 关键
3. **实现文件上传** - 重要
4. **添加WebSocket支持** - 重要
5. **编写单元测试** - 重要
6. **添加API文档（Swagger）** - 可选
7. **实现缓存** - 性能优化
8. **添加速率限制** - 安全增强

## 🧪 测试

### 使用cURL进行手动测试

```bash
# 注册
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123","confirm_password":"password123"}'

# 登录
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# 获取资料（使用登录返回的令牌）
curl -X GET http://localhost:8081/users/profile \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🤝 贡献

添加新功能时：
1. 在 `data/po/` 中创建对应的PO类
2. 在 `data/vo/` 中创建DTOs
3. 在 `mapper/` 中创建映射器
4. 创建服务接口和实现
5. 创建带端点的控制器
6. 更新此README

## 📞 支持

如有问题或疑问：
1. 查看文档文件
2. 查看API规范
3. 查看数据库设计文档
4. 查看现有实现以了解模式

## 📄 许可证

此项目是SDU二手市场平台的一部分。

---

**最后更新：** 2025-10-16
**状态：** ✅ 完成 - 准备好进行JWT实现和测试

