# 投票功能（点赞/点踩）设计方案

**日期**: 2026-04-06  
**状态**: 已批准  
**方案**: 方案A - 在现有表添加字段

## 1. 需求概述

### 1.1 功能描述
- **正面人物（HeroPerson）**: 支持点赞功能
- **反面人物（VillainPerson）**: 支持点踩功能
- **前端首页**: 显示点赞/点踩按钮和数量
- **后台管理页**: 显示点赞/点踩次数

### 1.2 技术约束
- 使用方案A：直接在 `hero_person` 和 `villain_person` 表添加字段
- 前端使用Cookie限制重复投票（简单防刷）
- 后台仅展示数据，不支持手动修改

## 2. 数据库变更

### 2.1 hero_person 表新增字段
```sql
ALTER TABLE hero_person ADD COLUMN likes INT NOT NULL DEFAULT 0 COMMENT '点赞数' AFTER status;
```

### 2.2 villain_person 表新增字段
```sql
ALTER TABLE villain_person ADD COLUMN dislikes INT NOT NULL DEFAULT 0 COMMENT '点踩数' AFTER status;
```

## 3. 代码变更清单

### 3.1 实体层
| 文件 | 变更 |
|------|------|
| `HeroPerson.java` | 添加 `likes` 字段 |
| `VillainPerson.java` | 添加 `dislikes` 字段 |

### 3.2 控制器层
| 文件 | 变更 |
|------|------|
| `PublicController.java` | 添加投票API端点 |

**新增API端点**:
- `POST /api/vote/like/{id}` - 正面人物点赞
- `POST /api/vote/dislike/{id}` - 反面人物点踩

### 3.3 服务层
| 文件 | 变更 |
|------|------|
| `PersonService.java` | 添加投票业务方法 |

### 3.4 安全配置
| 文件 | 变更 |
|------|------|
| `SecurityConfig.java` | `/api/vote/**` 加入白名单 |

### 3.5 前端模板
| 文件 | 变更 |
|------|------|
| `index.html` | 添加点赞/点踩按钮UI |
| `hero-manage.html` | 显示点赞数列 |
| `villain-manage.html` | 显示点踩数列 |

## 4. API设计

### 4.1 点赞接口
```
POST /api/vote/like/{id}
Request: 无
Response: { "success": true, "likes": 123 }
Error:   { "success": false, "message": "已点赞" }
```

### 4.2 点踩接口
```
POST /api/vote/dislike/{id}
Request: 无
Response: { "success": true, "dislikes": 45 }
Error:   { "success": false, "message": "已点踩" }
```

## 5. 防刷机制

### 5.1 实现方式
- 使用Cookie存储已投票记录
- Cookie格式: `votes={"liked":["id1","id2"],"disliked":["id3"]}`
- Cookie有效期: 365天

### 5.2 限制规则
- 同一用户对同一人物只能投票一次
- 服务端不校验（简化实现），依赖前端Cookie判断

## 6. UI设计

### 6.1 首页卡片按钮
- **正面人物**: 红色爱心图标 + 数字 (❤ 123)
- **反面人物**: 蓝色拇指向下图标 + 数字 (👎 45)
- 点击后: 数字+1，按钮变灰，禁止再点击

### 6.2 后台管理列表
- 新增"点赞/点踩数"列，只读显示

## 7. 测试要点

1. 点赞后数字正确递增
2. 重复点击提示"已点赞"
3. 刷新页面后投票状态保持（Cookie）
4. 后台管理页面正确显示投票数
5. 未登录用户可正常投票
