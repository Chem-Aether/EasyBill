package com.utils.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("airport")
public class Airport {
    @TableId(type = IdType.INPUT) // 主键由外部（CSV数据）直接赋值
    private String icao;          // ICAO机场码（主键）

    private String iata;          // IATA机场码
    private String name;          // 机场名称
    private String attr;          // 机场属性/标签
    private BigDecimal longitude; // 经度
    private BigDecimal latitude;  // 纬度
    private String level;         // 机场等级 4C/4D/4E/4F
    private String city;          // 所在城市
    private String type;          // 机场类型
}