# 闲鱼风二手物品交易平台 — RESTful API 接口文档

## 文档说明
- **基础 URL**: `http://api.xianyu.com/v1`
- **认证方式**: JWT Token (Bearer Token)
- **请求格式**: JSON
- **响应格式**: JSON
- **时间格式**: ISO 8601 (2024-01-15T10:30:00Z)

---

## 1. 用户相关接口

### 1.1 用户注册
**POST** `/auth/register`

**请求体**:
```json
{
  "username": "string (3-20字符)",
  "email": "string (有效邮箱)",
  "password": "string (至少8字符)",
  "confirm_password": "string"
}
```

**响应** (201 Created):
```json
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "user_id": "uuid",
    "username": "string",
    "email": "string",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

**错误响应** (400/409):
```json
{
  "code": 400,
  "message": "用户名已存在 / 邮箱已注册 / 密码不符合要求"
}
```

---

### 1.2 用户登录
**POST** `/auth/login`

**请求体**:
```json
{
  "email": "string",
  "password": "string"
}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "user_id": "uuid",
    "username": "string",
    "email": "string",
    "token": "jwt_token_string",
    "expires_in": 86400
  }
}
```

**错误响应** (401):
```json
{
  "code": 401,
  "message": "邮箱或密码错误"
}
```

---

### 1.3 获取个人资料
**GET** `/users/profile`

**请求头**:
```
Authorization: Bearer {token}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "user_id": "uuid",
    "username": "string",
    "email": "string",
    "avatar": "url",
    "nickname": "string",
    "campus": "string (校区)",
    "major": "string (专业)",
    "phone": "string",
    "wechat": "string",
    "bio": "string (个人简介)",
    "created_at": "2024-01-15T10:30:00Z",
    "updated_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 1.4 更新个人资料
**PUT** `/users/profile`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "nickname": "string (可选)",
  "avatar": "url (可选)",
  "campus": "string (可选)",
  "major": "string (可选)",
  "phone": "string (可选)",
  "wechat": "string (可选)",
  "bio": "string (可选)"
}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "更新成功",
  "data": { /* 更新后的用户信息 */ }
}
```

---

### 1.5 修改密码
**POST** `/auth/change-password`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "old_password": "string",
  "new_password": "string",
  "confirm_password": "string"
}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "密码修改成功"
}
```

---

### 1.6 用户登出
**POST** `/auth/logout`

**请求头**:
```
Authorization: Bearer {token}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "登出成功"
}
```

---

## 2. 商品相关接口

### 2.1 发布商品
**POST** `/products`

**请求头**:
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**请求体**:
```json
{
  "title": "string (1-100字符)",
  "description": "string (1-2000字符)",
  "category": "string (电子产品/图书/服装/其他)",
  "price": "number (0.01-999999.99)",
  "condition": "string (全新/九成新/八成新/七成新/较旧)",
  "campus": "string (校区)",
  "images": "file[] (最多9张，每张≤5MB)"
}
```

**响应** (201 Created):
```json
{
  "code": 0,
  "message": "商品发布成功",
  "data": {
    "product_id": "uuid",
    "title": "string",
    "price": 99.99,
    "status": "active",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 2.2 获取商品列表
**GET** `/products`

**查询参数**:
```
page: integer (默认1)
limit: integer (默认20, 最大100)
keyword: string (可选，搜索标题和描述)
category: string (可选，筛选分类)
campus: string (可选，筛选校区)
sort: string (可选: newest/price_asc/price_desc, 默认newest)
condition: string (可选，筛选成色)
```

**示例**: `GET /products?page=1&limit=20&keyword=手机&campus=校区A&sort=price_asc`

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "total": 150,
    "page": 1,
    "limit": 20,
    "items": [
      {
        "product_id": "uuid",
        "title": "string",
        "price": 99.99,
        "condition": "九成新",
        "campus": "校区A",
        "category": "电子产品",
        "thumbnail": "url",
        "seller_id": "uuid",
        "seller_nickname": "string",
        "status": "active",
        "created_at": "2024-01-15T10:30:00Z"
      }
    ]
  }
}
```

---

### 2.3 获取商品详情
**GET** `/products/{product_id}`

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "product_id": "uuid",
    "title": "string",
    "description": "string",
    "price": 99.99,
    "condition": "九成新",
    "category": "电子产品",
    "campus": "校区A",
    "images": ["url1", "url2"],
    "seller": {
      "user_id": "uuid",
      "nickname": "string",
      "avatar": "url",
      "campus": "校区A",
      "phone": "string",
      "wechat": "string"
    },
    "status": "active",
    "view_count": 150,
    "created_at": "2024-01-15T10:30:00Z",
    "updated_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 2.4 编辑商品
**PUT** `/products/{product_id}`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**: 同发布商品，所有字段可选

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "商品更新成功",
  "data": { /* 更新后的商品信息 */ }
}
```

---

### 2.5 删除商品
**DELETE** `/products/{product_id}`

**请求头**:
```
Authorization: Bearer {token}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "商品删除成功"
}
```

---


## 3. 订单相关接口

### 3.1 创建订单
**POST** `/orders`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "product_id": "uuid",
  "quantity": 1,
  "payment_method": "online|offline"
}
```

**响应** (201 Created):
```json
{
  "code": 0,
  "message": "订单创建成功",
  "data": {
    "order_id": "uuid",
    "product_id": "uuid",
    "buyer_id": "uuid",
    "seller_id": "uuid",
    "amount": 99.99,
    "status": "pending_payment",
    "payment_method": "online",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 3.2 获取订单列表
**GET** `/orders`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
```
page: integer (默认1)
limit: integer (默认20)
status: string (可选: pending_payment/paid/completed/cancelled)
role: string (可选: buyer/seller, 默认buyer)
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "total": 50,
    "page": 1,
    "limit": 20,
    "items": [
      {
        "order_id": "uuid",
        "product_id": "uuid",
        "product_title": "string",
        "buyer_id": "uuid",
        "seller_id": "uuid",
        "amount": 99.99,
        "status": "pending_payment",
        "payment_method": "online",
        "created_at": "2024-01-15T10:30:00Z"
      }
    ]
  }
}
```

---

### 3.3 获取订单详情
**GET** `/orders/{order_id}`

**请求头**:
```
Authorization: Bearer {token}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "order_id": "uuid",
    "product": { /* 商品详情 */ },
    "buyer": { /* 买家信息 */ },
    "seller": { /* 卖家信息 */ },
    "amount": 99.99,
    "status": "pending_payment",
    "payment_method": "online",
    "created_at": "2024-01-15T10:30:00Z",
    "paid_at": null,
    "completed_at": null
  }
}
```

---

### 3.4 更新订单状态
**PATCH** `/orders/{order_id}/status`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "status": "paid|completed|cancelled"
}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "订单状态更新成功",
  "data": {
    "order_id": "uuid",
    "status": "paid",
    "updated_at": "2024-01-15T10:30:00Z"
  }
}
```

---



## 5. 留言相关接口

### 5.1 发布留言
**POST** `/products/{product_id}/comments`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "content": "string (1-500字符)"
}
```

**响应** (201 Created):
```json
{
  "code": 0,
  "message": "留言发布成功",
  "data": {
    "comment_id": "uuid",
    "product_id": "uuid",
    "author": {
      "user_id": "uuid",
      "nickname": "string",
      "avatar": "url"
    },
    "content": "string",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 5.2 获取商品留言列表
**GET** `/products/{product_id}/comments`

**查询参数**:
```
page: integer (默认1)
limit: integer (默认20)
sort: string (可选: newest/oldest)
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "total": 50,
    "page": 1,
    "limit": 20,
    "items": [
      {
        "comment_id": "uuid",
        "author": {
          "user_id": "uuid",
          "nickname": "string",
          "avatar": "url"
        },
        "content": "string",
        "created_at": "2024-01-15T10:30:00Z"
      }
    ]
  }
}
```

---

### 5.3 删除留言
**DELETE** `/products/{product_id}/comments/{comment_id}`

**请求头**:
```
Authorization: Bearer {token}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "留言删除成功"
}
```

---

## 6. 收藏相关接口

### 6.1 收藏商品
**POST** `/favorites`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "product_id": "uuid"
}
```

**响应** (201 Created):
```json
{
  "code": 0,
  "message": "收藏成功",
  "data": {
    "favorite_id": "uuid",
    "product_id": "uuid",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 6.2 取消收藏
**DELETE** `/favorites/{product_id}`

**请求头**:
```
Authorization: Bearer {token}
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "取消收藏成功"
}
```

---

### 6.3 获取收藏列表
**GET** `/favorites`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
```
page: integer (默认1)
limit: integer (默认20)
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "total": 30,
    "page": 1,
    "limit": 20,
    "items": [
      {
        "favorite_id": "uuid",
        "product": { /* 商品信息 */ },
        "created_at": "2024-01-15T10:30:00Z"
      }
    ]
  }
}
```

---

## 7. 私信相关接口 (WebSocket)

### 7.1 WebSocket 连接
**WS** `/ws/messages`

**连接参数**:
```
token: jwt_token (通过 URL 参数或 Header 传递)
```

**连接示例**: `ws://api.xianyu.com/ws/messages?token={jwt_token}`

---

### 7.2 发送消息
**消息格式**:
```json
{
  "type": "message",
  "recipient_id": "uuid",
  "content": "string (1-1000字符)",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### 7.3 接收消息
**消息格式**:
```json
{
  "type": "message",
  "sender_id": "uuid",
  "sender_nickname": "string",
  "content": "string",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

### 7.4 获取消息历史
**GET** `/messages/{user_id}`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
```
page: integer (默认1)
limit: integer (默认50)
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "total": 100,
    "page": 1,
    "limit": 50,
    "items": [
      {
        "message_id": "uuid",
        "sender_id": "uuid",
        "recipient_id": "uuid",
        "content": "string",
        "created_at": "2024-01-15T10:30:00Z"
      }
    ]
  }
}
```

---

## 8. 充值相关接口

### 8.1 创建充值订单
**POST** `/recharge`

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "amount": 100.00,
  "payment_method": "alipay|wechat|card"
}
```

**响应** (201 Created):
```json
{
  "code": 0,
  "message": "充值订单创建成功",
  "data": {
    "recharge_id": "uuid",
    "amount": 100.00,
    "status": "pending",
    "payment_url": "string (支付链接)",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

---

### 8.2 获取充值记录
**GET** `/recharge/history`

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
```
page: integer (默认1)
limit: integer (默认20)
status: string (可选: pending/completed/failed)
```

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "total": 15,
    "page": 1,
    "limit": 20,
    "items": [
      {
        "recharge_id": "uuid",
        "amount": 100.00,
        "status": "completed",
        "payment_method": "alipay",
        "created_at": "2024-01-15T10:30:00Z",
        "completed_at": "2024-01-15T10:35:00Z"
      }
    ]
  }
}
```

---

## 9. 错误响应格式

所有错误响应遵循统一格式：

```json
{
  "code": 400,
  "message": "错误描述信息",
  "errors": [
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ]
}
```

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权 / Token 过期 |
| 403 | 禁止访问 / 权限不足 |
| 404 | 资源不存在 |
| 409 | 资源冲突 (如用户名已存在) |
| 422 | 数据验证失败 |
| 500 | 服务器内部错误 |

---

## 10. 认证说明

### JWT Token 使用

1. **获取 Token**: 通过登录接口获取
2. **使用 Token**: 在请求头中添加 `Authorization: Bearer {token}`
3. **Token 有效期**: 永久
4. **刷新 Token**: 无刷新机制

### 示例请求

```bash
curl -X GET http://api.xianyu.com/v1/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 11. 分页说明

所有列表接口支持分页，使用以下参数：

- `page`: 页码 (从 1 开始)
- `limit`: 每页数量 (默认 20, 最大 100)

**示例**: `GET /products?page=2&limit=50`

**响应包含**:
- `total`: 总数
- `page`: 当前页码
- `limit`: 每页数量
- `items`: 数据数组

---
## 13. 数据验证规则

### 用户名
- 长度: 3-20 字符
- 允许: 字母、数字、下划线
- 不允许: 特殊字符

### 密码
- 最少 8 字符
- 必须包含: 字母 + 数字

### 价格
- 范围: 0.01 - 999999.99
- 精度: 2 位小数

### 图片
- 格式: JPG, PNG, GIF
- 大小: 单张 ≤ 5MB, 总计 ≤ 50MB
- 分辨率: 最小 200x200px

---

## 14. 版本控制

- **当前版本**: v1
- **发布日期**: 2025-10-16
- **维护者**: API Team

---
