-- 已有数据库执行一次；全新建库无需执行，travel.sql 已包含这些字段。

USE `springdatabase`;
ALTER TABLE `foot_spot`
    ADD COLUMN `visit_type` VARCHAR(20) NOT NULL DEFAULT 'travel' COMMENT 'travel旅行/transit途经' AFTER `spot_type`,
    ADD COLUMN `image_url` VARCHAR(500) DEFAULT NULL COMMENT '地点缩略图地址' AFTER `travel_note`;
