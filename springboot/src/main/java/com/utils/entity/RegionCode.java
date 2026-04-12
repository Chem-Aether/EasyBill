package com.utils.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_area")
public class RegionCode {
    @TableId
    private String code;        // 行政区划编码
    private String name;        // 名称
    private Integer level;      // 1省 2市 3区
    private String type;        // 类型
    private String parentCode;  // 上级编码
}
