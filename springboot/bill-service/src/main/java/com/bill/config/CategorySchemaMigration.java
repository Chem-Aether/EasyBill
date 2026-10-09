package com.bill.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CategorySchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public CategorySchemaMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!hasColumn("category")) return;

        if (hasColumn("category_parent")) {
            jdbc.execute("""
                    UPDATE bill_transaction t
                    SET category_id=(
                        SELECT c.id
                        FROM bill_category c
                        LEFT JOIN bill_category p ON p.id=c.parent_id
                        WHERE c.name=t.category
                          AND ((NULLIF(t.category_parent,'') IS NOT NULL AND p.name=t.category_parent)
                            OR (NULLIF(t.category_parent,'') IS NULL AND c.parent_id IS NULL)
                            OR (NULLIF(t.category_parent,'') IS NULL AND c.parent_id IS NOT NULL
                                AND NOT EXISTS (SELECT 1 FROM bill_category r WHERE r.name=t.category AND r.parent_id IS NULL)))
                        ORDER BY CASE WHEN NULLIF(t.category_parent,'') IS NOT NULL AND p.name=t.category_parent THEN 0 ELSE 1 END,
                                 c.parent_id NULLS FIRST,c.id
                        LIMIT 1
                    )
                    WHERE t.category_id IS NULL AND t.category IS NOT NULL AND t.category<>''
                    """);
        } else {
            jdbc.execute("""
                    UPDATE bill_transaction t
                    SET category_id=(SELECT c.id FROM bill_category c WHERE c.name=t.category ORDER BY c.parent_id NULLS FIRST,c.id LIMIT 1)
                    WHERE t.category_id IS NULL AND t.category IS NOT NULL AND t.category<>''
                    """);
        }

        jdbc.execute("ALTER TABLE bill_transaction DROP COLUMN IF EXISTS category_parent");
        jdbc.execute("ALTER TABLE bill_transaction DROP COLUMN category");
    }

    private boolean hasColumn(String column) {
        Integer count = jdbc.queryForObject("""
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema=current_schema() AND table_name='bill_transaction' AND column_name=?
                """, Integer.class, column);
        return count != null && count > 0;
    }
}
