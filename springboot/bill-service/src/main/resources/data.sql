INSERT IGNORE INTO account (id, account_name, account_type, user_id) VALUES
  (998, '外部收入', 9, 0),
  (999, '商家消费', 9, 0);

INSERT IGNORE INTO bill_category (cate_id, class_name, parent_cate, level, type) VALUES
  ('S000', '收入', 'S0', 1, 2), ('S001', '工资', 'S0', 2, 2),
  ('F000', '餐饮', 'F0', 1, 1), ('F001', '三餐', 'F0', 2, 1),
  ('T000', '交通', 'T0', 1, 1), ('T001', '公共交通', 'T0', 2, 1),
  ('L000', '生活', 'L0', 1, 1), ('B000', '购物', 'B0', 1, 1),
  ('Y000', '娱乐', 'Y0', 1, 1), ('H000', '互转', 'H0', 1, 3),
  ('X000', '其他', 'X0', 1, 1);
