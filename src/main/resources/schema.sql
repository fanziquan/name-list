-- =====================================================
-- 名人名单管理系统 - 数据库表结构脚本
-- 数据库: fzqtest
-- =====================================================

-- 如果表已存在，先删除
DROP TABLE IF EXISTS person_extend;
DROP TABLE IF EXISTS villain_person;
DROP TABLE IF EXISTS hero_person;
DROP TABLE IF EXISTS dictionary;
DROP TABLE IF EXISTS admin_user;

-- =====================================================
-- 0. 数据字典表 (dictionary)
-- =====================================================
CREATE TABLE dictionary (
    dict_code VARCHAR(50) NOT NULL COMMENT '字典编码',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典中文名称',
    dict_item VARCHAR(50) NOT NULL COMMENT '字典项(编码)',
    item_name VARCHAR(100) NOT NULL COMMENT '字典项中文名',
    mark VARCHAR(20) NOT NULL COMMENT '标识: HERO(正面人物)/VILLAIN(反面人物)',
    status varchar(2) DEFAULT '1' COMMENT '是否启用: 1-启用, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`dict_code`,`dict_item`) USING BTREE,
    INDEX idx_dict_code (dict_code),
    INDEX idx_mark (mark),
    INDEX idx_status (status)
) COMMENT '数据字典表';

-- 插入人物分类字典数据
INSERT INTO dictionary (dict_code, dict_name, dict_item, item_name, mark, status) VALUES
-- 正面人物分类
('CATEGORY', '人物分类', 'MAR', '烈士', 'HERO', 1),
('CATEGORY', '人物分类', 'SCI', '科学家', 'HERO', 1),
('CATEGORY', '人物分类', 'GEN', '将军', 'HERO', 1),
('CATEGORY', '人物分类', 'PAT', '爱国志士', 'HERO', 1),
-- 反面人物分类
('CATEGORY', '人物分类', 'TRA', '汉奸', 'VILLAIN', 1),
('CATEGORY', '人物分类', 'PUB', '公知', 'VILLAIN', 1),
('CATEGORY', '人物分类', 'ESP', '间谍', 'VILLAIN', 1),
('CATEGORY', '人物分类', 'COR', '贪官', 'VILLAIN', 1);

-- =====================================================
-- 1. 管理员表 (admin_user)
-- =====================================================
CREATE TABLE admin_user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '自增ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(Bcrypt加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    role VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色: ADMIN/SUPER_ADMIN',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_status (status)
) COMMENT '管理员表';

-- 插入默认管理员用户 (用户名: admin, 密码: admin1)
INSERT INTO admin_user (username, password, nickname, role) VALUES
('admin', '$2a$10$plFTr927lGQw8SV5F0d/qu49b73ZBL7tXlJraqjxY.FOV18BWaHw6', '超级管理员', 'SUPER_ADMIN');

-- =====================================================
-- 2. 正面人物表 (hero_person)
-- =====================================================
CREATE TABLE hero_person (
    id VARCHAR(20) PRIMARY KEY COMMENT '主键: 分类代码(3位) + 自增数字(5位)',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    birth_year INT COMMENT '出生年份',
    death_year INT COMMENT '逝世年份',
    category VARCHAR(20) NOT NULL COMMENT '分类: MAR(烈士)/SCI(科学家)/GEN(将军)/PAT(爱国志士)',
    nationality VARCHAR(50) COMMENT '国籍',
    brief_intro VARCHAR(500) COMMENT '简要介绍(200字内)',
    full_bio TEXT COMMENT '完整事迹(大段描述)',
    photo_url VARCHAR(500) COMMENT '照片URL(OSS)',
    extend_id VARCHAR(20) COMMENT '拓展信息ID(关联extend表)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-显示, 0-隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_status (status)
) COMMENT '正面人物表';

-- 插入一些示例数据
INSERT INTO hero_person (id, name, birth_year, death_year, category, nationality, brief_intro, full_bio, status) VALUES
('MAR00001', '董存瑞', 1929, 1948, 'MAR', '中国', '中国人民解放军著名战斗英雄', '董存瑞（1929年—1948年），河北省怀来县人，中国人民解放军著名战斗英雄。1948年5月25日，在解放隆化的战斗中，为炸毁敌人碉堡，他用手托起炸药包，拉响导火索，与敌人同归于尽，年仅19岁。他的英雄事迹激励了无数中华儿女。', 1),
('SCI00001', '钱学森', 1911, 2009, 'SCI', '中国', '中国航天事业奠基人', '钱学森（1911年—2009年），浙江杭州人，中国航天事业的奠基人，被誉为“中国航天之父”、“中国导弹之父”、“中国自动化控制之父”和“火箭之王”。他为中国航天事业的发展做出了巨大贡献。', 1),
('GEN00001', '朱德', 1886, 1976, 'GEN', '中国', '中华人民共和国十大元帅之首', '朱德（1886年—1976年），四川仪陇人，中华人民共和国十大元帅之首，中国人民解放军的主要创建者和领导人之一。他参加了南昌起义、井冈山斗争，是红军总司令、八路军总指挥、人民解放军总司令。', 1);

-- =====================================================
-- 3. 反面人物表 (villain_person)
-- =====================================================
CREATE TABLE villain_person (
    id VARCHAR(20) PRIMARY KEY COMMENT '主键: 分类代码(3位) + 自增数字(5位)',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    birth_year INT COMMENT '出生年份',
    death_year INT COMMENT '逝世年份',
    category VARCHAR(20) NOT NULL COMMENT '分类: TRA(汉奸)/PUB(公知)/ESP(间谍)/COR(贪官)',
    nationality VARCHAR(50) COMMENT '国籍',
    brief_intro VARCHAR(500) COMMENT '简要介绍(200字内)',
    full_bio TEXT COMMENT '完整事迹(大段描述)',
    photo_url VARCHAR(500) COMMENT '照片URL(OSS)',
    extend_id VARCHAR(20) COMMENT '拓展信息ID(关联extend表)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-显示, 0-隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_status (status)
) COMMENT '反面人物表';

-- 插入一些示例数据
INSERT INTO villain_person (id, name, birth_year, death_year, category, nationality, brief_intro, full_bio, status) VALUES
('TRA00001', '汪精卫', 1883, 1944, 'TRA', '中国', '大汉奸', '汪精卫（1883年—1944年），浙江绍兴人，著名的大汉奸。抗日战争期间，投靠日本侵略者，成立伪国民政府，成为日本侵华的主要帮凶。他的叛国行为受到了人民的唾弃。', 1);

-- =====================================================
-- 4. 人物拓展信息表 (person_extend)
-- =====================================================
CREATE TABLE person_extend (
    person_id VARCHAR(20) PRIMARY KEY COMMENT '关联人物ID(作为主键)',
    person_type VARCHAR(10) NOT NULL COMMENT '人物类型: HERO/VILLAIN',
    achievements TEXT COMMENT '主要成就',
    evaluation TEXT COMMENT '历史评价',
    anecdotes TEXT COMMENT '逸闻趣事',
    sources TEXT COMMENT '资料来源',
    extra_field1 VARCHAR(500) COMMENT '扩展字段1',
    extra_field2 VARCHAR(500) COMMENT '扩展字段2',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_person_type (person_type)
) COMMENT '人物拓展信息表';

-- =====================================================
-- 5. ID生成器辅助表
-- 用于生成分类ID
-- =====================================================
DROP TABLE IF EXISTS id_sequence;

CREATE TABLE id_sequence (
    category_code VARCHAR(20) PRIMARY KEY COMMENT '分类代码',
    current_value INT DEFAULT 0 COMMENT '当前最大值',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT 'ID序列表';

-- 初始化序列
INSERT INTO id_sequence (category_code, current_value) VALUES
('MAR', 1),
('SCI', 1),
('GEN', 1),
('PAT', 1),
('TRA', 1),
('PUB', 1),
('ESP', 1),
('COR', 1);

-- =====================================================
-- 分类代码说明
-- =====================================================
-- 正面人物分类:
--   MAR = 烈士 (Martyr)
--   SCI = 科学家 (Scientist)
--   GEN = 将军 (General)
--   PAT = 爱国志士 (Patriot)
--
-- 反面人物分类:
--   TRA = 汉奸 (Traitor)
--   PUB = 公知 (Public Intellectual)
--   ESP = 间谍 (Espionage)
--   COR = 贪官 (Corrupt Official)
--
-- 拓展表分类:
--   EXT = 拓展信息 (Extension)
