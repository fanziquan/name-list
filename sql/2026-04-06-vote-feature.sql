-- 投票功能数据库变更
-- 日期: 2026-04-06
-- 说明: 为正面人物添加点赞字段，反面人物添加点踩字段

ALTER TABLE hero_person ADD COLUMN likes INT NOT NULL DEFAULT 0 COMMENT '点赞数' AFTER status;
ALTER TABLE villain_person ADD COLUMN dislikes INT NOT NULL DEFAULT 0 COMMENT '点踩数' AFTER status;
