CREATE DATABASE `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;

CREATE TABLE `sys_area` (
                            `code` VARCHAR(32) NOT NULL COMMENT '行政区划代码（唯一）',
                            `name` VARCHAR(64) NOT NULL COMMENT '名称（省/市/区）',
                            `level` TINYINT NOT NULL COMMENT '层级：1=省 2=市 3=区',
                            `type` VARCHAR(32) DEFAULT '' COMMENT '类型：省/市/市辖区/县/自治州',
                            `parent_code` VARCHAR(32) DEFAULT NULL COMMENT '上级行政代码',
                            PRIMARY KEY (`code`),
                            KEY `idx_parent_code` (`parent_code`),
                            KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行政区划表（省市区三级）';