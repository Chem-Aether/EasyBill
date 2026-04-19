package com.utils.controller;

import com.sysconfig.Result;
import com.utils.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/support/Airports")
@Tag(name = "系统资源接口接口", description = "提供固定的系统资源表")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping("/all")
    @Operation(summary = "查询所有支持的机场")
    public Result getAllAirports() {
        return Result.success(airportService.getAllAirports());
    }

    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        return Result.success(airportService.search(keyword));
    }
}
