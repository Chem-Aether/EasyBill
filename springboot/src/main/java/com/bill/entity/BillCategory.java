package com.bill.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BillCategory {
    private String cate_id;
    private String class_name;
    private String parent_cate;
    private Integer level;
    private String icon;
    private Integer type;
    private Integer is_deleted;
    private Date create_time;
    private Date update_time;
}
