# UUID 到 UID 迁移指南

## 📌 概述

本指南说明如何从 UUID 主键迁移到自增 UID 主键。

## ⚠️ 重要提示

- **备份数据库** - 迁移前必须备份现有数据库
- **停止应用** - 迁移期间应停止应用运行
- **测试环境** - 建议先在测试环境验证

## 🔄 迁移步骤

### 步骤 1: 备份现有数据库

```bash
# 备份数据库
mysqldump -u root -p sdu_flea_market > backup_$(date +%Y%m%d_%H%M%S).sql

# 验证备份
ls -lh backup_*.sql
```

### 步骤 2: 停止应用

```bash
# 停止 Spring Boot 应用
# 使用 Ctrl+C 或相应的停止命令
```

### 步骤 3: 执行数据库迁移

```bash
# 方式 1: 使用迁移脚本（推荐）
mysql -u root -p sdu_flea_market < ducuments/database_schema.sql

# 方式 2: 手动执行 SQL
mysql -u root -p
use sdu_flea_market;
# 执行 database_schema.sql 中的 SQL 语句
```

### 步骤 4: 验证迁移

```bash
# 连接到数据库
mysql -u root -p sdu_flea_market

# 检查表结构
DESCRIBE users;
DESCRIBE products;
DESCRIBE orders;

# 验证主键
SHOW CREATE TABLE users\G
SHOW CREATE TABLE products\G
```

### 步骤 5: 编译新代码

```bash
# 清理并编译
mvn clean compile

# 检查编译结果
# 应该没有错误
```

### 步骤 6: 启动应用

```bash
# 启动应用
mvn spring-boot:run

# 或使用 IDE 启动
# 在 IDE 中点击 Run 按钮
```

### 步骤 7: 测试应用

```bash
# 测试登录
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# 测试创建商品
curl -X POST http://localhost:8081/products \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "category": "电子产品",
    "condition": "全新",
    "campus": "校区1"
  }'

# 测试获取商品列表
curl http://localhost:8081/products
```

## 🔍 验证清单

- [ ] 数据库已备份
- [ ] 应用已停止
- [ ] 数据库迁移已执行
- [ ] 表结构已验证
- [ ] 代码已编译
- [ ] 应用已启动
- [ ] 登录功能正常
- [ ] 商品创建正常
- [ ] 商品查询正常
- [ ] 订单创建正常

## 🆘 故障排除

### 问题 1: 数据库连接失败

```bash
# 检查 MySQL 服务
sudo service mysql status

# 启动 MySQL 服务
sudo service mysql start

# 检查连接
mysql -u root -p -e "SELECT 1"
```

### 问题 2: 编译错误

```bash
# 清理缓存
mvn clean

# 重新编译
mvn compile

# 检查错误信息
mvn compile 2>&1 | grep -i error
```

### 问题 3: 应用启动失败

```bash
# 查看日志
tail -f logs/application.log

# 检查端口占用
lsof -i :8081

# 检查配置文件
cat src/main/resources/application.yml
```

### 问题 4: 数据不一致

```bash
# 恢复备份
mysql -u root -p sdu_flea_market < backup_*.sql

# 重新执行迁移
mysql -u root -p sdu_flea_market < ducuments/database_schema.sql
```

## 📝 迁移前后对比

### 数据库表结构

**迁移前**:
```sql
CREATE TABLE users (
  user_id CHAR(36) NOT NULL COMMENT '用户ID (UUID)',
  ...
  PRIMARY KEY (user_id)
);
```

**迁移后**:
```sql
CREATE TABLE users (
  uid BIGINT AUTO_INCREMENT NOT NULL COMMENT '用户ID (自增)',
  ...
  PRIMARY KEY (uid)
);
```

### Java 代码

**迁移前**:
```java
@TableId(type = IdType.ASSIGN_UUID)
private String userId;
```

**迁移后**:
```java
@TableId(type = IdType.AUTO)
private Long uid;
```

### API 调用

**迁移前**:
```bash
GET /products/550e8400-e29b-41d4-a716-446655440000
```

**迁移后**:
```bash
GET /products/1
```

## ✅ 迁移完成

迁移完成后，系统应该：

- ✅ 所有表都使用 `uid` 作为主键
- ✅ 所有主键都是自增 BIGINT 类型
- ✅ 所有外键都指向 `uid` 列
- ✅ 应用正常运行
- ✅ 所有 API 端点正常工作

## 📞 支持

如有问题，请检查：

1. 数据库日志: `/var/log/mysql/error.log`
2. 应用日志: `logs/application.log`
3. 编译输出: `mvn clean compile 2>&1`

