package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.TicketStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
@Tag(name = "统计接口", description = "航班/火车/票务相关接口")
public class TicketStatisticsController {

    @Autowired
    private TicketStatisticsService ticketService;

    @GetMapping("/getTicketData")
    @Operation(summary = "查询车票、机票")
    public ResponseEntity<Result> findAll(@RequestParam String type) {
        return ResponseEntity.ok(
                Result.success(ticketService.getTicketList(type))
        );
    }

    @GetMapping("/getTicketStatistics")
    @Operation(summary = "查询出行票据统计信息")
    public ResponseEntity<Result> getTicketStatistics(@RequestParam String type) {
        return ResponseEntity.ok(
                Result.success(ticketService.getTravelStatistics(type))
        );
    }

    @GetMapping("/getTicketDashboard")
    @Operation(summary = "查询出行面板数据")
    public ResponseEntity<Result> getTicketDashboard(@RequestParam String type) {
        return ResponseEntity.ok(
                Result.success(ticketService.getTicketDashboard(type))
        );
    }
}
