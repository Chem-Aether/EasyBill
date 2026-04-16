package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("train_station")
public class TrainStationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    // 关联车次ID
    private Long trainId;

    // 用户ID
    private Long userId;

    // 车站名称
    private String stationName;

    // 车站顺序
    private Integer stationOrder;
}
