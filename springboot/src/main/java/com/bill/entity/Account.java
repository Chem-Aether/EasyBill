package com.bill.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Account {
    private Integer id;
    private String account_name;
    private String account_icon;
    private Integer account_type;
    private BigDecimal balance;
    private Integer is_default;
    private Integer status;
    private Integer user_id;
    private Date create_time;
    private Date update_time;
}
