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