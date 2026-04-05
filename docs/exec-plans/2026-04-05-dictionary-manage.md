# 数据字典管理 实施计划

**日期：** 2026-04-05
**设计文档：** docs/plans/2026-04-05-dictionary-manage-design.md
**目标：** 为管理员后台添加数据字典管理功能，支持对字典数据的增删改查和状态管理
**架构：** Controller → Service → Mapper → Database
**技术栈：** Spring Boot + MyBatis-Plus + FreeMarker + Bootstrap

---

## 任务列表

### 任务 1: 创建数据字典实体类 (Entity)

**目标：** 创建 Dictionary 实体类映射 dictionary 表
**预计耗时：** 3 分钟

**步骤：**
1. 创建 `src/main/java/org/example/namelist/entity/Dictionary.java`
2. 定义字段：id, code, name, item, itemName, flag, status
3. 使用 MyBatis-Plus 注解映射表名和字段

```java
package org.example.namelist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("dictionary")
public class Dictionary {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String code;      // 字典编码
    private String name;      // 字典名称
    private String item;     // 字典项
    private String itemName;  // 字典项名称
    private Integer flag;     // 标识
    private Integer status;   // 状态 0-禁用 1-启用
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

**验证：** `mvn compile` 编译通过

---

### 任务 2: 创建数据字典 Mapper

**目标：** 创建 DictionaryMapper 接口
**预计耗时：** 2 分钟

**步骤：**
1. 创建 `src/main/java/org/example/namelist/mapper/DictionaryMapper.java`
2. 继承 BaseMapper<Dictionary>

```java
package org.example.namelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.namelist.entity.Dictionary;

@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {
}
```

**验证：** `mvn compile` 编译通过

---

### 任务 3: 创建数据字典服务层 (Service)

**目标：** 创建 DictionaryService 业务逻辑层
**预计耗时：** 5 分钟

**步骤：**
1. 创建 `src/main/java/org/example/namelist/service/DictionaryService.java`
2. 实现 IService<Dictionary> 接口
3. 创建 `src/main/java/org/example/namelist/service/impl/DictionaryServiceImpl.java`
4. 实现字典项的增删改查和状态切换业务逻辑

```java
// DictionaryService.java
package org.example.namelist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.namelist.entity.Dictionary;

public interface DictionaryService extends IService<Dictionary> {
    // 字典项业务方法声明
}
```

```java
// DictionaryServiceImpl.java
package org.example.namelist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.namelist.mapper.DictionaryMapper;
import org.example.namelist.entity.Dictionary;
import org.example.namelist.service.DictionaryService;
import org.springframework.stereotype.Service;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> 
    implements DictionaryService {
    // 业务实现
}
```

**验证：** `mvn compile` 编译通过

---

### 任务 4: 创建数据字典控制器 (Controller)

**目标：** 创建 DictionaryController 处理前端请求
**预计耗时：** 5 分钟

**步骤：**
1. 创建 `src/main/java/org/example/namelist/controller/DictionaryController.java`
2. 实现以下接口：
   - GET /admin/dictionary/list - 字典列表
   - GET /admin/dictionary/add - 添加页
   - GET /admin/dictionary/edit/{id} - 编辑页
   - POST /admin/dictionary/save - 保存
   - POST /admin/dictionary/delete/{id} - 删除
   - POST /admin/dictionary/toggle/{id} - 切换状态

```java
@Controller
@RequestMapping("/admin/dictionary")
public class DictionaryController {
    // 控制器实现
}
```

**验证：** `mvn compile` 编译通过

---

### 任务 5: 创建前端模板页面

**目标：** 创建字典管理的列表页和表单页
**预计耗时：** 8 分钟

**步骤：**
1. 创建 `src/main/resources/templates/admin/dictionary-manage.html`
   - 列表展示
   - 搜索筛选
   - 分页
   - 状态切换按钮
   - 新增/编辑/删除操作

2. 创建 `src/main/resources/templates/admin/dictionary-form.html`
   - 表单字段
   - 表单验证
   - 提交逻辑

**验证：** 页面可正常访问

---

### 任务 6: 更新侧边栏菜单

**目标：** 在侧边栏添加数据字典菜单项
**预计耗时：** 3 分钟

**步骤：**
1. 修改 `src/main/resources/templates/common/sidebar.html`
2. 添加菜单项链接到 `/admin/dictionary/list`

**验证：** 菜单链接可正常访问

---

## 进度汇总

| 任务 | 状态 | 说明 |
|------|------|------|
| 1. 实体类 | ✅ | 已完成 |
| 2. Mapper | ✅ | 已完成 |
| 3. Service | ✅ | 已完成 |
| 4. Controller | ✅ | 已完成 |
| 5. 前端模板 | ✅ | 已完成 |
| 6. 侧边栏菜单 | ✅ | 已完成 |

**总耗时：** 约 26 分钟（实际）
