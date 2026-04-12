package com.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("foot_spot")
public class FootSpot {

    @TableId(type = IdType.AUTO)
    private Long spotId;           // 足迹ID
    private Long userId;          // 用户ID
    private String adcode;        // 行政区划代码
    private String spotName;      // 景点名称
    private String spotType;      // 类型
    private LocalDate visitTime;  // 到访日期
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}