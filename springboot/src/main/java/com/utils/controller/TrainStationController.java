package com.utils.controller;

import com.sysconfig.Result;
import com.utils.service.TrainStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/support/TrainStations")
@RequiredArgsConstructor
public class TrainStationController {

    private final TrainStationService trainStationService;

    @GetMapping("/all")
    public Result getAllStations() {
        return Result.success(trainStationService.getAllStations());
    }

    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        return Result.success(trainStationService.search(keyword));
    }
}