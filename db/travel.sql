CREATE DATABASE `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;

-- 2. 航线表
CREATE TABLE `flight` (
                          `flight_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '航线记录ID',
                          `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
                          `flight_no` VARCHAR(20) NOT NULL COMMENT '航班号',
                          `aircraft_reg` VARCHAR(10) NOT NULL COMMENT '飞机注册号',
                          `aircraft_type` VARCHAR(30) NOT NULL COMMENT '飞机型号',
                          `departure_airport` VARCHAR(50) NOT NULL COMMENT '始发地',
                          `departure_icao` CHAR(4) NOT NULL COMMENT '始发地ICAO码',
                          `arrival_airport` VARCHAR(50) NOT NULL COMMENT '目的地',
                          `arrival_icao` CHAR(4) NOT NULL COMMENT '目的地ICAO码',
                          `stopover_airport` VARCHAR(50) DEFAULT NULL COMMENT '经停地',
                          `takeoff_time` DATETIME NOT NULL COMMENT '起飞时间',
                          `landing_time` DATETIME NOT NULL COMMENT '降落时间',
                          `flight_duration` VARCHAR(20) NOT NULL COMMENT '航时',
                          `flight_distance_km` INT NOT NULL COMMENT '航程(km)',
                          `boarding_method` VARCHAR(20) NOT NULL COMMENT '登机方式',
                          `deplaning_method` VARCHAR(20) NOT NULL COMMENT '下机方式',
                          `seat_no` VARCHAR(10) NOT NULL COMMENT '座位号',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`flight_id`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_flight_no` (`flight_no`),
                          FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='航线表';

-- 3. 铁路表
CREATE TABLE `train` (
                         `train_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '铁路记录ID',
                         `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
                         `train_no` VARCHAR(20) NOT NULL COMMENT '车次',
                         `train_type` VARCHAR(20) NOT NULL COMMENT '列车类型',
                         `train_model` VARCHAR(30) DEFAULT NULL COMMENT '车型',
                         `start_station` VARCHAR(50) NOT NULL COMMENT '起点站',
                         `end_station` VARCHAR(50) NOT NULL COMMENT '目的站',
                         `origin_station` VARCHAR(50) NOT NULL COMMENT '始发站',
                         `terminal_station` VARCHAR(50) NOT NULL COMMENT '终点站',
                         `departure_datetime` DATETIME NOT NULL COMMENT '发车日期时间',
                         `arrival_datetime` DATETIME NOT NULL COMMENT '到达日期时间',
                         `seat_no` VARCHAR(20) NOT NULL COMMENT '座位号',
                         `seat_class` VARCHAR(20) NOT NULL COMMENT '座位等级',
                         `mileage_km` INT DEFAULT NULL COMMENT '里程(km)',
                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`train_id`),
                         KEY `idx_user_id` (`user_id`),
                         KEY `idx_train_no` (`train_no`),
                         FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='铁路表';

-- 4. 途径车站表
CREATE TABLE `train_station` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `train_id` BIGINT NOT NULL COMMENT '关联铁路表车次ID',
                                 `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
                                 `station_name` VARCHAR(50) NOT NULL COMMENT '车站名称',
                                 `station_order` INT NOT NULL COMMENT '车站顺序',
                                 `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_train_id` (`train_id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_station_name` (`station_name`),
                                 FOREIGN KEY (`train_id`) REFERENCES `train`(`train_id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='铁路途经车站表';

-- 5. 足迹地点表
CREATE TABLE `footprint_spot` (
                                  `spot_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '足迹地点ID',
                                  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
                                  `province` VARCHAR(50) NOT NULL COMMENT '省份',
                                  `city` VARCHAR(50) NOT NULL COMMENT '城市',
                                  `district` VARCHAR(50) NOT NULL COMMENT '区县',
                                  `spot_name` VARCHAR(100) NOT NULL COMMENT '地点',
                                  `spot_tye` VARCHAR(20) NOT NULL COMMENT '地点类型',
                                  `visit_time` DATE DEFAULT NULL COMMENT '到访日期',
                                  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`spot_id`),
                                  KEY `idx_user_id` (`user_id`),
                                  KEY `idx_province_city` (`province`, `city`),
                                  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='足迹地点表';