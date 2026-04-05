# 历史时间线和事件关联功能 - 部署说明

## 功能概述

新增 **历史时间线** 和 **历史事件关联** 功能，让用户可以通过时间线方式浏览历史事件，并建立人物与事件之间的关联。

## 新增内容

### 1. 数据库表

运行 SQL 脚本创建新表：

```bash
mysql -u root -p namelist < sql/init-event-tables.sql
```

**新增表：**
- `period` - 历史时期表（古代史、近代史、当代史、新时代）
- `event` - 历史事件表（南昌起义、抗日战争、两弹一星等）
- `person_event` - 人物-事件关联表

### 2. 后台管理页面

- `/admin/event/period/list` - 时期管理
- `/admin/event/list` - 历史事件管理
- `/admin/event/relation/list` - 事件-人物关联管理

### 3. 前台展示页面

- `/timeline` - 历史时间线（横向时间轴展示）
- `/events` - 历史事件库
- `/event/{id}` - 事件详情页（含参与人物）

### 4. API 接口

- `GET /api/timeline` - 获取时间线数据
- `GET /api/events` - 获取事件列表
- `GET /api/event/{id}` - 获取事件详情
- `GET /api/event/{id}/persons` - 获取事件的参与人物
- `GET /api/person/{personId}/events` - 获取人物的参与事件

## 部署步骤

### 1. 运行数据库脚本

```bash
mysql -u root -p namelist < sql/init-event-tables.sql
```

### 2. 编译项目

```bash
mvn clean package -DskipTests
```

### 3. 启动应用

```bash
java -jar target/name-list-0.0.1-SNAPSHOT.jar
```

或使用 Maven 启动：

```bash
mvn spring-boot:run
```

## 预置数据

脚本会自动插入以下数据：

**时期：**
- 古代史 (ANCIENT)
- 近代史 (MODERN)
- 当代史 (CONTEM)
- 新时代 (NEW_ERA)

**事件：**
- 南昌起义 (1927-08-01)
- 抗日战争 (1937-07-07)
- 抗美援朝 (1950-10-01)
- 两弹一星 (1964-10-16)
- 解放隆化 (1948-05-25)
- 开国大典 (1949-10-01)
- 改革开放 (1978-12-18)

**关联：**
- 董存瑞 → 解放隆化
- 朱德 → 南昌起义
- 钱学森 → 两弹一星
- 汪精卫 → 抗日战争

## 注意事项

1. **数据库配置**：确保 `application.properties` 中的数据库连接配置正确
2. **OSS 配置**：事件配图功能需要配置阿里云 OSS
3. **PMD 插件问题**：如果 Maven 编译时遇到 PMD 插件解析问题，可暂时在 `pom.xml` 中注释掉该插件
