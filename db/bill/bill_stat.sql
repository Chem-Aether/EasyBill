USE springdatabase;

-- 今日收支统计
SELECT
    SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) today_expense,
    SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) today_income,
    SUM(CASE WHEN pay_type = 1 THEN -amount WHEN pay_type = 2 THEN amount ELSE 0 END) today_balance
FROM bill_record
WHERE user_id = 1 AND DATE(bill_time) = CURDATE();

-- 本月收支统计
SELECT
    SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) month_expense,
    SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) month_income,
    SUM(CASE WHEN pay_type = 1 THEN -amount WHEN pay_type = 2 THEN amount ELSE 0 END) month_balance
FROM bill_record
WHERE user_id = 1 AND DATE_FORMAT(bill_time, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m');

-- 支出分类统计
SELECT
    c.cate_id,
    c.class_name,
    c.icon,
    SUM(b.amount) total_amount,
    COUNT(*) bill_count
FROM bill_record b
         LEFT JOIN bill_category c ON b.cate_id = c.cate_id
WHERE b.user_id = 1 AND b.pay_type = 1
GROUP BY c.cate_id, c.class_name, c.icon
ORDER BY total_amount DESC;

-- 收入分类统计
SELECT
    c.cate_id,
    c.class_name,
    c.icon,
    SUM(b.amount) total_amount,
    COUNT(*) bill_count
FROM bill_record b
         LEFT JOIN bill_category c ON b.cate_id = c.cate_id
WHERE b.user_id = 1 AND b.pay_type = 2
GROUP BY c.cate_id, c.class_name, c.icon
ORDER BY total_amount DESC;

-- 消费最多商家排行
SELECT
    counterparty_name,
    SUM(amount) total_amount,
    COUNT(*) bill_count,
    MIN(bill_time) first_buy_time,
    MAX(bill_time) last_buy_time
FROM bill_record
WHERE user_id = 1 AND pay_type = 1 AND counterparty_name != ''
GROUP BY counterparty_name
ORDER BY total_amount DESC
LIMIT 10;


-- 各账户收支 + 余额
SELECT
    a.id,
    a.account_name,
    a.account_type,
    a.balance,
    -- 支出
    (SELECT IFNULL(SUM(amount),0) FROM bill_record WHERE user_id=1 AND out_account_id=a.id AND pay_type=1) account_expense,
    -- 收入
    (SELECT IFNULL(SUM(amount),0) FROM bill_record WHERE user_id=1 AND in_account_id=a.id AND pay_type=2) account_income
FROM account a
WHERE a.user_id IN (0,1)
ORDER BY a.id;

-- 近 6 个月收支趋势
SELECT
    DATE_FORMAT(bill_time, '%Y-%m') month,
    SUM(CASE WHEN pay_type=1 THEN amount ELSE 0 END) expense,
    SUM(CASE WHEN pay_type=2 THEN amount ELSE 0 END) income,
    SUM(CASE WHEN pay_type=1 THEN -amount WHEN pay_type=2 THEN amount ELSE 0 END) month_balance
FROM bill_record
WHERE user_id=1
GROUP BY DATE_FORMAT(bill_time, '%Y-%m')
ORDER BY month DESC
LIMIT 6;

-- 按日期汇总每日收支
SELECT
    DATE(bill_time) day,
    SUM(CASE WHEN pay_type=1 THEN amount ELSE 0 END) expense,
    SUM(CASE WHEN pay_type=2 THEN amount ELSE 0 END) income,
    COUNT(*) bill_count
FROM bill_record
WHERE user_id=1
GROUP BY DATE(bill_time)
ORDER BY day DESC;

-- 支出 / 收入 / 互转总数
SELECT
    CASE pay_type
        WHEN 1 THEN '支出'
        WHEN 2 THEN '收入'
        WHEN 3 THEN '互转'
        END trans_type,
    SUM(amount) total_amount,
    COUNT(*) trans_count
FROM bill_record
WHERE user_id=1
GROUP BY pay_type;