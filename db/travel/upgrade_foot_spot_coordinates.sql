-- 已有数据库执行一次；全新建库无需执行，travel.sql 已包含这些字段。
ALTER TABLE `foot_spot`
    ADD COLUMN `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT 'WGS84经度' AFTER `visit_time`,
    ADD COLUMN `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT 'WGS84纬度' AFTER `longitude`,
    ADD COLUMN `travel_note` VARCHAR(1000) DEFAULT NULL COMMENT '旅行心得' AFTER `latitude`;
