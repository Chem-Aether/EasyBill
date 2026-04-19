CREATE DATABASE `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;

CREATE TABLE `train_stations` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `name` varchar(64) NOT NULL COMMENT '车站名称',
                                  `code` varchar(20) DEFAULT NULL COMMENT '车站电报码/三字码',
                                  `city` varchar(64) DEFAULT NULL COMMENT '城市',
                                  `region` varchar(64) DEFAULT NULL COMMENT '地区',
                                  `province` varchar(32) DEFAULT NULL COMMENT '省份',
                                  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
                                  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',

                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全国火车站数据表';

CREATE TABLE `airport` (
                           `icao` varchar(4) NOT NULL COMMENT 'ICAO机场码',
                           `iata` varchar(3) DEFAULT NULL COMMENT 'IATA机场码',
                           `name` varchar(100) NOT NULL COMMENT '机场名称',
                           `attr` varchar(50) DEFAULT NULL COMMENT '机场属性/标签',
                           `longitude` decimal(12,8) NOT NULL COMMENT '经度',
                           `latitude` decimal(12,8) NOT NULL COMMENT '纬度',
                           `level` varchar(10) DEFAULT NULL COMMENT '机场等级',
                           `city` varchar(50) DEFAULT NULL COMMENT '所在城市',
                           `type` varchar(50) DEFAULT NULL COMMENT '机场类型',

                           PRIMARY KEY (`icao`),
                           UNIQUE KEY `uk_iata` (`iata`),
                           KEY `idx_city` (`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机场信息表';

