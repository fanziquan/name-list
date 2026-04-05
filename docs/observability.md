# Name-List 可观测性设计

## 概述
可观测性是现代软件系统的重要特性，包括日志、指标和追踪三个维度。本文档描述 Name-List 项目的可观测性设计。

## 监控目标

### 业务指标
1. **人物数据统计**
   - 总人物数量（按分类）
   - 每日新增人物数
   - 人物信息完整度
   - 图片上传成功率

2. **用户行为统计**
   - 活跃管理员数量
   - 登录频率和时长
   - 操作分布（增删改查）

3. **系统使用情况**
   - 页面访问量
   - API 调用频率
   - 搜索关键词统计

### 技术指标
1. **应用健康度**
   - 服务可用性
   - 响应时间（P50, P95, P99）
   - 错误率
   - JVM 内存和 GC

2. **数据库性能**
   - 连接池使用率
   - 慢查询数量
   - 查询响应时间
   - 锁等待时间

3. **外部依赖**
   - OSS 上传成功率
   - OSS 响应时间
   - 外部 API 可用性

## 日志策略

### 日志级别
```
ERROR: 系统错误，需要立即处理
WARN:  警告信息，需要关注但不紧急
INFO:  重要业务操作和系统状态
DEBUG: 调试信息，开发环境使用
TRACE: 详细追踪，性能分析使用
```

### 结构化日志
```json
{
  "timestamp": "2026-04-05T11:30:00Z",
  "level": "INFO",
  "logger": "org.example.namelist.controller.AdminController",
  "thread": "http-nio-8080-exec-1",
  "message": "用户登录成功",
  "context": {
    "userId": "admin001",
    "ip": "192.168.1.100",
    "userAgent": "Chrome/120.0.0.0",
    "sessionId": "session_abc123"
  },
  "duration_ms": 45,
  "tags": ["auth", "login"]
}
```

### 关键日志点
1. **用户认证**
   - 登录成功/失败
   - 会话创建/销毁
   - 权限验证

2. **业务操作**
   - 人物信息增删改查
   - 图片上传/删除
   - 批量操作

3. **系统事件**
   - 应用启动/关闭
   - 配置变更
   - 定时任务执行

4. **错误处理**
   - 异常捕获和记录
   - 业务逻辑错误
   - 外部服务错误

## 指标收集

### Spring Boot Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: name-list
      environment: production
```

### 自定义指标
1. **业务指标**
   ```java
   // 人物操作计数器
   Counter.builder("namelist.person.operations")
          .tag("type", "create")
          .tag("category", "hero")
          .register(registry);
   
   // 图片上传指标
   Timer.builder("namelist.oss.upload.duration")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(registry);
   ```

2. **性能指标**
   - HTTP 请求耗时分布
   - 数据库查询耗时
   - 缓存命中率
   - JVM 性能指标

3. **健康检查**
   - 数据库连接状态
   - OSS 连接状态
   - 磁盘空间检查
   - 内存使用率

## 追踪系统

### 分布式追踪
1. **Trace ID 传播**
   - HTTP 头: `X-Trace-Id`
   - 日志关联
   - 跨服务追踪

2. **Span 定义**
   ```java
   // Web 请求 Span
   Span webSpan = tracer.buildSpan("http_request")
                       .withTag("http.method", "POST")
                       .withTag("http.path", "/api/person")
                       .start();
   
   // 数据库操作 Span
   Span dbSpan = tracer.buildSpan("database_query")
                      .asChildOf(webSpan)
                      .withTag("db.operation", "insert")
                      .withTag("db.table", "hero_person")
                      .start();
   ```

3. **关键追踪点**
   - HTTP 请求处理
   - 数据库事务
   - 外部服务调用
   - 异步任务执行

## 告警策略

### 告警级别
- **P0 (紧急)**: 服务不可用，需要立即处理
- **P1 (高)**: 性能严重下降，影响用户体验
- **P2 (中)**: 潜在问题，需要关注
- **P3 (低)**: 信息性告警，记录即可

### 告警规则
1. **可用性告警**
   ```yaml
   - alert: ServiceDown
     expr: up{job="name-list"} == 0
     for: 1m
     labels:
       severity: P0
     annotations:
       summary: "Name-List 服务不可用"
   ```

2. **性能告警**
   ```yaml
   - alert: HighResponseTime
     expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 1
     for: 5m
     labels:
       severity: P1
     annotations:
       summary: "API 响应时间超过 1 秒"
   ```

3. **错误率告警**
   ```yaml
   - alert: HighErrorRate
     expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) > 0.05
     for: 5m
     labels:
       severity: P1
     annotations:
       summary: "HTTP 5xx 错误率超过 5%"
   ```

### 告警通知
- **P0**: 短信 + 电话 + Slack/钉钉
- **P1**: 邮件 + Slack/钉钉
- **P2**: Slack/钉钉
- **P3**: 日志记录

## 可视化仪表板

### Grafana 仪表板设计
1. **业务概览面板**
   - 实时用户数
   - 今日操作统计
   - 数据增长趋势
   - 系统健康状态

2. **性能监控面板**
   - 响应时间趋势
   - 错误率监控
   - 资源使用情况
   - 数据库性能

3. **业务分析面板**
   - 人物分类分布
   - 操作类型分析
   - 用户活跃度
   - 峰值时间分析

### 关键图表
1. **时序图**
   - 响应时间变化
   - 错误率趋势
   - 请求量波动

2. **统计图**
   - 操作类型分布
   - 人物分类占比
   - 用户地域分布

3. **状态图**
   - 服务健康状态
   - 数据库连接状态
   - 外部依赖状态

## 实现方案

### 技术栈选择
1. **日志收集**
   - 日志框架: Logback
   - 日志格式: JSON
   - 收集工具: Filebeat/Fluentd
   - 存储: Elasticsearch
   - 查询: Kibana

2. **指标收集**
   - 框架: Micrometer
   - 暴露: Spring Boot Actuator
   - 收集: Prometheus
   - 存储: Prometheus TSDB
   - 可视化: Grafana

3. **追踪系统**
   - 框架: OpenTelemetry
   - 收集: Jaeger
   - 存储: Elasticsearch
   - 可视化: Jaeger UI

### 部署架构
```
应用实例 → 日志/指标/追踪 → 收集器 → 存储 → 可视化/告警
    ↓           ↓           ↓        ↓         ↓
   App      Filebeat    Logstash   ES       Kibana
   App      Micrometer  Prometheus TSDB     Grafana
   App      OTel Agent  Jaeger     ES       Jaeger UI
```

## 最佳实践

### 日志最佳实践
1. **避免过度日志**
   - 生产环境关闭 DEBUG/TRACE
   - 敏感信息脱敏
   - 避免日志循环

2. **结构化日志**
   - 使用 JSON 格式
   - 包含上下文信息
   - 统一字段命名

3. **性能考虑**
   - 异步日志写入
   - 合理的日志级别
   - 日志轮转策略

### 指标最佳实践
1. **指标命名规范**
   - 使用点号分隔
   - 包含单位信息
   - 统一前缀

2. **标签使用**
   - 避免高基数标签
   - 使用枚举值
   - 标签值规范化

3. **采集频率**
   - 业务指标: 1分钟
   - 性能指标: 15秒
   - 系统指标: 5秒

### 追踪最佳实践
1. **Trace 采样**
   - 生产环境: 10% 采样率
   - 开发环境: 100% 采样率
   - 错误请求: 100% 采样

2. **Span 粒度**
   - 关键业务操作
   - 外部服务调用
   - 耗时超过阈值的操作

3. **上下文传播**
   - 跨线程传播 Trace ID
   - 异步任务关联父 Span
   - RPC 调用传播上下文

## 维护和优化

### 日常维护
1. **日志管理**
   - 定期清理过期日志
   - 监控日志存储空间
   - 检查日志收集状态

2. **指标管理**
   - 监控指标基数增长
   - 优化指标采集频率
   - 清理无用指标

3. **追踪管理**
   - 调整采样率
   - 优化 Span 粒度
   - 监控追踪数据量

### 性能优化
1. **日志优化**
   - 使用异步 Appender
   - 优化日志格式
   - 压缩日志文件

2. **指标优化**
   - 聚合相似指标
   - 使用直方图替代计数器
   - 优化 Prometheus 查询

3. **追踪优化**
   - 调整采样策略
   - 优化 Span 收集
   - 压缩追踪数据

## 故障排查指南

### 常见问题排查
1. **服务不可用**
   ```bash
   # 检查应用日志
   tail -f logs/application.log | grep ERROR
   
   # 检查健康端点
   curl http://localhost:8080/actuator/health
   
   # 检查数据库连接
   mysql -h host -u user -p
   ```

2. **性能下降**
   ```bash
   # 检查响应时间指标
   curl http://localhost:8080/actuator/metrics/http.server.requests
   
   # 检查慢查询
   grep "slow query" logs/application.log
   
   # 检查 JVM 状态
   jstat -gc <pid>
   ```

3. **数据不一致**
   ```bash
   # 检查业务日志
   grep "data conflict" logs/application.log
   
   # 检查事务日志
   grep "transaction" logs/application.log
   
   # 检查数据库状态
   show processlist;
   ```

### 工具支持
1. **日志分析**
   - Kibana 查询和可视化
   - grep/awk 命令行分析
   - 日志分析脚本

2. **指标分析**
   - Grafana 仪表板
   - Prometheus 查询
   - 自定义告警规则

3. **追踪分析**
   - Jaeger UI 追踪查询
   - 分布式调用链分析
   - 性能瓶颈定位