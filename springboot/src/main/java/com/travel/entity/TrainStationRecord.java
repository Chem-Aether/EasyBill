package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("train_station")
public class TrainStationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long trainId;           // 关联车次ID
    private Long userId;            // 用户ID
    private String stationName;     // 车站名称
    private Integer stationOrder;   // 车站顺序
}
