package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.FlightRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
@Tag(name = "交通工具票务管理接口", description = "航班/火车/票务相关接口")
public class FlightRecordController {

    @Autowired
    private FlightRecordService flightRecordService;

}
