package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flight")
public class FlightRecord {
    @TableId(value = "flight_id", type = IdType.AUTO)
    private Long flightId;
    private Long userId;                //  用户ID
    private String flightNo;            //  航班号
    private String company;             //  航空公司
    private String aircraftReg;         //  飞机注册号
    private String aircraftType;        //  机型
    private String departureAirport;    //  起飞机场
    private String departureTerminal;   //  起飞机场航站楼
    private String departureIcao;       //  起飞机场ICAO
    private LocalDateTime takeoffTime;  //  起飞时间
    private String boardingMethod;      //  登机方式
    private String arrivalAirport;      //  到达机场
    private String arrivalTerminal;     //  到达机场航站楼
    private String arrivalIcao;         //  到达机场ICAO
    private LocalDateTime landingTime;  //  达到时间
    private String deplaningMethod;     //  下机方式
    private String stopoverAirport;     //  中转机场
    private Integer flightDistanceKm;   //  总航程
    private String seatNo;              //  座位号
}
