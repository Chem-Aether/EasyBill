package com.utils.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_stations")
public class TrainStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;     // 车站名称
    private String code;     // 车站代码
    private String city;     // 城市
    private String region;   // 地区
    private String province; // 省份
    private Double latitude; // 纬度
    private Double longitude;// 经度
}