CREATE DATABASE IF NOT EXISTS `travel_database`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `travel_database`.`flight` LIKE `springdatabase`.`flight`;
CREATE TABLE IF NOT EXISTS `travel_database`.`train` LIKE `springdatabase`.`train`;
CREATE TABLE IF NOT EXISTS `travel_database`.`train_station` LIKE `springdatabase`.`train_station`;
CREATE TABLE IF NOT EXISTS `travel_database`.`foot_spot` LIKE `springdatabase`.`foot_spot`;

INSERT IGNORE INTO `travel_database`.`flight` SELECT * FROM `springdatabase`.`flight`;
INSERT IGNORE INTO `travel_database`.`train` SELECT * FROM `springdatabase`.`train`;
INSERT IGNORE INTO `travel_database`.`train_station` SELECT * FROM `springdatabase`.`train_station`;
INSERT IGNORE INTO `travel_database`.`foot_spot` SELECT * FROM `springdatabase`.`foot_spot`;

-- 验证新库运行正常后，再手动删除 springdatabase 中的四张旧表。
