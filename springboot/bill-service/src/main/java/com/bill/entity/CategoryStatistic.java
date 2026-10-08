package com.bill.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategoryStatistic {
    private String cate_id;
    private String category_name;
    private Integer category_type;
    private BigDecimal total_amount;
}
