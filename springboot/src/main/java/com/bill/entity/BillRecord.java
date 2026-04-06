package com.bill.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillRecord {
    private Long id;
    private Integer out_account_id;
    private Integer in_account_id;
    private String counterparty_name;
    private String cate_id;
    private Integer pay_type;
    private BigDecimal amount;
    private String currency;
    private Date bill_time;
    private String commodity;
    private String remark;
    private Integer user_id;
    private Date create_time;
    private Date update_time;
}
