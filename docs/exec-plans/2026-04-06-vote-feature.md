# 投票功能实施计划

**日期**: 2026-04-06  
**关联设计文档**: [vote-feature-design.md](../plans/2026-04-06-vote-feature-design.md)  
**状态**: 待执行

## 实施步骤

### Step 1: 数据库变更
- [ ] 1.1 创建SQL迁移脚本 `sql/2026-04-06-vote-feature.sql`
- [ ] 1.2 添加 hero_person.likes 字段
- [ ] 1.3 添加 villain_person.dislikes 字段

### Step 2: 实体层修改
- [ ] 2.1 HeroPerson.java 添加 likes 属性和getter/setter
- [ ] 2.2 VillainPerson.java 添加 dislikes 属性和getter/setter

### Step 3: 服务层扩展
- [ ] 3.1 PersonService.java 添加 likeHero(id) 方法
- [ ] 3.2 PersonService.java 添加 dislikeVillain(id) 方法
- [ ] 3.3 使用 LambdaUpdateWrapper 原子递增

### Step 4: 控制器层添加API
- [ ] 4.1 PublicController.java 添加 POST /api/vote/like/{id}
- [ ] 4.2 PublicController.java 添加 POST /api/vote/dislike/{id}

### Step 5: 安全配置更新
- [ ] 5.1 SecurityConfig.java 放行 /api/vote/** 路径

### Step 6: 前端首页UI
- [ ] 6.1 index.html 添加点赞按钮（正面人物卡片）
- [ ] 6.2 index.html 添加点踩按钮（反面人物卡片）
- [ ] 6.3 添加Cookie防刷逻辑JS函数
- [ ] 6.4 页面加载时恢复投票状态

### Step 7: 后台管理页面
- [ ] 7.1 hero-manage.html 添加点赞数列
- [ ] 7.2 villain-manage.html 添加点踩数列

### Step 8: 编译验证
- [ ] 8.1 执行 mvn compile 确保无编译错误

### Step 9: 功能测试
- [ ] 9.1 测试点赞接口
- [ ] 9.2 测试点踩接口
- [ ] 9.3 测试重复投票拦截
- [ ] 9.4 验证后台显示正确

## 文件变更汇总

| 序号 | 文件路径 | 变更类型 |
|------|----------|----------|
| 1 | sql/2026-04-06-vote-feature.sql | 新建 |
| 2 | src/main/java/.../entity/HeroPerson.java | 修改 |
| 3 | src/main/java/.../entity/VillainPerson.java | 修改 |
| 4 | src/main/java/.../service/PersonService.java | 修改 |
| 5 | src/main/java/.../controller/PublicController.java | 修改 |
| 6 | src/main/java/.../config/SecurityConfig.java | 修改 |
| 7 | src/main/resources/templates/index.html | 修改 |
| 8 | src/main/resources/templates/admin/hero-manage.html | 修改 |
| 9 | src/main/resources/templates/admin/villain-manage.html | 修改 |

## 风险点

1. **并发安全**: 使用数据库原子递增，非内存操作
2. **Cookie清理**: 用户清除Cookie后可重复投票（可接受）
3. **兼容性**: 新字段有DEFAULT 0，旧数据自动兼容

## 验收标准

- [ ] 首页正面人物卡片显示点赞按钮和数量
- [ ] 首页反面人物卡片显示点踩按钮和数量
- [ ] 点击后数字+1，重复点击被拦截
- [ ] 刷新页面状态保持
- [ ] 后台管理列表显示正确的投票数
- [ ] 编译通过，无错误
