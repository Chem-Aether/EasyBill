package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.FlightRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
public class FlightRecordController {

    @Autowired
    private FlightRecordService flightRecordService;

    @GetMapping("/getflight")
    public ResponseEntity<Result> findAll() {
        return ResponseEntity.ok(
                Result.success(flightRecordService.findAll())
        );
    }
}
