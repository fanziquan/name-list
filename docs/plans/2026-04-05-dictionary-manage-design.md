# 数据字典管理 功能设计文档

**日期：** 2026-04-05
**作者：** CodeBuddy
**状态：** 已实施

## 背景

管理员后台需要能够维护系统中使用的数据字典，包括正面人物分类、反面人物分类等。这些分类数据目前存储在 `dictionary` 表中，但没有提供可视化的管理界面。

## 需求描述

- 管理员可以在后台查看所有字典项
- 支持按字典编码筛选和关键词搜索
- 管理员可以添加、编辑、删除字典项
- 管理员可以启用/禁用字典项
- 每个字典项包含：字典编码、字典名称、字典项、字典项名称、标识、状态

## 方案选择

### 方案 A：独立管理界面（推荐）
- 创建专门的数据字典管理模块
- 与现有后台管理风格保持一致
- 使用统一的侧边栏菜单

### 方案 B：复用现有编辑页面
- 在人物管理中嵌入字典编辑
- 缺点：职责不清晰，扩展性差

## 技术设计

### 涉及的文件变更

| 文件 | 变更类型 | 说明 |
|------|---------|------|
| `service/DictionaryService.java` | 新增 | 数据字典服务层 |
| `controller/DictionaryController.java` | 新增 | 数据字典控制器 |
| `templates/admin/dictionary-manage.html` | 新增 | 字典列表页面 |
| `templates/admin/dictionary-form.html` | 新增 | 字典表单页面 |
| `templates/common/sidebar.html` | 修改 | 添加数据字典菜单项 |

### 数据库变更
- 无需修改，使用现有 `dictionary` 表

### API 变更

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /admin/dictionary/list | 字典列表 |
| GET | /admin/dictionary/add | 添加字典页 |
| GET | /admin/dictionary/edit/{id} | 编辑字典页 |
| POST | /admin/dictionary/save | 保存字典 |
| POST | /admin/dictionary/delete/{id} | 删除字典 |
| POST | /admin/dictionary/toggle/{id} | 切换状态 |

## 影响范围

- 新增 1 个菜单入口（数据字典）
- 侧边栏菜单新增第 9 个菜单项

## 风险和注意事项

- 字典编码和字典项组合唯一，需要在保存时校验
- 禁用字典项可能影响使用该分类的人物数据显示
