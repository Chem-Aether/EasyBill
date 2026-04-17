package com.travel.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainRecordQueryDTO {
    private Integer pageNum;
    private Integer pageSize;

    private String trainNo;
    private String startStation;
    private String endStation;
    private LocalDateTime departureDatetimeStart;
    private LocalDateTime departureDatetimeEnd;
}