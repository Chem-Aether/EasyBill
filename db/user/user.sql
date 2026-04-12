CREATE DATABASE `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;


CREATE TABLE `user` (
                        `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID号',
                        `account` VARCHAR(50) NOT NULL COMMENT '账号',
                        `user_name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
                        `role` VARCHAR(10) DEFAULT '2' COMMENT '用户类型：sadmin超级管理员；admin管理员；user普通用户',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更改时间',
                        PRIMARY KEY (`user_id`),
                        UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


INSERT INTO user (account, user_name, password, role)
VALUES ('admin', '超级管理员', '$2a$10$Z63Gm5C3kG76dN90bGaYUufIgyCh5N1d1tXH4GpqD.XwLr0Vv5O6a', 'user');