package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flight")
public class FlightRecord {
    @TableId(value = "flight_id", type = IdType.AUTO)
    private Long flightId;

    private Long userId;

    private String flightNo;

    private String aircraftReg;

    private String aircraftType;

    private String departureAirport;

    private String departureTerminal;

    private String departureIcao;

    private LocalDateTime takeoffTime;

    private String boardingMethod;

    private String arrivalAirport;

    private String arrivalTerminal;

    private String arrivalIcao;

    private LocalDateTime landingTime;

    private String stopoverAirport;

    private Integer flightDistanceKm;

    private String seatNo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
