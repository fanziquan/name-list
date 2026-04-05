# Name-List 人物名录管理系统

## 项目简介
Name-List 是一个基于 Spring Boot 的人物名录管理系统，用于管理正面人物（英雄）和反面人物（反派）的信息。系统具备完整的后台管理功能，支持图片上传到阿里云 OSS，采用现代 Web 开发技术栈。

## 功能特性

### ✅ 已实现功能
- **人物管理**: 正面人物和反面人物的增删改查
- **分类系统**: 人物分类（烈士、科学家、将军、爱国志士等）
- **图片管理**: 上传到阿里云 OSS，支持预览
- **用户系统**: 管理员登录和权限控制
- **后台管理**: 完整的后台操作界面
- **数据字典**: 分类代码和系统参数管理

### 🔄 规划中功能
- 公共访问页面
- 搜索和筛选功能
- 数据统计分析
- API 接口开放
- 移动端适配

## 技术栈

### 后端
- **框架**: Spring Boot 2.2.13
- **ORM**: MyBatis-Plus 3.5.3.1
- **安全**: Spring Security
- **模板**: FreeMarker
- **数据库**: MySQL 8.0.33
- **存储**: 阿里云 OSS

### 前端
- **模板引擎**: FreeMarker
- **样式**: 内联 CSS（计划集成 Bootstrap）
- **脚本**: 原生 JavaScript（计划集成 jQuery）

### 开发工具
- **构建工具**: Maven
- **代码质量**: Checkstyle, PMD, JaCoCo
- **IDE**: 推荐 IntelliJ IDEA 或 Eclipse

## 快速开始

### 环境要求
- Java 8+
- MySQL 8.0+
- Maven 3.6+
- 阿里云 OSS 账号（用于图片存储）

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd name-list
   ```

2. **数据库配置**
   - 创建 MySQL 数据库：`fzqtest`
   - 执行 SQL 脚本初始化表结构
   - 修改 `src/main/resources/application.properties` 中的数据库连接信息

3. **OSS 配置**
   - 复制 `src/main/resources/application-oss.properties.example` 为 `application-oss.properties`
   - 填入阿里云 OSS 的 Access Key 和配置信息

4. **构建项目**
   ```bash
   mvn clean package
   ```

5. **运行项目**
   ```bash
   # 使用 Maven
   mvn spring-boot:run
   
   # 或使用启动脚本
   start.bat
   ```

6. **访问系统**
   - 后台管理: http://localhost:8080/admin
   - 默认账号: admin / admin123

## 项目结构

```
name-list/
├── src/main/java/org/example/namelist/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器层
│   ├── entity/          # 实体类
│   ├── mapper/          # MyBatis-Plus Mapper
│   └── service/         # 业务逻辑层
├── src/main/resources/
│   ├── templates/       # FreeMarker 模板
│   ├── mapper/          # MyBatis XML 映射文件
│   └── application.properties  # 主配置文件
├── docs/                # 项目文档
├── check-project.bat    # 质量检查脚本
└── pom.xml             # Maven 配置
```

## 质量保障

### 代码质量检查
```bash
# 运行完整质量检查
mvn validate

# 单独运行检查
mvn checkstyle:check    # 代码规范检查
mvn pmd:check           # 代码质量检查
mvn jacoco:check        # 测试覆盖率检查
```

### 测试
```bash
# 运行所有测试
mvn test

# 生成测试覆盖率报告
mvn jacoco:report
```

### 项目完整性检查
```bash
# Windows
check-project.bat

# 或手动运行 Maven 编译
mvn compile
```

## 文档

### 核心文档
- [AGENTS.md](AGENTS.md) - 智能体指南和项目概览
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) - 系统架构设计
- [docs/core-beliefs.md](docs/core-beliefs.md) - 项目核心价值观
- [docs/quality-score.md](docs/quality-score.md) - 质量指标体系

### 开发文档
- [docs/product-specs/index.md](docs/product-specs/index.md) - 产品功能规范
- [docs/exec-plans/tech-debt-tracker.md](docs/exec-plans/tech-debt-tracker.md) - 技术债务追踪

## 开发指南

### 代码规范
- 遵循 Java 编码规范
- 使用 Checkstyle 进行代码规范检查
- 重要代码必须添加注释
- 公共方法需要 JavaDoc

### 提交规范
1. 代码提交前运行质量检查
2. 确保所有测试通过
3. 更新相关文档
4. 填写清晰的提交信息

### 分支策略
- `main` - 主分支，稳定版本
- `develop` - 开发分支，功能集成
- `feature/*` - 功能分支
- `bugfix/*` - 缺陷修复分支

## 部署

### 生产环境要求
- 独立应用服务器（Tomcat/Jetty）
- MySQL 数据库集群
- 阿里云 OSS 存储
- CDN 加速（可选）

### 部署步骤
1. 打包应用：`mvn clean package -DskipTests`
2. 上传 `target/name-list-0.0.1-SNAPSHOT.jar` 到服务器
3. 配置生产环境参数
4. 启动应用：`java -jar name-list-0.0.1-SNAPSHOT.jar`

## 监控和维护

### 系统监控
- 应用健康检查：`/actuator/health`
- 性能指标收集
- 错误日志聚合

### 数据备份
- 数据库定时备份
- OSS 数据同步备份
- 配置文件版本管理

## 贡献指南

### 报告问题
- 使用 Issue 模板报告 bug
- 提供详细的复现步骤
- 附上相关日志和截图

### 功能建议
- 描述功能场景和价值
- 提供用户故事和使用流程
- 讨论技术实现方案

### 代码贡献
1. Fork 项目仓库
2. 创建功能分支
3. 遵循代码规范
4. 添加测试用例
5. 提交 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目仓库: [GitHub Repository]
- 问题反馈: [Issues]
- 文档更新: [Wiki]

---

**提示**: 更多详细信息和最新更新，请查看 [docs](docs) 目录下的文档。