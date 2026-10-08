package com.bill.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Account {
    private Integer id;
    private String accountName;
    private String accountIcon;
    private Integer accountType;
    private BigDecimal balance;
    private Integer isDefault;
    private Integer status;
    private Integer userId;
}
