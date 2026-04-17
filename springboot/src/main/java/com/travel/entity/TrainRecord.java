package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Data
@TableName("train")
public class TrainRecord {

    @TableId(type = IdType.AUTO)
    private Long trainId;
    private Long userId;
    private String trainNo;
    private String trainType;
    private String trainModel;
    private String startStation;
    private String endStation;
    private String originStation;
    private String terminalStation;
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private String seatNo;
    private String seatClass;
    private Integer mileageKm;

    @TableField(exist = false) // 标注：数据库不存在
    private List<TrainStationRecord> stationList;
}


