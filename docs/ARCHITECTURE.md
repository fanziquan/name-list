# Name-List 系统架构

## 概述
Name-List 是一个三层架构的 Web 应用程序，基于 Spring Boot 框架，采用经典的分层设计模式。

## 架构图

```
┌─────────────────────────────────────────────────────┐
│                   Web 浏览器                          │
└─────────────────┬───────────────────────────────────┘
                  │ HTTP/HTTPS
┌─────────────────▼───────────────────────────────────┐
│               Spring MVC 层                          │
│  ┌─────────────┬─────────────┬─────────────┐       │
│  │  Controller │  Controller │  Controller │       │
│  │  (Admin)    │  (Auth)     │  (Public)   │       │
│  └─────────────┴─────────────┴─────────────┘       │
└─────────────────┬───────────────────────────────────┘
                  │ 服务调用
┌─────────────────▼───────────────────────────────────┐
│               业务逻辑层                             │
│  ┌─────────────┬─────────────┬─────────────┐       │
│  │ PersonService│ OssService │ AuthService │       │
│  └─────────────┴─────────────┴─────────────┘       │
└─────────────────┬───────────────────────────────────┘
                  │ 数据访问
┌─────────────────▼───────────────────────────────────┐
│               数据访问层                             │
│  ┌─────────────┬─────────────┬─────────────┐       │
│  │  Mapper     │  Mapper     │  Mapper     │       │
│  │  (MyBatis+) │  (MyBatis+) │  (MyBatis+) │       │
│  └─────────────┴─────────────┴─────────────┘       │
└─────────────────┬───────────────────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────────────────┐
│               持久化存储                             │
│  ┌─────────────────────────────────────────────────┐│
│  │               MySQL 数据库                       ││
│  │  • hero_person      - 正面人物表                ││
│  │  • villain_person   - 反面人物表                ││
│  │  • person_extend    - 人物扩展表                ││
│  │  • dictionary       - 数据字典表                ││
│  │  • admin_user       - 管理员用户表              ││
│  │  • id_sequence      - ID 序列表                 ││
│  └─────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────┘
```

## 技术栈详情

### 后端技术栈
1. **框架**: Spring Boot 2.2.13
2. **数据访问**: MyBatis-Plus 3.5.3.1
3. **模板引擎**: FreeMarker
4. **安全框架**: Spring Security
5. **数据库**: MySQL 8.0.33
6. **文件存储**: 阿里云 OSS
7. **工具库**: Lombok, Apache Commons IO/FileUpload

### 前端技术栈
1. **模板**: FreeMarker HTML 模板
2. **样式**: 内联 CSS + Bootstrap（计划中）
3. **交互**: 原生 JavaScript + jQuery（计划中）

## 数据库设计

### 核心表结构
1. **hero_person** - 正面人物表
   - 字段: id, name, birth_year, death_year, category, nationality, brief_intro, full_bio, photo_url, extend_id, status
   - 分类: MAR(烈士), SCI(科学家), GEN(将军), PAT(爱国志士)

2. **villain_person** - 反面人物表
   - 字段: id, name, birth_year, death_year, crime_type, nationality, brief_intro, full_bio, photo_url, extend_id, status
   - 犯罪类型: WAR(战争罪犯), COR(腐败分子), CRU(暴君), CRI(刑事罪犯)

3. **person_extend** - 人物扩展信息表
   - 字段: id, related_persons, historical_events, cultural_impact, tags

4. **dictionary** - 数据字典表
   - 字段: dict_type, dict_code, dict_name, dict_value, sort_order

5. **admin_user** - 管理员用户表
   - 字段: id, username, password, role, status, create_time, update_time

6. **id_sequence** - ID 序列表
   - 字段: seq_name, current_value, step_size, prefix, suffix

## 核心业务流程

### 1. 人物管理流程
```
用户请求 → 控制器验证 → 业务逻辑处理 → 数据库操作 → 结果返回
```

### 2. 图片上传流程
```
上传请求 → 文件验证 → OSS 存储 → 返回 URL → 数据库更新
```

### 3. 认证授权流程
```
登录请求 → 密码验证 → Session 创建 → 权限检查 → 访问控制
```

## 设计模式

1. **MVC 模式**: Controller-Service-Mapper 三层分离
2. **依赖注入**: Spring IoC 容器管理 Bean
3. **数据映射**: MyBatis-Plus ORM 框架
4. **模板方法**: FreeMarker 模板渲染
5. **策略模式**: OSS 存储策略（可扩展）

## 扩展性设计

### 水平扩展
- 无状态设计，支持多实例部署
- Session 存储可迁移到 Redis

### 功能扩展
- 插件式设计，新增人物类型只需添加对应实体和 Mapper
- 存储策略可替换（本地存储、其他云存储）
- 模板引擎支持多主题切换

### 性能优化
- 数据库索引优化
- 缓存层预留（Redis）
- 静态资源 CDN 分发

## 安全性设计

1. **认证**: 管理员登录验证
2. **授权**: 基于角色的访问控制
3. **输入验证**: 请求参数校验
4. **文件安全**: 上传文件类型和大小限制
5. **会话安全**: HTTP-Only Cookie, Session 超时

## 监控与日志

1. **应用日志**: Logback 配置，按包名分级
2. **SQL 日志**: MyBatis-Plus SQL 输出
3. **性能监控**: Spring Boot Actuator（计划中）
4. **健康检查**: 数据库连接状态检查