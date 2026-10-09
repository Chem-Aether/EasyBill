package com.bill.service;

import com.bill.dto.AccountRequest;
import com.bill.dto.BillTransactionRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BillService {
    private static final String ACCOUNT_SELECT = """
            SELECT a.id, a.name, a.code, a.opening_balance AS "openingBalance",
                   a.opening_balance
                     + COALESCE((SELECT SUM(t.amount) FROM bill_transaction t WHERE t.to_account_id = a.id), 0)
                     - COALESCE((SELECT SUM(t.amount) FROM bill_transaction t WHERE t.from_account_id = a.id), 0)
                     AS balance
            FROM bill_account a ORDER BY a.id
            """;
    private static final String RECORD_SELECT = """
            SELECT t.id, TO_CHAR(t.occurred_at, 'YYYY-MM-DD"T"HH24:MI:SS') AS "occurredAt", t.amount,
                   t.from_account_id AS "fromAccountId", t.to_account_id AS "toAccountId",
                   fa.name AS "fromAccountName", fa.code AS "fromAccountCode",
                   ta.name AS "toAccountName", ta.code AS "toAccountCode",
                   t.counterparty, t.description, c.id AS "categoryId", c.name AS category, c.icon AS "categoryIcon",
                   p.name AS "categoryParent", t.remark,
                   CASE WHEN t.from_account_id IS NULL THEN 'income'
                        WHEN t.to_account_id IS NULL THEN 'expense' ELSE 'transfer' END AS direction
            FROM bill_transaction t
            LEFT JOIN bill_account fa ON fa.id = t.from_account_id
            LEFT JOIN bill_account ta ON ta.id = t.to_account_id
            LEFT JOIN bill_category c ON c.id = t.category_id
            LEFT JOIN bill_category p ON p.id = c.parent_id
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public BillService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> accounts() {
        return jdbc.getJdbcTemplate().queryForList(ACCOUNT_SELECT);
    }

    public List<Map<String, Object>> categories() {
        return jdbc.getJdbcTemplate().queryForList("""
                SELECT c.id, c.name, c.icon, c.parent_id AS "parentId", p.name AS "parentName", c.sort_order AS "sortOrder"
                FROM bill_category c LEFT JOIN bill_category p ON p.id=c.parent_id
                ORDER BY COALESCE(p.sort_order,c.sort_order), p.id NULLS FIRST, c.sort_order, c.id
                """);
    }

    public long createCategory(String name, Long parentId, Integer sortOrder, String icon) {
        validateCategory(name);
        if (parentId != null) requireCategoryParent(parentId);
        int nextSortOrder = sortOrder == null ? nextCategorySortOrder(parentId) : sortOrder;
        try {
            return jdbc.queryForObject("INSERT INTO bill_category(name,parent_id,sort_order,icon) VALUES (:name,:parentId,:sortOrder,:icon) RETURNING id",
                    new MapSqlParameterSource().addValue("name", name.trim()).addValue("parentId", parentId)
                            .addValue("sortOrder", nextSortOrder).addValue("icon", categoryIcon(icon)), Long.class);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new ResponseStatusException(BAD_REQUEST, "同级分类名称已存在");
        }
    }

    public void updateCategory(long id, String name, String icon) {
        validateCategory(name);
        try {
            int changed = jdbc.update("UPDATE bill_category SET name=:name, icon=:icon WHERE id=:id",
                    new MapSqlParameterSource().addValue("id", id).addValue("name", name.trim()).addValue("icon", categoryIcon(icon)));
            if (changed == 0) throw new ResponseStatusException(NOT_FOUND, "分类不存在");
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new ResponseStatusException(BAD_REQUEST, "同级分类名称已存在");
        }
    }

    private static String categoryIcon(String icon) {
        if (icon == null || icon.isBlank()) return "🏷️";
        if (icon.length() > 32000)
            throw new ResponseStatusException(BAD_REQUEST, "分类图标无效");
        return icon;
    }

    private int nextCategorySortOrder(Long parentId) {
        Integer max = jdbc.queryForObject("SELECT COALESCE(MAX(sort_order),0) FROM bill_category WHERE parent_id IS NOT DISTINCT FROM :parentId",
                new MapSqlParameterSource("parentId", parentId), Integer.class);
        return (max == null ? 0 : max) + 1;
    }

    public void deleteCategory(long id) {
        try {
            int changed = jdbc.update("DELETE FROM bill_category WHERE id=:id", Map.of("id", id));
            if (changed == 0) throw new ResponseStatusException(NOT_FOUND, "分类不存在");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(CONFLICT, "该分类仍被账单或子分类使用，不能删除");
        }
    }

    private void requireCategoryParent(long id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM bill_category WHERE id=:id AND parent_id IS NULL", Map.of("id", id), Integer.class);
        if (count == null || count == 0) throw new ResponseStatusException(BAD_REQUEST, "二级分类必须归属一个一级分类");
    }

    private static void validateCategory(String name) {
        if (name == null || name.isBlank())
            throw new ResponseStatusException(BAD_REQUEST, "分类名称不能为空");
        if (name.trim().length() > 100)
            throw new ResponseStatusException(BAD_REQUEST, "分类名称不能超过100个字符");
    }

    public Map<String, Object> accountRecords(long accountId, int page, int pageSize, String sortBy, String sortOrder) {
        Integer exists = jdbc.queryForObject("SELECT COUNT(*) FROM bill_account WHERE id=:id", Map.of("id", accountId), Integer.class);
        if (exists == null || exists == 0) throw new ResponseStatusException(NOT_FOUND, "账户不存在");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("accountId", accountId)
                .addValue("limit", pageSize)
                .addValue("offset", (long) (page - 1) * pageSize);
        String sortColumn = switch (sortBy) {
            case "amount" -> "t.amount";
            case "description" -> "t.description";
            case "category" -> "c.name";
            case "direction" -> "CASE WHEN t.from_account_id IS NULL THEN 'income' WHEN t.to_account_id IS NULL THEN 'expense' ELSE 'transfer' END";
            case "payer" -> "COALESCE(fa.name, t.counterparty, '')";
            case "payee" -> "COALESCE(ta.name, t.counterparty, '')";
            default -> "t.occurred_at";
        };
        String order = "asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";
        List<Map<String, Object>> rows = jdbc.queryForList(RECORD_SELECT + " WHERE (t.from_account_id=:accountId OR t.to_account_id=:accountId) ORDER BY " + sortColumn + " " + order + ", t.id DESC LIMIT :limit OFFSET :offset", params);
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM bill_transaction WHERE from_account_id=:accountId OR to_account_id=:accountId", Map.of("accountId", accountId), Long.class);
        return Map.of("records", rows, "total", total == null ? 0L : total, "page", page, "pageSize", pageSize);
    }

    public long createAccount(AccountRequest request) {
        validateAccount(request);
        return jdbc.queryForObject("INSERT INTO bill_account(name, code, opening_balance) VALUES (:name, :code, :balance) RETURNING id",
                new MapSqlParameterSource().addValue("name", request.name().trim())
                        .addValue("code", blankToNull(request.code()))
                        .addValue("balance", request.openingBalance() == null ? BigDecimal.ZERO : request.openingBalance()), Long.class);
    }

    public void updateAccount(long id, AccountRequest request) {
        validateAccount(request);
        int changed = jdbc.update("UPDATE bill_account SET name=:name, code=:code, opening_balance=:balance, updated_at=now() WHERE id=:id",
                accountParams(id, request));
        if (changed == 0) throw new ResponseStatusException(NOT_FOUND, "账户不存在");
    }

    public void deleteAccount(long id) {
        int changed = jdbc.update("DELETE FROM bill_account WHERE id=:id", Map.of("id", id));
        if (changed == 0) throw new ResponseStatusException(NOT_FOUND, "账户不存在");
    }

    public List<Map<String, Object>> records(String direction, Long accountId, Long categoryId,
                                              String keyword, String startTime, String endTime) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder(RECORD_SELECT).append(" WHERE 1=1");
        if (direction != null && !direction.isBlank()) {
            requireDirection(direction);
            sql.append(" AND CASE WHEN t.from_account_id IS NULL THEN 'income' WHEN t.to_account_id IS NULL THEN 'expense' ELSE 'transfer' END=:direction");
            params.addValue("direction", direction);
        }
        if (accountId != null) {
            sql.append(" AND (t.from_account_id=:accountId OR t.to_account_id=:accountId)");
            params.addValue("accountId", accountId);
        }
        if (categoryId != null) {
            sql.append(" AND t.category_id=:categoryId");
            params.addValue("categoryId", categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (t.counterparty ILIKE :keyword OR t.description ILIKE :keyword OR t.remark ILIKE :keyword)");
            params.addValue("keyword", "%" + keyword.trim() + "%");
        }
        addDateFilter(sql, params, startTime, endTime);
        sql.append(" ORDER BY t.occurred_at DESC, t.id DESC");
        return jdbc.queryForList(sql.toString(), params);
    }

    @Transactional
    public int importRecords(List<BillTransactionRequest> requests) {
        if (requests == null || requests.isEmpty())
            throw new ResponseStatusException(BAD_REQUEST, "导入文件没有账单记录");
        if (requests.size() > 10000)
            throw new ResponseStatusException(BAD_REQUEST, "单次最多导入10000条记录");
        for (int i = 0; i < requests.size(); i++) {
            try {
                BillTransactionRequest request = requests.get(i);
                validateRecord(request);
                checkAccounts(request);
                checkCategory(request.categoryId());
            } catch (ResponseStatusException e) {
                throw new ResponseStatusException(BAD_REQUEST, "第" + (i + 2) + "行：" + e.getReason());
            }
        }
        for (BillTransactionRequest request : requests) {
            jdbc.update("""
                    INSERT INTO bill_transaction(occurred_at, amount, from_account_id, to_account_id, counterparty, description, category_id, remark)
                    VALUES (:occurredAt, :amount, :fromAccountId, :toAccountId, :counterparty, :description, :categoryId, :remark)
                    """, recordParams(request));
        }
        return requests.size();
    }

    public byte[] exportRecords(String direction, Long accountId, Long categoryId, String keyword,
                                String startTime, String endTime) {
        List<Map<String, Object>> rows = records(direction, accountId, categoryId, keyword, startTime, endTime);
        StringBuilder csv = new StringBuilder("\uFEFF交易时间,方向,金额,付款账户ID,付款账户,收款账户ID,收款账户,交易对象,分类ID,分类,摘要,备注\r\n");
        for (Map<String, Object> row : rows) {
            Object fromId = row.get("fromAccountId");
            Object toId = row.get("toAccountId");
            Object categoryIdValue = row.get("categoryId");
            csv.append(csvValue(row.get("occurredAt"))).append(',')
                    .append(csvValue(row.get("direction"))).append(',')
                    .append(csvValue(row.get("amount"))).append(',')
                    .append(csvValue(fromId)).append(',')
                    .append(csvValue(row.get("fromAccountName"))).append(',')
                    .append(csvValue(toId)).append(',')
                    .append(csvValue(row.get("toAccountName"))).append(',')
                    .append(csvValue(row.get("counterparty"))).append(',')
                    .append(csvValue(categoryIdValue)).append(',')
                    .append(csvValue(row.get("category"))).append(',')
                    .append(csvValue(row.get("description"))).append(',')
                    .append(csvValue(row.get("remark"))).append("\r\n");
        }
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static String csvValue(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    @Transactional
    public long createRecord(BillTransactionRequest request) {
        validateRecord(request);
        checkAccounts(request);
        checkCategory(request.categoryId());
        return jdbc.queryForObject("""
                INSERT INTO bill_transaction(occurred_at, amount, from_account_id, to_account_id, counterparty, description, category_id, remark)
                VALUES (:occurredAt, :amount, :fromAccountId, :toAccountId, :counterparty, :description, :categoryId, :remark)
                RETURNING id
                """, recordParams(request), Long.class);
    }

    @Transactional
    public void updateRecord(long id, BillTransactionRequest request) {
        validateRecord(request);
        checkAccounts(request);
        checkCategory(request.categoryId());
        MapSqlParameterSource params = recordParams(request).addValue("id", id);
        int changed = jdbc.update("""
                UPDATE bill_transaction SET occurred_at=:occurredAt, amount=:amount,
                    from_account_id=:fromAccountId, to_account_id=:toAccountId,
                    counterparty=:counterparty, description=:description, category_id=:categoryId,
                    remark=:remark, updated_at=now() WHERE id=:id
                """, params);
        if (changed == 0) throw new ResponseStatusException(NOT_FOUND, "账单记录不存在");
    }

    public void deleteRecord(long id) {
        int changed = jdbc.update("DELETE FROM bill_transaction WHERE id=:id", Map.of("id", id));
        if (changed == 0) throw new ResponseStatusException(NOT_FOUND, "账单记录不存在");
    }

    public Map<String, Object> summary(String startTime, String endTime) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("SELECT ")
                .append("COALESCE(SUM(amount) FILTER (WHERE from_account_id IS NULL),0) AS income, ")
                .append("COALESCE(SUM(amount) FILTER (WHERE to_account_id IS NULL),0) AS expense, ")
                .append("COALESCE(SUM(amount) FILTER (WHERE from_account_id IS NOT NULL AND to_account_id IS NOT NULL),0) AS transfer ")
                .append("FROM bill_transaction WHERE 1=1");
        addDateFilter(sql, params, startTime, endTime);
        return jdbc.queryForMap(sql.toString(), params);
    }

    public Map<String, Object> timeline(int year) {
        Timestamp start = Timestamp.valueOf(LocalDate.of(year, 1, 1).atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDate.of(year + 1, 1, 1).atStartOfDay());
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("startTime", start).addValue("endTime", end);
        String measures = "COALESCE(SUM(amount) FILTER (WHERE from_account_id IS NULL),0) AS income, "
                + "COALESCE(SUM(amount) FILTER (WHERE to_account_id IS NULL),0) AS expense ";
        List<Map<String, Object>> daily = jdbc.queryForList("SELECT TO_CHAR(DATE_TRUNC('day', occurred_at), 'YYYY-MM-DD') AS period, "
                + measures + "FROM bill_transaction WHERE occurred_at >= :startTime AND occurred_at < :endTime "
                + "GROUP BY DATE_TRUNC('day', occurred_at) ORDER BY DATE_TRUNC('day', occurred_at)", params);
        List<Map<String, Object>> monthly = jdbc.queryForList("SELECT TO_CHAR(DATE_TRUNC('month', occurred_at), 'YYYY-MM') AS period, "
                + measures + "FROM bill_transaction WHERE occurred_at >= :startTime AND occurred_at < :endTime "
                + "GROUP BY DATE_TRUNC('month', occurred_at) ORDER BY DATE_TRUNC('month', occurred_at)", params);
        return Map.of("daily", daily, "monthly", monthly);
    }

    public List<Map<String, Object>> categoryStatistics(String direction, String startTime, String endTime, boolean detailed) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String categoryExpression = detailed ? "c.name" : "COALESCE(p.name,c.name)";
        StringBuilder sql = new StringBuilder("SELECT ").append(categoryExpression)
                .append(" AS category, SUM(t.amount) AS total_amount FROM bill_transaction t LEFT JOIN bill_category c ON c.id=t.category_id LEFT JOIN bill_category p ON p.id=c.parent_id WHERE t.category_id IS NOT NULL");
        if (direction != null && !direction.isBlank()) {
            requireDirection(direction);
            sql.append(" AND CASE WHEN from_account_id IS NULL THEN 'income' WHEN to_account_id IS NULL THEN 'expense' ELSE 'transfer' END=:direction");
            params.addValue("direction", direction);
        }
        addDateFilter(sql, params, startTime, endTime);
        if (detailed) sql.append(" GROUP BY c.id, c.name ORDER BY total_amount DESC");
        else sql.append(" GROUP BY 1 ORDER BY total_amount DESC");
        return jdbc.queryForList(sql.toString(), params);
    }

    private void checkAccounts(BillTransactionRequest request) {
        for (Long id : new Long[]{request.fromAccountId(), request.toAccountId()}) {
            if (id == null) continue;
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM bill_account WHERE id=:id", Map.of("id", id), Integer.class);
            if (count == null || count == 0) throw new ResponseStatusException(BAD_REQUEST, "所选账户不存在");
        }
    }

    private void checkCategory(Long categoryId) {
        if (categoryId == null) return;
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM bill_category WHERE id=:id", Map.of("id", categoryId), Integer.class);
        if (count == null || count == 0) throw new ResponseStatusException(BAD_REQUEST, "所选分类不存在");
    }

    private static void validateAccount(AccountRequest request) {
        if (request == null || request.name() == null || request.name().isBlank())
            throw new ResponseStatusException(BAD_REQUEST, "账户名称不能为空");
        if (request.openingBalance() != null && request.openingBalance().scale() > 2)
            throw new ResponseStatusException(BAD_REQUEST, "余额最多保留两位小数");
    }

    private static void validateRecord(BillTransactionRequest request) {
        if (request == null || request.occurredAt() == null)
            throw new ResponseStatusException(BAD_REQUEST, "交易时间不能为空");
        if (request.amount() == null || request.amount().signum() <= 0 || request.amount().scale() > 2)
            throw new ResponseStatusException(BAD_REQUEST, "金额必须大于零且最多保留两位小数");
        if (request.fromAccountId() == null && request.toAccountId() == null)
            throw new ResponseStatusException(BAD_REQUEST, "出账账户和入账账户至少填写一个");
        if (request.fromAccountId() != null && request.fromAccountId().equals(request.toAccountId()))
            throw new ResponseStatusException(BAD_REQUEST, "出账与入账账户不能相同");
    }

    private static MapSqlParameterSource recordParams(BillTransactionRequest r) {
        return new MapSqlParameterSource()
                .addValue("occurredAt", Timestamp.valueOf(r.occurredAt()))
                .addValue("amount", r.amount())
                .addValue("fromAccountId", r.fromAccountId())
                .addValue("toAccountId", r.toAccountId())
                .addValue("counterparty", value(r.counterparty()))
                .addValue("description", value(r.description()))
                .addValue("categoryId", r.categoryId())
                .addValue("remark", value(r.remark()));
    }

    private static MapSqlParameterSource accountParams(long id, AccountRequest request) {
        return new MapSqlParameterSource().addValue("id", id)
                .addValue("name", request.name().trim())
                .addValue("code", blankToNull(request.code()))
                .addValue("balance", request.openingBalance() == null ? BigDecimal.ZERO : request.openingBalance());
    }

    private static void addDateFilter(StringBuilder sql, MapSqlParameterSource params, String start, String end) {
        if (start != null && !start.isBlank()) {
            sql.append(" AND occurred_at >= :startTime");
            params.addValue("startTime", Timestamp.valueOf(LocalDateTime.parse(start)));
        }
        if (end != null && !end.isBlank()) {
            sql.append(" AND occurred_at <= :endTime");
            params.addValue("endTime", Timestamp.valueOf(LocalDateTime.parse(end)));
        }
    }

    private static void requireDirection(String direction) {
        if (!List.of("income", "expense", "transfer").contains(direction))
            throw new ResponseStatusException(BAD_REQUEST, "账单方向无效");
    }

    private static String value(String value) { return value == null ? "" : value.trim(); }
    private static String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
