USE springdatabase;

ALTER TABLE `foot_spot`
    CHANGE COLUMN `address` `travel_note` VARCHAR(1000) DEFAULT NULL COMMENT '旅行心得';
