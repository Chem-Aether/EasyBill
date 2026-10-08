CREATE TABLE IF NOT EXISTS bill_category (
  cate_id CHAR(4) PRIMARY KEY,
  class_name VARCHAR(20) NOT NULL,
  parent_cate CHAR(2) NOT NULL,
  level TINYINT NOT NULL DEFAULT 2,
  icon VARCHAR(100) NOT NULL DEFAULT '',
  type TINYINT NOT NULL,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_category_parent_name (parent_cate, class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS account (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  account_name VARCHAR(30) NOT NULL,
  account_icon VARCHAR(50) NOT NULL DEFAULT '',
  account_type TINYINT NOT NULL,
  balance DECIMAL(12,2) NOT NULL DEFAULT 0,
  is_default TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  user_id BIGINT UNSIGNED NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_account_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bill_record (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  out_account_id INT UNSIGNED NOT NULL,
  in_account_id INT UNSIGNED NOT NULL,
  counterparty_name VARCHAR(100) NOT NULL DEFAULT '',
  cate_id CHAR(4),
  pay_type TINYINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  currency VARCHAR(20) NOT NULL DEFAULT 'CNY',
  bill_time DATETIME NOT NULL,
  commodity VARCHAR(500) NOT NULL DEFAULT '',
  remark VARCHAR(500) NOT NULL DEFAULT '',
  user_id BIGINT UNSIGNED NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_bill_out_account FOREIGN KEY (out_account_id) REFERENCES account(id),
  CONSTRAINT fk_bill_in_account FOREIGN KEY (in_account_id) REFERENCES account(id),
  CONSTRAINT fk_bill_category FOREIGN KEY (cate_id) REFERENCES bill_category(cate_id),
  INDEX idx_bill_user_time (user_id, bill_time),
  INDEX idx_bill_category (cate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE OR REPLACE VIEW v_bill_detail AS
SELECT b.*, oa.account_name AS out_account_name, ia.account_name AS in_account_name,
       c.class_name AS category_name, c.type AS category_type
FROM bill_record b
LEFT JOIN account oa ON b.out_account_id = oa.id
LEFT JOIN account ia ON b.in_account_id = ia.id
LEFT JOIN bill_category c ON b.cate_id = c.cate_id;
