CREATE DATABASE `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;

-- 航线表
CREATE TABLE `flight` (
                          `flight_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '航线记录ID',
                          `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
                          `flight_no` VARCHAR(20) NOT NULL COMMENT '航班号',
                          `aircraft_reg` VARCHAR(10) NOT NULL COMMENT '飞机注册号',
                          `aircraft_type` VARCHAR(30) NOT NULL COMMENT '飞机型号',
                          `seat_no` VARCHAR(10) NOT NULL COMMENT '座位号',

                          `departure_icao` CHAR(4) NOT NULL COMMENT '始发地ICAO码',
                          `departure_airport` VARCHAR(50) NOT NULL COMMENT '始发地机场',
                          `departure_terminal` VARCHAR(20) NOT NULL COMMENT '始发地航站楼',
                          `takeoff_time` DATETIME NOT NULL COMMENT '起飞时间',
                          `boarding_method` VARCHAR(20) NOT NULL COMMENT '登机方式',

                          `arrival_icao` CHAR(4) NOT NULL COMMENT '目的地ICAO码',
                          `arrival_airport` VARCHAR(50) NOT NULL COMMENT '目的地机场',
                          `arrival_terminal` VARCHAR(20) NOT NULL COMMENT '目的地航站楼',
                          `landing_time` DATETIME NOT NULL COMMENT '降落时间',
                          `deplaning_method` VARCHAR(20) NOT NULL COMMENT '下机方式',

                          `stopover_airport` VARCHAR(50) DEFAULT NULL COMMENT '经停地',

                          `flight_distance_km` INT NOT NULL COMMENT '航程(km)',

                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`flight_id`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_flight_no` (`flight_no`),
                          FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='航线表';

-- 铁路表
CREATE TABLE `train` (
                         `train_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '铁路记录ID',
                         `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',

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

-- 途径车站表
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

-- 足迹地点表
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





INSERT INTO `flight` (
    user_id, flight_no, aircraft_reg, aircraft_type,
    departure_airport, departure_terminal, departure_icao, takeoff_time, boarding_method,
    arrival_airport, arrival_terminal, arrival_icao, landing_time, deplaning_method,
    stopover_airport, flight_distance_km, seat_no
) VALUES
-- 1 MU2387
(1, 'MU2387', 'B2356', 'A320-14W',
 '西安咸阳', 'T3', 'ZLXY', '2016-07-16 08:23:00', '廊桥',
 '南京禄口', 'T2', 'ZSNJ', '2016-07-16 09:57:00', '廊桥',
 NULL,  1104, '41A'),

-- 2 MU2152
(1, 'MU2152', 'B6925', 'A321-323',
 '上海浦东', 'T1', 'ZSPD', '2016-07-26 09:04:00', '摆渡车',
 '西安咸阳', 'T3', 'ZLXY', '2016-07-26 11:05:00', '廊桥',
 NULL,  1351, '45K'),

-- 3 MU2320
(1, 'MU2320', 'B6616', 'A320-200',
 '深圳宝安', 'T3', 'ZGSZ', '2023-08-15 14:14:00', '廊桥',
 '西安咸阳', 'T3', 'ZLXY', '2023-08-15 18:30:00', '廊桥',
 'ZSGS',  1635, '49L'),

-- 4 MU2387
(1, 'MU2387', 'B9905', 'A321-231(SL)',
 '西安咸阳', 'T5', 'ZLXY', '2025-03-26 08:06:00', '廊桥',
 '南京禄口', 'T2', 'ZSNJ', '2025-03-26 09:46:00', '廊桥',
 NULL,  1140, '39A');


INSERT INTO `train` (
    user_id, train_no, train_type, train_model,
    start_station, end_station, origin_station, terminal_station,
    departure_datetime, arrival_datetime, seat_no, seat_class, mileage_km
) VALUES
      (1, 'G824/G821',    '高速动车',  'CRH380AL',     '西安北',   '广州南',   '西安北',   '深圳北',     '2023-08-08 09:56:00', '2023-08-08 19:16:00', '10车12B',        '二等座',       NULL),
      (1, 'G6215',        '高速动车',  'CR400AF',      '广州南',   '深圳北',   '广州南',   '深圳北',     '2023-08-11 15:43:00', '2023-08-11 16:19:00', '10车03B',        '二等座',       NULL),
      (1, 'G115',         '高速动车',  'CRH380B',      '南京南',   '上海虹桥', '北京南',   '杭州东',     '2016-07-18 13:41:00', '2016-07-18 14:48:00', '14车11A',        '二等座',       NULL),
      (1, 'D2696',        '动车',      'CRH5G',        '咸阳西',   '西安北',   '嘉峪关南', '西安北',     '2023-10-03 17:30:00', '2023-10-03 17:34:00', '07车18A',        '二等座',       NULL),
      (1, 'D216',         '动车',      'CR200J3-C',    '南京',     '西安',     '上海',     '兰州',       '2025-04-01 19:47:00', '2025-04-02 07:49:00', '05车11上铺',    '二等卧',       NULL),
      (1, 'G2064',        '高速动车',  'CRH380B',      '西安北',   '青岛',     '西安北',   '青岛',       '2025-07-23 09:37:00', '2025-07-23 15:50:00', '06车04A',        '二等座',       NULL),
      (1, 'G1855',        '高速动车',  'CRH381B',      '青岛北',   '西安北',   '青岛北',   '西安北',     '2025-07-27 13:43:00', '2025-07-27 21:21:00', '06车03A',        '二等座',       NULL),
      (1, 'K912',         '快速列车',  NULL,           '西安',     '济南',     '西安',     '青岛',       '2019-07-23 12:12:00', '2019-07-24 05:20:00', '14车004号上铺', '新空调硬卧',   NULL),
      (1, 'K911',         '快速列车',  NULL,           '兖州',     '西安',     '青岛',     '西安',       '2019-07-28 18:21:00', '2019-07-29 11:39:00', '13车002号上铺', '新空调硬卧',   NULL),
      (1, 'D124',         '动车',      'CR200J1-C',    '西安',     '南京',     '西安',     '上海',       '2025-08-25 21:16:00', '2025-08-26 09:27:00', '14车019号下铺','二等卧',       NULL),
      (1, 'G1928/G1929',  '高速动车',  'CRH380AL',     '南京南',   '西安北',   '上海虹桥', '西安北',     '2026-02-08 14:00:00', '2026-02-08 20:00:00', '07车06F',        '二等座',       NULL),
      (1, 'G96/G93',      '高速动车',  'CR400BF-S',    '西安北',   '南京南',   '西安北',   '上海虹桥',   '2026-03-01 15:32:00', '2026-03-01 20:19:00', '02车04F',        '二等座',       NULL),
      (1, 'G7431',        '高速动车',  'CRH380B',      '南京南',   '苏州北',   '合肥南',   '温州南',     '2026-04-04 09:23:00', '2026-04-04 10:27:00', '13车10D',        '二等座',       NULL),
      (1, 'Z164',         '直快列车',  '25T',          '苏州',     '南京南',   '上海',     '拉萨',       '2026-04-05 19:24:00', '2026-04-05 21:13:00', '12车19号上铺',  '硬卧',         NULL);


