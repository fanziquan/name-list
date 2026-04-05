# Name-List 项目智能体指南

## 项目概述
Name-List 是一个基于 Spring Boot 的人物名录管理系统，用于管理正面人物（英雄）和反面人物（反派）的信息，支持图片上传到阿里云 OSS，具备完整的后台管理功能。

## 核心知识位置

### 项目配置
- **项目配置**: `pom.xml` - Maven 依赖和构建配置
- **应用配置**: `src/main/resources/application.properties` - 数据库、OSS 等配置
- **OSS 配置**: `src/main/resources/application-oss.properties` - 阿里云 OSS 配置

### 代码结构
```
src/main/java/org/example/namelist/
├── NameListApplication.java     # Spring Boot 启动类
├── config/                      # 配置类
├── controller/                  # 控制器层
│   ├── AdminController.java     # 后台管理控制器
│   ├── AuthController.java      # 认证控制器
│   ├── DictionaryController.java # 数据字典控制器
│   ├── EventController.java     # 历史事件控制器
│   └── PublicController.java    # 公共页面控制器
├── entity/                      # 实体类
│   ├── HeroPerson.java          # 正面人物
│   ├── VillainPerson.java       # 反面人物
│   ├── PersonExtend.java        # 人物扩展信息
│   ├── Dictionary.java          # 数据字典
│   ├── AdminUser.java           # 管理员用户
│   └── IdSequence.java          # ID 序列生成
├── mapper/                      # MyBatis-Plus Mapper 接口
└── service/                     # 业务逻辑层
    └── DictionaryService.java   # 数据字典服务
```

### 文档索引
- **架构设计**: `docs/ARCHITECTURE.md`
- **核心信念**: `docs/core-beliefs.md`
- **质量指标**: `docs/quality-score.md`
- **技术债务**: `docs/exec-plans/tech-debt-tracker.md`
- **产品规范**: `docs/product-specs/index.md`
- **执行计划**: `docs/exec-plans/index.md`

## 开发指引

### 环境要求
- Java 8+
- MySQL 8.0+
- Maven 3.6+
- 阿里云 OSS 账号（用于图片存储）

### 启动项目
1. 配置数据库连接：`application.properties`
2. 配置 OSS：`application-oss.properties`
3. 运行：`mvn spring-boot:run` 或 `start.bat`

### 技术栈
- **后端**: Spring Boot 2.2.13, MyBatis-Plus 3.5.3.1
- **前端**: FreeMarker 模板引擎
- **数据库**: MySQL 8.0.33
- **存储**: 阿里云 OSS
- **安全**: Spring Security

## 智能体协作指南
- 添加新功能时，首先更新相应的产品规范文档
- 修复缺陷时，记录在技术债务追踪器中
- 重大变更需要更新架构文档
- 保持代码与文档同步更新