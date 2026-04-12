package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
public class TicketController {

    @Autowired
    private TicketService ticketService;

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
