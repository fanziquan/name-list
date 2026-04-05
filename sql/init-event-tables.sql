-- =============================================
-- 历史时间线和事件关联功能 - 数据库初始化脚本
-- 运行前请确保已创建 namelist 数据库
-- =============================================

USE namelist;

-- =============================================
-- 1. 历史时期表 (period)
-- =============================================
CREATE TABLE IF NOT EXISTS `period` (
    `code` VARCHAR(20) NOT NULL COMMENT '时期编码',
    `name` VARCHAR(50) NOT NULL COMMENT '时期名称',
    `start_year` INT COMMENT '开始年份',
    `end_year` INT COMMENT '结束年份',
    `order_num` INT DEFAULT 0 COMMENT '排序号',
    `description` VARCHAR(500) COMMENT '时期简介',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='历史时期表';

-- =============================================
-- 2. 历史事件表 (event)
-- =============================================
CREATE TABLE IF NOT EXISTS `event` (
    `id` VARCHAR(20) NOT NULL COMMENT '事件ID',
    `name` VARCHAR(100) NOT NULL COMMENT '事件名称',
    `event_date` DATE COMMENT '事件日期',
    `period_code` VARCHAR(20) COMMENT '所属时期编码',
    `location` VARCHAR(200) COMMENT '事件地点',
    `brief_desc` VARCHAR(500) COMMENT '简要描述(100字内)',
    `full_desc` TEXT COMMENT '详细描述',
    `significance` VARCHAR(20) DEFAULT 'ORDINARY' COMMENT '重要程度: MAJOR-重大, ORDINARY-普通',
    `photo_url` VARCHAR(500) COMMENT '事件配图URL(OSS)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-显示, 0-隐藏',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_event_date` (`event_date`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_significance` (`significance`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_event_period` FOREIGN KEY (`period_code`) REFERENCES `period`(`code`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='历史事件表';

-- =============================================
-- 3. 人物-事件关联表 (person_event)
-- =============================================
CREATE TABLE IF NOT EXISTS `person_event` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `person_id` VARCHAR(20) NOT NULL COMMENT '人物ID',
    `person_type` VARCHAR(10) NOT NULL COMMENT '人物类型: HERO-正面人物, VILLAIN-反面人物',
    `event_id` VARCHAR(20) NOT NULL COMMENT '事件ID',
    `role_desc` VARCHAR(100) COMMENT '在事件中的角色描述',
    `contribution` VARCHAR(500) COMMENT '贡献描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_person_event` (`person_id`, `event_id`),
    KEY `idx_event_id` (`event_id`),
    KEY `idx_person_id` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人物-事件关联表';

-- =============================================
-- 4. 预置时期数据
-- =============================================
INSERT INTO `period` (`code`, `name`, `start_year`, `end_year`, `order_num`, `description`, `status`) VALUES
('ANCIENT', '古代史', -3000, 1840, 1, '从远古到鸦片战争前的历史时期', 1),
('MODERN', '近代史', 1840, 1949, 2, '从鸦片战争到新中国成立', 1),
('CONTEM', '当代史', 1949, 2000, 3, '新中国成立后到20世纪末', 1),
('NEW_ERA', '新时代', 2000, 2100, 4, '21世纪至今', 1)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `start_year` = VALUES(`start_year`),
    `end_year` = VALUES(`end_year`),
    `description` = VALUES(`description`);

-- =============================================
-- 5. 预置历史事件数据
-- =============================================
INSERT INTO `event` (`id`, `name`, `event_date`, `period_code`, `location`, `brief_desc`, `significance`, `status`) VALUES
('EVT001', '南昌起义', '1927-08-01', 'MODERN', '江西南昌', '中国共产党独立领导武装斗争的开始，创建人民军队的标志', 'MAJOR', 1),
('EVT002', '抗日战争', '1937-07-07', 'MODERN', '全国', '全民族抗击日本侵略的正义战争', 'MAJOR', 1),
('EVT003', '抗美援朝', '1950-10-01', 'CONTEM', '朝鲜', '中国人民志愿军赴朝作战', 'MAJOR', 1),
('EVT004', '两弹一星', '1964-10-16', 'CONTEM', '全国', '原子弹、氢弹成功爆炸，人造卫星上天', 'MAJOR', 1),
('EVT005', '解放隆化', '1948-05-25', 'MODERN', '河北隆化', '董存瑞舍身炸碉堡的战斗', 'MAJOR', 1),
('EVT006', '开国大典', '1949-10-01', 'CONTEM', '北京', '中华人民共和国中央人民政府成立', 'MAJOR', 1),
('EVT007', '改革开放', '1978-12-18', 'CONTEM', '全国', '开启中国改革开放新时期', 'MAJOR', 1)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `event_date` = VALUES(`event_date`),
    `brief_desc` = VALUES(`brief_desc`);

-- =============================================
-- 6. 预置人物-事件关联数据
-- =============================================
INSERT INTO `person_event` (`person_id`, `person_type`, `event_id`, `role_desc`, `contribution`) VALUES
-- 董存瑞 → 解放隆化
('MAR00001', 'HERO', 'EVT005', '爆破手', '舍身炸碉堡，为部队打开前进通道'),
-- 朱德 → 南昌起义
('GEN00001', 'HERO', 'EVT001', '总指挥', '领导起义部队'),
-- 钱学森 → 两弹一星
('SCI00001', 'HERO', 'EVT004', '技术总负责', '中国航天事业奠基人'),
-- 汪精卫 → 抗日战争(作为反派)
('VIL00001', 'VILLAIN', 'EVT002', '投降派', '投敌叛国，建立伪政权')
ON DUPLICATE KEY UPDATE
    `role_desc` = VALUES(`role_desc`),
    `contribution` = VALUES(`contribution`);

-- =============================================
-- 7. 显示创建结果
-- =============================================
SELECT '数据库表创建完成!' AS result;

-- 查看创建的表
SHOW TABLES LIKE 'period';
SHOW TABLES LIKE 'event';
SHOW TABLES LIKE 'person_event';
