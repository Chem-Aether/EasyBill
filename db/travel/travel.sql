CREATE DATABASE IF NOT EXISTS `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;

-- 航线表
CREATE TABLE `flight` (
                          `flight_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '航线记录ID',
                          `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
                          `flight_no` VARCHAR(20) NOT NULL COMMENT '航班号',
                          `company` VARCHAR(50) NOT NULL COMMENT '航空公司',
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
                                 `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
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
CREATE TABLE `foot_spot` (
                             `spot_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '足迹ID',
                             `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                             `adcode` CHAR(6) NOT NULL COMMENT '行政区划代码',
                             `spot_name` VARCHAR(100) NOT NULL COMMENT '景点名称',
                             `spot_type` VARCHAR(20) NOT NULL COMMENT '类型',
                             `visit_time` DATE DEFAULT NULL COMMENT '到访日期',
                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`spot_id`),
                             KEY `idx_user_adcode` (`user_id`,`adcode`),
                             KEY `idx_visit_time` (`visit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户足迹表';





INSERT INTO `flight` (
    user_id, flight_no, aircraft_reg, company, aircraft_type,
    departure_airport, departure_terminal, departure_icao, takeoff_time, boarding_method,
    arrival_airport, arrival_terminal, arrival_icao, landing_time, deplaning_method,
    stopover_airport, flight_distance_km, seat_no
) VALUES
-- 1 MU2387
(1, 'MU2387', 'B2356', '中国东方航空','A320-14W',
 '西安咸阳', 'T3', 'ZLXY', '2016-07-16 08:23:00', '廊桥',
 '南京禄口', 'T2', 'ZSNJ', '2016-07-16 09:57:00', '廊桥',
 NULL,  1104, '41A'),

-- 2 MU2152
(1, 'MU2152', 'B6925', '中国东方航空','A321-323',
 '上海浦东', 'T1', 'ZSPD', '2016-07-26 09:04:00', '摆渡车',
 '西安咸阳', 'T3', 'ZLXY', '2016-07-26 11:05:00', '廊桥',
 NULL,  1351, '45K'),

-- 3 MU2320
(1, 'MU2320', 'B6616', '中国东方航空','A320-200',
 '深圳宝安', 'T3', 'ZGSZ', '2023-08-15 14:14:00', '廊桥',
 '西安咸阳', 'T3', 'ZLXY', '2023-08-15 18:30:00', '廊桥',
 'ZSGS',  1635, '49L'),

-- 4 MU2387
(1, 'MU2387', 'B9905', '中国东方航空','A321-231(SL)',
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


INSERT INTO foot_spot (user_id, adcode, spot_name, spot_type, visit_time)
VALUES
-- 上海
(1, '310101', '南京路步行街', '景点', NULL),
(1, '310101', '上海人民广场', '景点', NULL),
(1, '310101', '外滩商业街', '商业街', NULL),
(1, '310112', '上海虹桥交通枢纽', '交通枢纽', NULL),
(1, '310114', '同济大学嘉定校区', '校园', NULL),
(1, '310115', '上海迪士尼', '景点', NULL),
(1, '310115', '陆家嘴商业街', '商业街', NULL),
(1, '310115', '上海浦东国际机场', '机场', NULL),

-- 陕西 西安
(1, '610113', '长安区', '区域', NULL),
(1, '610115', '西安科技大学', '校园', NULL),
(1, '610115', '秦始皇陵兵马俑', '景点', NULL),
(1, '610117', '长庆二中', '校园', NULL),
(1, '610117', '泾渭分明观景台', '景点', NULL),
(1, '610102', '钟楼', '景点', NULL),
(1, '610102', '北大街', '街道', NULL),
(1, '610102', '大明宫国家遗址公园', '景点', NULL),
(1, '610103', '西北大学', '校园', NULL),
(1, '610103', '碑林博物馆', '景点', NULL),
(1, '610103', '西安城墙', '景点', NULL),
(1, '610104', '回民街', '商业街', NULL),
(1, '610111', '西安后海', '景点', NULL),
(1, '610111', '奥体中心', '景点', NULL),
(1, '610112', '浐灞国家湿地公园', '公园', NULL),
(1, '610113', '大唐芙蓉园', '景点', NULL),
(1, '610113', '大雁塔', '景点', NULL),

-- 陕西 咸阳
(1, '610423', '乐华欢乐世界', '景点', NULL),
(1, '610423', '崇文塔', '景点', NULL),
(1, '610402', '西安咸阳国际机场', '机场', NULL),

-- 陕西 渭南
(1, '610581', '司马迁墓', '景点', NULL),
(1, '610581', '党家村', '景点', NULL),

-- 陕西 商洛
(1, '611026', '柞水溶洞', '景点', NULL),

-- 陕西 安康
(1, '610923', '旬阳坝', '景点', NULL),

-- 山东 济南
(1, '370102', '大明湖景区', '景点', NULL),
(1, '370102', '趵突泉', '景点', NULL),

-- 山东 济宁
(1, '370832', '水伯梁山风景区', '景点', NULL),
(1, '370881', '孔子故里园', '景点', NULL),
(1, '370881', '孔子博物馆', '博物馆', NULL),

-- 山东 泰安
(1, '370902', '岱庙', '景点', NULL),
(1, '370911', '泰山', '景点', NULL),

-- 山东 青岛
(1, '370202', '青岛栈桥', '景点', NULL),
(1, '370202', '西陵峡三路', '街道', NULL),
(1, '370202', '宫崎骏漫画街', '景点', NULL),
(1, '370202', '信号山公园', '公园', NULL),
(1, '370202', '小青岛公园', '公园', NULL),
(1, '370202', '琴屿路', '街道', NULL),
(1, '370202', '八大关', '景点', NULL),
(1, '370202', '太平角公园', '公园', NULL),
(1, '370202', '五四广场', '广场', NULL),
(1, '370202', '奥帆中心', '景点', NULL),
(1, '370202', '情人坝', '景点', NULL),
(1, '370202', '燕儿岛公园', '公园', NULL),
(1, '370211', '小麦岛', '公园', NULL),
(1, '370211', '石老人海水浴场', '海滩', NULL),
(1, '370211', '崂山风景区', '景点', NULL),
(1, '370203', '台东步行街', '商业街', NULL),
(1, '370212', '凤凰山公园', '公园', NULL),
(1, '370212', '金沙滩啤酒节', '景点', NULL),
(1, '370212', '唐岛湾', '公园', NULL),
(1, '370212', '渔鸣嘴村', '景点', NULL),

-- 江苏 南京
(1, '320115', '南京禄口国际机场', '机场', NULL),
(1, '320115', '中国药科大学江宁校区', '校园', NULL),
(1, '320115', '百家湖商业街', '商业街', NULL),
(1, '320102', '南京总统府', '景点', NULL),
(1, '320104', '夫子庙', '景点', NULL),
(1, '320104', '秦淮河夜游', '景点', NULL),
(1, '320105', '南京大屠杀遇难同胞纪念馆', '博物馆', NULL),
(1, '320114', '雨花台烈士陵园', '景点', NULL),

-- 江苏 苏州
(1, '320501', '平江路', '景点', NULL),
(1, '320501', '寒山寺', '景点', NULL),
(1, '320501', '西园寺', '景点', NULL),
(1, '320501', '留园', '景点', NULL),
(1, '320501', '狮子林', '景点', NULL),
(1, '320501', '苏州站', '火车站', NULL),
(1, '320507', '苏州北站', '火车站', NULL),
(1, '320506', '东方之门', '景点', NULL),
(1, '320506', '太湖', '景点', NULL),
(1, '320505', '斜塘老街', '景点', NULL),

-- 广东 广州
(1, '440113', '广州南站', '火车站', NULL),
(1, '440113', '长隆欢乐世界', '景点', NULL),
(1, '440113', '奥园广场', '广场', NULL),
(1, '440103', '沙面岛', '景点', NULL),
(1, '440103', '永庆坊', '景点', NULL),
(1, '440103', '滘口地铁站', '地铁站', NULL),
(1, '440104', '圣心大教堂', '景点', NULL),
(1, '440105', '广州塔', '景点', NULL),
(1, '440105', '猎德大桥', '景点', NULL),
(1, '440105', '中山大学(广州校区南校园)', '校园', NULL),
(1, '440106', '海心沙亚运公园', '公园', NULL),

-- 广东 深圳
(1, '440304', '岗厦北地铁站', '地铁站', NULL),
(1, '440305', '蛇口邮轮码头', '码头', NULL),
(1, '440305', '华侨城欢乐海岸', '景点', NULL),
(1, '440305', '深圳人才公园', '公园', NULL),
(1, '440306', '深圳宝安国际机场', '机场', NULL),
(1, '440306', '深圳欢乐港湾-“湾区之光”摩天轮', '景点', NULL),
(1, '440306', '九方广场', '广场', NULL),
(1, '440308', '中英街', '景点', NULL),
(1, '440308', '大梅沙滨海公园', '公园', NULL),
(1, '440309', '深圳北站', '火车站', NULL),

(1, '441900', '东莞市', '城市', NULL),
(1, '810000', '香港', '城市', NULL),
(1, '820000', '澳门', '城市', NULL),
(1, '440600', '佛山市', '城市', NULL),
(1, '441800', '清远市', '城市', NULL),
(1, '440200', '韶关市', '城市', NULL),
(1, '431000', '郴州市', '城市', NULL),
(1, '430400', '衡阳市', '城市', NULL),
(1, '360800', '吉安市', '城市', NULL),
(1, '430300', '湘潭市', '城市', NULL),
(1, '430200', '株洲市', '城市', NULL),
(1, '430100', '长沙市', '城市', NULL),
(1, '430600', '岳阳市', '城市', NULL),
(1, '421200', '咸宁市', '城市', NULL),
(1, '420100', '武汉市', '城市', NULL),
(1, '420900', '孝感市', '城市', NULL),
(1, '411500', '信阳市', '城市', NULL),
(1, '411700', '驻马店市', '城市', NULL),
(1, '411100', '漯河市', '城市', NULL),
(1, '411000', '许昌市', '城市', NULL),
(1, '410100', '郑州市', '城市', NULL),
(1, '410200', '开封市', '城市', NULL),
(1, '411400', '商丘市', '城市', NULL),
(1, '410300', '洛阳市', '城市', NULL),
(1, '411200', '三门峡市', '城市', NULL),
(1, '611000', '商洛市', '城市', NULL),
(1, '410700', '新乡市', '城市', NULL),
(1, '410600', '鹤壁市', '城市', NULL),
(1, '410900', '濮阳市', '城市', NULL),
(1, '341300', '宿州市', '城市', NULL),
(1, '320300', '徐州市', '城市', NULL),
(1, '340300', '蚌埠市', '城市', NULL),
(1, '341100', '滁州市', '城市', NULL),
(1, '321100', '镇江市', '城市', NULL),
(1, '320400', '常州市', '城市', NULL),
(1, '320200', '无锡市', '城市', NULL),
(1, '370400', '枣庄市', '城市', NULL),
(1, '371300', '临沂市', '城市', NULL),
(1, '320700', '连云港市', '城市', NULL);


INSERT INTO `train_station` (train_id, user_id, station_name, station_order) VALUES
-- 1 路线
(1, 1, '西安北', 1),
(1, 1, '渭南北', 2),
(1, 1, '三门峡南', 3),
(1, 1, '洛阳龙门', 4),
(1, 1, '巩义南', 5),
(1, 1, '郑州', 6),
(1, 1, '许昌东', 7),
(1, 1, '驻马店西', 8),
(1, 1, '信阳东', 9),
(1, 1, '武汉', 10),
(1, 1, '岳阳东', 11),
(1, 1, '长沙南', 12),
(1, 1, '株洲西', 13),
(1, 1, '耒阳西', 14),
(1, 1, '清远', 15),
(1, 1, '广州北', 16),
(1, 1, '广州南', 17),

-- 2 路线
(2, 1, '广州南', 1),
(2, 1, '虎门', 2),
(2, 1, '深圳北', 3),

-- 3 路线
(3, 1, '南京南', 1),
(3, 1, '苏州北', 2),
(3, 1, '上海虹桥', 3),

-- 4 路线
(4, 1, '咸阳西', 1),
(4, 1, '西安北', 2),

-- 5 路线
(5, 1, '南京', 1),
(5, 1, '蚌埠', 2),
(5, 1, '徐州', 3),
(5, 1, '郑州', 4),
(5, 1, '西安', 5),

-- 6 路线
(6, 1, '西安北', 1),
(6, 1, '洛阳龙门', 2),
(6, 1, '郑州东', 3),
(6, 1, '新乡东', 4),
(6, 1, '濮阳东', 5),
(6, 1, '茌平南', 6),
(6, 1, '济南西', 7),
(6, 1, '淄博北', 8),
(6, 1, '青岛', 9),

-- 7 路线
(7, 1, '青岛北', 1),
(7, 1, '日照西', 2),
(7, 1, '岚山西', 3),
(7, 1, '赣榆', 4),
(7, 1, '连云港', 5),
(7, 1, '东海县', 6),
(7, 1, '新沂南', 7),
(7, 1, '邳州东', 8),
(7, 1, '徐州东', 9),
(7, 1, '永城北', 10),
(7, 1, '砀山南', 11),
(7, 1, '商丘', 12),
(7, 1, '民权北', 13),
(7, 1, '开封北', 14),
(7, 1, '郑州东', 15),
(7, 1, '洛阳龙门', 16),
(7, 1, '华山北', 17),
(7, 1, '西安北', 18),

-- 8 路线
(8, 1, '西安', 1),
(8, 1, '渭南', 2),
(8, 1, '华山', 3),
(8, 1, '灵宝', 4),
(8, 1, '三门峡西', 5),
(8, 1, '三门峡', 6),
(8, 1, '洛阳', 7),
(8, 1, '巩义', 8),
(8, 1, '郑州', 9),
(8, 1, '开封', 10),
(8, 1, '兰考', 11),
(8, 1, '民权', 12),
(8, 1, '商丘', 13),
(8, 1, '徐州', 14),
(8, 1, '泰山', 15),
(8, 1, '济南', 16),

-- 9 路线
(9, 1, '兖州', 1),
(9, 1, '邹城', 2),
(9, 1, '滕州', 3),
(9, 1, '枣庄西', 4),
(9, 1, '徐州', 5),
(9, 1, '砀山', 6),
(9, 1, '商丘', 7),
(9, 1, '郑州', 8),
(9, 1, '洛阳', 9),
(9, 1, '三门峡', 10),
(9, 1, '灵宝', 11),
(9, 1, '华山', 12),
(9, 1, '渭南', 13),
(9, 1, '西安', 14),

-- 10 路线
(10, 1, '西安', 1),
(10, 1, '渭南', 2),
(10, 1, '郑州', 3),
(10, 1, '商丘', 4),
(10, 1, '徐州', 5),
(10, 1, '蚌埠', 6),
(10, 1, '南京', 7),

-- 11 路线
(11, 1, '南京南', 1),
(11, 1, '定远', 2),
(11, 1, '蚌埠南', 3),
(11, 1, '徐州东', 4),
(11, 1, '永城北', 5),
(11, 1, '砀山南', 6),
(11, 1, '商丘', 7),
(11, 1, '郑州东', 8),
(11, 1, '洛阳龙门', 9),
(11, 1, '灵宝西', 10),
(11, 1, '渭南北', 11),
(11, 1, '西安北', 12),

-- 12 路线
(12, 1, '西安北', 1),
(12, 1, '三门峡南', 2),
(12, 1, '洛阳龙门', 3),
(12, 1, '郑州东', 4),
(12, 1, '徐州东', 5),
(12, 1, '南京南', 6),

-- 13 路线
(13, 1, '南京南', 1),
(13, 1, '镇江南', 2),
(13, 1, '常州北', 3),
(13, 1, '苏州北', 4),

-- 14 路线
(14, 1, '苏州', 1),
(14, 1, '无锡', 2),
(14, 1, '南京', 3);
