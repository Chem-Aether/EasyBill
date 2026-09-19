-- 仅在 mapserver/geo.sqlite 已验证且 Spring Boot 不再使用旧版本代码后执行。
USE springdatabase;

DROP TABLE IF EXISTS airport;
DROP TABLE IF EXISTS train_stations;
DROP TABLE IF EXISTS sys_area;
