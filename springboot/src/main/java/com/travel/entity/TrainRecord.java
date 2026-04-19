package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@TableName("train")
public class TrainRecord {

    @TableId(type = IdType.AUTO)
    private Long trainId;                       // ID号
    private Long userId;                        // 用户ID
    private String trainNo;                     // 列车号
    private String trainType;                   // 列车类型
    private String trainModel;                  // 车型
    private String startStation;                // 出发站
    private String endStation;                  // 达到站
    private String originStation;               // 始发站
    private String terminalStation;             // 终点站
    private LocalDateTime departureDatetime;    // 出发时间
    private LocalDateTime arrivalDatetime;      // 达到时间
    private String seatNo;                      // 座位号
    private String seatClass;                   // 席别
    private Integer mileageKm;                  // 总里程

    @TableField(exist = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer stationCount;               // 途径站数目
}


