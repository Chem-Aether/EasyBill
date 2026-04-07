package com.bill.entity;

import lombok.Data;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
public class BillCategory {
    @TableId
    private String cateId;
    private String className;
    private String parentCate;
    private Integer level;
    private String icon;
    private Integer type;
    private Integer isDeleted;
}
