CREATE DATABASE IF NOT EXISTS `springdatabase` DEFAULT CHARACTER SET utf8mb4;
USE `springdatabase`;

-- 分类表
DROP TABLE IF EXISTS `bill_category`;
CREATE TABLE `bill_category` (
                                 cate_id CHAR(4) PRIMARY KEY,
                                 class_name VARCHAR(20) NOT NULL,
                                 parent_cate CHAR(2) NOT NULL,
                                 level TINYINT NOT NULL DEFAULT 2,
                                 icon VARCHAR(100) DEFAULT '',
                                 type TINYINT NOT NULL COMMENT '1支出 2收入 3互转',
                                 is_deleted TINYINT DEFAULT 0,
                                 create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 UNIQUE KEY uk_parent_name (parent_cate, class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 账户表
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
                           id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                           account_name VARCHAR(30) NOT NULL,
                           account_icon VARCHAR(50) DEFAULT '',
                           account_type TINYINT NOT NULL COMMENT '1储蓄卡 2信用卡 3支付宝 4微信 5现金 6钱包 9系统账户',
                           balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                           is_default TINYINT NOT NULL DEFAULT 0,
                           status TINYINT NOT NULL DEFAULT 1,
                           user_id INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '所属用户',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入系统账户
INSERT IGNORE INTO account (id, account_name, account_type, user_id)
VALUES
    (998, '外部收入', 9, 0),
    (999, '商家消费', 9, 0);

-- 账单表
DROP TABLE IF EXISTS `bill_record`;
CREATE TABLE `bill_record` (
                               id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                               out_account_id INT UNSIGNED NOT NULL,
                               in_account_id INT UNSIGNED NOT NULL,
                               counterparty_name VARCHAR(100) DEFAULT '',
                               cate_id CHAR(4) NULL,
                               pay_type TINYINT NOT NULL COMMENT '1支出 2收入 3互转',
                               amount DECIMAL(12,2) NOT NULL,
                               currency VARCHAR(20) DEFAULT 'CNY',
                               bill_time DATETIME NOT NULL,
                               commodity VARCHAR(500) DEFAULT '',
                               remark VARCHAR(500) DEFAULT '',
                               user_id INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '所属用户',
                               create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               FOREIGN KEY (out_account_id) REFERENCES account(id),
                               FOREIGN KEY (in_account_id) REFERENCES account(id),
                               FOREIGN KEY (cate_id) REFERENCES bill_category(cate_id),

                               INDEX idx_out_account (out_account_id),
                               INDEX idx_in_account (in_account_id),
                               INDEX idx_cate (cate_id),
                               INDEX idx_bill_time (bill_time),
                               INDEX idx_pay_type (pay_type),
                               INDEX idx_counterparty (counterparty_name),
                               INDEX idx_user (user_id),
                               INDEX idx_user_time (user_id, bill_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 视图
DROP VIEW IF EXISTS `v_bill_detail`;
CREATE VIEW v_bill_detail AS
SELECT
    b.*,
    out_acc.account_name AS out_account_name,
    in_acc.account_name AS in_account_name,
    c.class_name AS category_name,
    c.type AS category_type
FROM bill_record b
         LEFT JOIN account out_acc ON b.out_account_id = out_acc.id
         LEFT JOIN account in_acc ON b.in_account_id = in_acc.id
         LEFT JOIN bill_category c ON b.cate_id = c.cate_id;




USE springdatabase;
-- 收入类 type=2
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('S000','收入','S0',1,'💰',2),
                                                                                      ('S001','生活费','S0',2,'🧧',2),
                                                                                      ('S002','补助','S0',2,'🎁',2),
                                                                                      ('S003','工资','S0',2,'💼',2),
                                                                                      ('S004','奖金','S0',2,'🏆',2),
                                                                                      ('S005','金融','S0',2,'📈',2),
                                                                                      ('S006','退款','S0',2,'💳',2),
                                                                                      ('S007','AA收入','S0',2,'🤝',2),
                                                                                      ('S008','出售物品','S0',2,'🛒',2),
                                                                                      ('S009','收还款','S0',2,'💵',2);

-- 餐饮类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('F000','餐饮','F0',1,'🍽️',1),
                                                                                      ('F001','三餐','F0',2,'🍚',1),
                                                                                      ('F002','水果','F0',2,'🍎',1),
                                                                                      ('F003','蔬菜','F0',2,'🥬',1),
                                                                                      ('F004','甜品','F0',2,'🍰',1),
                                                                                      ('F005','饮品','F0',2,'🥤',1),
                                                                                      ('F006','零食','F0',2,'🍪',1),
                                                                                      ('F007','调味料','F0',2,'🧂',1),
                                                                                      ('F008','肉蛋奶','F0',2,'🥚',1),
                                                                                      ('F009','加工食品','F0',2,'🥫',1);

-- 交通类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('T000','交通','T0',1,'🚗',1),
                                                                                      ('T001','单车','T0',2,'🚲',1),
                                                                                      ('T002','公交/地铁','T0',2,'🚇',1),
                                                                                      ('T003','出租车','T0',2,'🚕',1),
                                                                                      ('T004','铁路','T0',2,'🚄',1),
                                                                                      ('T005','航空','T0',2,'✈️',1),
                                                                                      ('T006','快递运输','T0',2,'📦',1),
                                                                                      ('T007','船舶','T0',2,'🚢',1);

-- 医疗类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('M000','医疗','M0',1,'🏥',1),
                                                                                      ('M001','药品','M0',2,'💊',1),
                                                                                      ('M002','就诊','M0',2,'👨‍⚕️',1),
                                                                                      ('M003','体检','M0',2,'🩺',1),
                                                                                      ('M004','医疗器械','M0',2,'🦽',1),
                                                                                      ('M005','保健品','M0',2,'💪',1);

-- 生活成本类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('L000','生活成本','L0',1,'🏠',1),
                                                                                      ('L001','水电气','L0',2,'💡',1),
                                                                                      ('L002','网费','L0',2,'📶',1),
                                                                                      ('L003','话费','L0',2,'📱',1),
                                                                                      ('L004','物业费','L0',2,'🏢',1),
                                                                                      ('L005','房租/贷','L0',2,'🏡',1);

-- 教育类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('E000','教育','E0',1,'📚',1),
                                                                                      ('E001','书本','E0',2,'📖',1),
                                                                                      ('E002','学费','E0',2,'🎓',1),
                                                                                      ('E003','文具','E0',2,'✏️',1),
                                                                                      ('E004','报名费','E0',2,'📝',1),
                                                                                      ('E005','课程 / 培训','E0',2,'🧑‍🏫',1);

-- 办公类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('O000','办公','O0',1,'💻',1),
                                                                                      ('O001','正版化','O0',2,'🪪',1),
                                                                                      ('O002','技术服务','O0',2,'🔧',1),
                                                                                      ('O003','办公用品','O0',2,'🗂️',1),
                                                                                      ('O004','数字消费','O0',2,'📀',1);

-- 娱乐类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('Y000','娱乐','Y0',1,'🎮',1),
                                                                                      ('Y001','影视演出','Y0',2,'🎬',1),
                                                                                      ('Y002','游戏','Y0',2,'🎮',1),
                                                                                      ('Y003','门票','Y0',2,'🎟️',1),
                                                                                      ('Y004','娱乐社交','Y0',2,'🥳',1),
                                                                                      ('Y005','会员服务','Y0',2,'🪪',1),
                                                                                      ('Y006','场馆费','Y0',2,'🏟️',1),
                                                                                      ('Y007','玩具','Y0',2,'🧸',1);

-- 服饰类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('C000','服饰','C0',1,'👕',1),
                                                                                      ('C001','衣服','C0',2,'👕',1),
                                                                                      ('C002','裤子','C0',2,'👖',1),
                                                                                      ('C003','鞋子','C0',2,'👟',1),
                                                                                      ('C004','内衣','C0',2,'🩲',1),
                                                                                      ('C005','袜子','C0',2,'🧦',1),
                                                                                      ('C006','帽子','C0',2,'🧢',1);

-- 个人护理类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('G000','个人护理','G0',1,'💆',1),
                                                                                      ('G001','美发','G0',2,'💇',1),
                                                                                      ('G002','健身','G0',2,'🏋️',1),
                                                                                      ('G003','美妆','G0',2,'💄',1),
                                                                                      ('G004','按摩疗养','G0',2,'🧖',1),
                                                                                      ('G005','护肤品','G0',2,'🧴',1),
                                                                                      ('G006','护理仪器','G0',2,'🪮',1),
                                                                                      ('G007','滋补养生','G0',2,'🍵',1);

-- 购物类 type=1
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('B000','购物','B0',1,'🛍️',1),
                                                                                      ('B001','饰品','B0',2,'💍',1),
                                                                                      ('B002','日用品','B0',2,'🧴',1),
                                                                                      ('B003','个人护理','B0',2,'🧼',1),
                                                                                      ('B004','居家','B0',2,'🏡',1),
                                                                                      ('B005','电器','B0',2,'🔌',1),
                                                                                      ('B006','3C数码','B0',2,'📱',1),
                                                                                      ('B007','零配件','B0',2,'🔩',1);

-- 互转 & 其他
INSERT INTO `bill_category` (cate_id, class_name, parent_cate, level, icon, type) VALUES
                                                                                      ('H000','互转','H0',1,'🔄',3),
                                                                                      ('X000','其他','X0',1,'📌',1);


-- 插入你的真实账户
INSERT IGNORE INTO account (id, account_name, account_type, user_id, balance) VALUES
                                                                                  (1, '邮政银行(4395)', 1, 1, 3526.80),  -- 储蓄卡
                                                                                  (2, '微信零钱', 4, 1, 1289.50),         -- 微信
                                                                                  (3, '招商银行信用卡', 2, 1, -2360.20);  -- 信用卡


-- 多账号测试账单数据
INSERT INTO bill_record
(out_account_id, in_account_id, counterparty_name, cate_id, pay_type, amount, bill_time, commodity, remark, user_id)
VALUES
-- 1. 邮政银行卡 支出 超市购物
(1, 999, '港佳超市', 'F006', 1, 6.30, '2026-04-01 08:30:00', '矿泉水;巧克力饼干', '早餐零食', 1),

-- 2. 微信 支出 早餐
(2, 999, '包子铺', 'F001', 1, 12.00, '2026-04-01 09:10:00', '肉包;豆浆', '早餐', 1),

-- 3. 邮政 收入 工资
(998, 1, 'XX科技公司', 'S003', 2, 8560.00, '2026-04-01 10:00:00', '4月工资', '工资到账', 1),

-- 4. 信用卡 支出 网购
(3, 999, '淘宝商城', 'B006', 1, 299.00, '2026-04-01 14:20:00', '蓝牙耳机', '数码消费', 1),

-- 5. 微信 支出 打车
(2, 999, '滴滴出行', 'T003', 1, 35.50, '2026-04-01 18:15:00', '出租车费', '下班回家', 1),

-- 6. 邮政 支出 话费
(1, 999, '中国移动', 'L003', 1, 59.00, '2026-04-02 09:30:00', '手机话费', '话费充值', 1),

-- 7. 微信 支出 咖啡
(2, 999, '星巴克', 'F005', 1, 42.00, '2026-04-02 14:00:00', '拿铁', '下午茶', 1),

-- 8. 微信 收入 红包
(998, 2, '朋友转账', 'S007', 2, 200.00, '2026-04-02 20:10:00', 'AA收款', '聚餐AA', 1),

-- 9. 邮政 → 信用卡 互转（还款）
(1, 3, '信用卡还款', 'H000', 3, 2000.00, '2026-04-03 10:00:00', '信用卡账单还款', '自动还款', 1),

-- 10. 微信 支出 水果
(2, 999, '百果园', 'F002', 1, 48.80, '2026-04-03 16:40:00', '苹果;香蕉', '买水果', 1),

-- 11. 信用卡 支出 餐饮
(3, 999, '海底捞', 'F001', 1, 368.00, '2026-04-03 19:00:00', '火锅晚餐', '聚餐', 1),

-- 12. 邮政 支出 地铁
(1, 999, '地铁出行', 'T002', 1, 5.00, '2026-04-04 08:50:00', '地铁费', '上班通勤', 1),

-- 13. 微信 支出 健身
(2, 999, '健身房', 'G002', 1, 299.00, '2026-04-04 18:30:00', '月卡续费', '健身', 1),

-- 14. 邮政 收入 退款
(998, 1, '京东商城', 'S006', 2, 129.00, '2026-04-05 11:20:00', '商品退货退款', '退款', 1),

-- 15. 微信 → 邮政 互转
(2, 1, '零钱提现', 'H000', 3, 500.00, '2026-04-05 15:10:00', '微信提现到银行卡', '提现', 1);
USE springdatabase;

INSERT INTO bill_record
(out_account_id, in_account_id, counterparty_name, cate_id, pay_type, amount, bill_time, commodity, remark, user_id)
VALUES
-- 1. 家人转账 → 邮政（收入）
(998, 1, '家人转账', 'S001', 2, 2000.66, '2026-01-01 00:00:00', '生活费', '生活费', 1),

-- 2. 邮政 → 药科大二食堂（三餐）
(1, 999, '药科大二食堂', 'F001', 1, 13.00, '2026-01-01 00:00:00', '烤鸭饭', '', 1),

-- 3. 邮政 → 药科大二食堂
(1, 999, '药科大二食堂', 'F001', 1, 14.90, '2026-01-01 00:00:00', '螺丝辣椒炒肉', '', 1),

-- 4. 邮政 → 药科大二食堂
(1, 999, '药科大二食堂', 'F001', 1, 13.80, '2026-01-02 00:00:00', '宫爆鸡丁拌面', '', 1),

-- 5. 邮政 → 南京地铁
(1, 999, '南京地铁', 'T002', 1, 4.00, '2026-01-02 00:00:00', '南京地铁', '', 1),

-- 6. 邮政 → 南京地铁
(1, 999, '南京地铁', 'T002', 1, 4.00, '2026-01-02 00:00:00', '南京地铁', '', 1),

-- 7. 邮政 → 美团（茶百道）
(1, 999, '美团', 'F005', 1, 8.99, '2026-01-02 00:00:00', '茶百道青提茉莉', '', 1),

-- 8. 邮政 → 美团（星巴克蛋糕）
(1, 999, '美团', 'F004', 1, 29.89, '2026-01-02 00:00:00', '星巴克蛋糕', '', 1),

-- 9. 邮政 → 微信支付（魏斯理汉堡）
(1, 999, '微信支付', 'F001', 1, 92.00, '2026-01-02 00:00:00', '魏斯理汉堡', '', 1),

-- 10. 邮政 → 南京地铁
(1, 999, '南京地铁', 'T002', 1, 6.00, '2026-01-02 00:00:00', '南京地铁', '', 1),

-- 11. 邮政 → 南京地铁
(1, 999, '南京地铁', 'T002', 1, 3.00, '2026-01-02 00:00:00', '南京地铁', '', 1),

-- 12. 邮政 → 个体户（矿泉水）
(1, 999, '个体户', 'F005', 1, 3.00, '2026-01-02 00:00:00', '矿泉水', '', 1),

-- 13. 邮政 → 南京地铁
(1, 999, '南京地铁', 'T002', 1, 6.00, '2026-01-02 00:00:00', '南京地铁', '', 1),

-- 14. 张家鑫 → 邮政（AA收入）
(998, 1, '张家鑫', 'S000', 2, 50.00, '2026-01-02 00:00:00', 'AA聚餐收入', '', 1),

-- 15. 邮政 → 药科大二食堂（甜品）
(1, 999, '药科大二食堂', 'F004', 1, 11.80, '2026-01-03 00:00:00', '开心果马里奥', '', 1),

-- 16. 邮政 → E组团水果店
(1, 999, 'E组团水果店', 'F002', 1, 13.80, '2026-01-03 00:00:00', '芭乐*2', '', 1);