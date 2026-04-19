package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.TrainStationRouterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("travelTrainStationController")
@RequestMapping("/travel/trainStations")
@Tag(name = "火车票管理接口", description = "按车票ID获取途径站明细（用于详情展开）")
public class TrainStationController {

    @Autowired
    private TrainStationRouterService trainStationRouterService;

    @GetMapping("/list/{trainId}")
    public Result listByTrainId(@PathVariable Long trainId) {
    return Result.success(trainStationRouterService.listByTrainId(trainId));
    }
}
