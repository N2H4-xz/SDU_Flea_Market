# 📚 订单 API 文档

**项目**: SDU 二手市场 (SDU Flea Market)  
**版本**: 1.0  
**更新日期**: 2025-10-17

---

## 🔐 认证

所有订单相关的 API 都需要在请求头中包含有效的 JWT Token：

```
Authorization: Bearer {jwt_token}
```

---

## 📋 API 端点

### 1️⃣ 创建订单

**请求**:
```
POST /orders
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

**请求体**:
```json
{
  "product_id": 123,
  "quantity": 1,
  "payment_method": "online"
}
```

**请求字段说明**:
| 字段 | 类型 | 必需 | 说明 |
|------|------|------|------|
| product_id | Long | ✅ | 商品ID |
| quantity | Integer | ❌ | 购买数量 (默认1) |
| payment_method | String | ✅ | 支付方式 (online/offline) |

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "订单创建成功",
  "data": {
    "uid": 1,
    "productId": 123,
    "buyerId": 456,
    "sellerId": 789,
    "amount": 99.99,
    "status": "pending_payment",
    "paymentMethod": "online",
    "quantity": 1,
    "createdAt": "2025-10-17T10:30:00",
    "updatedAt": "2025-10-17T10:30:00"
  }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "商品不存在",
  "data": null
}
```

**可能的错误**:
- `商品不存在` - 商品不存在或已被删除
- `不能购买自己的商品` - 用户尝试购买自己的商品

---

### 2️⃣ 获取订单列表

**请求**:
```
GET /orders?page=1&limit=20&status=pending_payment&role=buyer
Authorization: Bearer {jwt_token}
```

**查询参数**:
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | Integer | 1 | 页码 |
| limit | Integer | 20 | 每页数量 |
| status | String | - | 订单状态 (可选) |
| role | String | buyer | 角色 (buyer/seller/all) |

**订单状态值**:
- `pending_payment` - 待支付
- `paid` - 已支付
- `completed` - 已完成
- `cancelled` - 已取消

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
        "order_id": 1,
        "product_id": 123,
        "product_title": "iPhone 13",
        "buyer_id": 456,
        "seller_id": 789,
        "amount": 99.99,
        "status": "pending_payment",
        "payment_method": "online",
        "created_at": "2025-10-17T10:30:00",
        "paid_at": null,
        "completed_at": null
      }
    ]
  }
}
```

---

### 3️⃣ 获取订单详情

**请求**:
```
GET /orders/{orderId}
Authorization: Bearer {jwt_token}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| orderId | Long | 订单ID |

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "order_id": 1,
    "product_id": 123,
    "product_title": "iPhone 13",
    "buyer_id": 456,
    "seller_id": 789,
    "amount": 99.99,
    "status": "pending_payment",
    "payment_method": "online",
    "created_at": "2025-10-17T10:30:00",
    "paid_at": null,
    "completed_at": null
  }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "订单不存在",
  "data": null
}
```

---

### 4️⃣ 更新订单状态

**请求**:
```
PATCH /orders/{orderId}/status
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| orderId | Long | 订单ID |

**请求体**:
```json
{
  "status": "paid"
}
```

**允许的状态转换**:

| 当前状态 | 允许转换到 | 操作者 |
|---------|-----------|--------|
| pending_payment | paid | 买家 |
| pending_payment | cancelled | 买家/卖家 |
| paid | completed | 卖家 |
| paid | cancelled | 买家/卖家 |

**响应** (200 OK):
```json
{
  "code": 0,
  "message": "订单状态更新成功",
  "data": {
    "order_id": 1,
    "product_id": 123,
    "product_title": "iPhone 13",
    "buyer_id": 456,
    "seller_id": 789,
    "amount": 99.99,
    "status": "paid",
    "payment_method": "online",
    "created_at": "2025-10-17T10:30:00",
    "paid_at": "2025-10-17T10:35:00",
    "completed_at": null
  }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "只有买家可以标记为已支付",
  "data": null
}
```

**可能的错误**:
- `订单不存在` - 订单不存在
- `只有买家可以标记为已支付` - 非买家尝试标记为已支付
- `只有卖家可以标记为已完成` - 非卖家尝试标记为已完成
- `无权限取消此订单` - 无权限取消订单
- `订单状态不允许转换` - 非法的状态转换

---

## 🔄 订单状态流转图

```
┌─────────────────┐
│ pending_payment │ (待支付)
└────────┬────────┘
         │
         ├─→ [买家] → paid (已支付)
         │              │
         │              └─→ [卖家] → completed (已完成)
         │
         └─→ [买家/卖家] → cancelled (已取消)

┌─────────────────┐
│     paid        │ (已支付)
└────────┬────────┘
         │
         ├─→ [卖家] → completed (已完成)
         │
         └─→ [买家/卖家] → cancelled (已取消)
```

---

## 📊 数据模型

### Order (订单实体)

```java
{
  "uid": 1,                          // 订单ID (自增)
  "productId": 123,                  // 商品ID
  "buyerId": 456,                    // 买家ID
  "sellerId": 789,                   // 卖家ID
  "amount": 99.99,                   // 订单金额
  "status": "pending_payment",       // 订单状态
  "paymentMethod": "online",         // 支付方式
  "quantity": 1,                     // 购买数量
  "createdAt": "2025-10-17T10:30:00", // 创建时间
  "paidAt": null,                    // 支付时间
  "completedAt": null,               // 完成时间
  "updatedAt": "2025-10-17T10:30:00"  // 更新时间
}
```

---

## 🧪 使用示例

### 创建订单

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "product_id": 123,
    "quantity": 1,
    "payment_method": "online"
  }'
```

### 获取订单列表

```bash
curl -X GET "http://localhost:8081/orders?page=1&limit=20&role=buyer" \
  -H "Authorization: Bearer {jwt_token}"
```

### 获取订单详情

```bash
curl -X GET http://localhost:8081/orders/1 \
  -H "Authorization: Bearer {jwt_token}"
```

### 更新订单状态

```bash
curl -X PATCH http://localhost:8081/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "status": "paid"
  }'
```

---

## ✅ 检查清单

- ✅ 所有 API 端点已实现
- ✅ 认证和授权已实现
- ✅ 业务逻辑验证已实现
- ✅ 错误处理已实现
- ✅ 状态转换验证已实现

**状态**: 🟢 **生产就绪**

