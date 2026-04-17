package com.travel.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightRecordQueryDTO {

    // 分页
    private Integer pageNum;
    private Integer pageSize;

    // 查询条件
    private String flightNo;
    private String aircraftReg;
    private String aircraftType;
    private String departureAirport;
    private String arrivalAirport;
    private String stopoverAirport;
    private LocalDateTime takeoffTimeStart;
    private LocalDateTime takeoffTimeEnd;
}