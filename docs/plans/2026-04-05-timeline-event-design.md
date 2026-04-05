# 历史人物时间线与事件关联系统设计方案

**版本**: v1.0  
**日期**: 2026-04-05  
**状态**: 已设计，待实施

---

## 一、项目概述

### 1.1 目标
为现有「名人名单管理系统」新增两个核心功能模块，提升网站的历史教育价值和用户吸引力：
1. **历史人物时间线** - 按年代横向展示历史人物分布
2. **历史事件关联** - 双向关联人物与历史事件

### 1.2 现有系统分析
- **技术栈**: Spring Boot + MyBatis-Plus + MySQL + FreeMarker + 阿里云OSS
- **现有功能**: 正面/反面人物管理、分类管理、图片上传
- **目标用户**: 历史爱好者、教育工作者、普通网民

---

## 二、数据库设计

### 2.1 新增数据表

#### 2.1.1 历史时期表 (period)
```sql
CREATE TABLE period (
    code VARCHAR(20) PRIMARY KEY COMMENT '时期编码，如 MODERN',
    name VARCHAR(50) NOT NULL COMMENT '时期名称，如 近代史',
    start_year INT NOT NULL COMMENT '开始年份',
    end_year INT NOT NULL COMMENT '结束年份',
    order_num INT COMMENT '排序号',
    description VARCHAR(500) COMMENT '时期简介',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '历史时期表';
```

#### 2.1.2 历史事件表 (event)
```sql
CREATE TABLE event (
    id VARCHAR(20) PRIMARY KEY COMMENT '事件ID，如 EVT001',
    name VARCHAR(100) NOT NULL COMMENT '事件名称',
    event_date DATE COMMENT '事件日期',
    period_code VARCHAR(20) COMMENT '所属时期',
    location VARCHAR(100) COMMENT '事件地点',
    brief_desc VARCHAR(500) COMMENT '简要描述（100字内）',
    full_desc TEXT COMMENT '详细描述',
    significance VARCHAR(20) DEFAULT 'ORDINARY' COMMENT '重要程度：MAJOR/ORDINARY',
    photos TEXT COMMENT '多张配图，JSON数组',
    status TINYINT DEFAULT 1 COMMENT '状态：1-显示，0-隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_period (period_code),
    INDEX idx_event_date (event_date)
) COMMENT '历史事件表';
```

#### 2.1.3 人物-事件关联表 (person_event)
```sql
CREATE TABLE person_event (
    id INT PRIMARY KEY AUTO_INCREMENT,
    person_id VARCHAR(20) NOT NULL COMMENT '人物ID',
    person_type VARCHAR(10) NOT NULL COMMENT '人物类型：HERO/VILLAIN',
    event_id VARCHAR(20) NOT NULL COMMENT '事件ID',
    role_desc VARCHAR(100) COMMENT '在事件中的角色',
    contribution VARCHAR(500) COMMENT '贡献描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_person_event (person_id, event_id),
    INDEX idx_event (event_id)
) COMMENT '人物事件关联表';
```

### 2.2 预置数据

#### 时期数据
```sql
INSERT INTO period (code, name, start_year, end_year, order_num) VALUES
('ANCIENT', '古代史', -3000, 1840, 1),
('MODERN', '近代史', 1840, 1949, 2),
('CONTEM', '当代史', 1949, 2000, 3),
('NEW_ERA', '新时代', 2000, 2100, 4);
```

#### 事件数据示例
```sql
INSERT INTO event (id, name, event_date, period_code, brief_desc, significance) VALUES
('EVT001', '南昌起义', '1927-08-01', 'MODERN', '中国共产党独立领导武装斗争的开始', 'MAJOR'),
('EVT002', '抗日战争', '1937-07-07', 'MODERN', '全民族抗击日本侵略的正义战争', 'MAJOR'),
('EVT003', '抗美援朝', '1950-10-01', 'CONTEM', '中国人民志愿军赴朝作战', 'MAJOR'),
('EVT004', '两弹一星', '1964-10-16', 'CONTEM', '原子弹、氢弹成功研制', 'MAJOR');
```

#### 关联数据示例
```sql
INSERT INTO person_event (person_id, person_type, event_id, role_desc, contribution) VALUES
('MAR00001', 'HERO', 'EVT005', '爆破手', '舍身炸碉堡'),
('SCI00001', 'HERO', 'EVT004', '技术总负责', '中国航天事业奠基人'),
('GEN00001', 'HERO', 'EVT001', '总指挥', '领导起义部队');
```

---

## 三、前端页面设计

### 3.1 页面结构
```
前台页面
├── /timeline              历史时间线（新增入口）
├── /events                历史事件库（新增）
├── /events/{id}          事件详情页（新增）
└── /hero/{id}            人物详情页（增强：显示关联事件）
    /villain/{id}
```

### 3.2 功能描述

#### 3.2.1 历史时间线页面 (/timeline)
- 横向时间轴展示，以10年为单位
- 时期切换标签（古代/近代/当代/新时代）
- 年代区间筛选
- 人物卡片展示（点击查看详情）
- 事件标记点（点击查看事件卡片）

#### 3.2.2 历史事件库 (/events)
- 事件列表展示
- 重要程度筛选
- 时期筛选
- 关键词搜索
- 分页展示

#### 3.2.3 事件详情页 (/events/{id})
- 事件基本信息
- 详细描述
- 历史意义
- 参与人物列表（点击跳转人物详情）
- 相关图片展示

#### 3.2.4 增强版人物详情页
- 新增「参与的历史事件」区块
- 事件卡片展示（含角色描述）

---

## 四、API 接口设计

### 4.1 接口列表
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /timeline | 获取时间线数据（含时期和人物分布） |
| GET | /timeline/persons | 按年代区间获取人物 |
| GET | /events | 获取事件列表 |
| GET | /events/{id} | 获取事件详情（含关联人物） |
| GET | /events/{id}/persons | 获取事件的参与人物 |
| GET | /person/{id}/events | 获取人物参与的事件 |
| GET | /periods | 获取所有时期 |

### 4.2 响应格式
所有接口返回统一格式：
```json
{
  "code": 200,
  "data": {},
  "message": "success"
}
```

---

## 五、后台管理功能

### 5.1 新增菜单
```
后台管理
├── 历史事件管理（新增）
│   ├── 事件列表
│   ├── 添加事件
│   └── 编辑事件
├── 事件-人物关联管理（新增）
│   └── 关联列表 / 添加关联
└── 时期管理（新增）
    └── 时期列表 / 编辑
```

### 5.2 功能要点
- 事件CRUD管理
- 关联关系维护（事件↔人物）
- 时期基础数据管理

---

## 六、非功能性设计

### 6.1 性能优化
- Redis缓存：时间线数据缓存1小时，事件列表缓存30分钟
- CDN加速：图片资源使用OSS+CDN
- 数据库索引优化

### 6.2 SEO优化
- 页面Meta标签优化
- JSON-LD结构化数据
- 站点地图生成

### 6.3 错误处理
- 统一异常处理
- 友好错误提示
- 日志记录规范

---

## 七、实施计划

### Phase 1: 数据层
1. 创建数据库表
2. 编写Mapper接口
3. 配置MyBatis映射

### Phase 2: 服务层
1. 实现PeriodService
2. 实现EventService
3. 实现关联查询

### Phase 3: 控制层
1. 后台管理API
2. 前台展示API
3. 时间线数据接口

### Phase 4: 前端页面
1. 后台管理页面
2. 时间线页面
3. 事件库页面
4. 人物详情页增强

---

## 八、风险与注意事项

1. **数据迁移**：确保现有数据不受影响
2. **向后兼容**：不破坏现有API
3. **性能测试**：大数据量下的时间线渲染性能
4. **用户体验**：移动端适配

---

*本设计方案由 Brainstorming 技能辅助生成*
