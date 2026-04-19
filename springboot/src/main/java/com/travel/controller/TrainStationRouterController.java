package com.travel.controller;

import com.sysconfig.Result;
import com.travel.dto.TrainStationBatchDTO;
import com.travel.service.TrainStationRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/train-station")
public class TrainStationRouterController {

    @Autowired
    private TrainStationRouterService stationService;

    @GetMapping("/list")
    public Result list(Long trainId) {
        return Result.success(stationService.listByTrainId(trainId));
    }

    @PostMapping("/save-batch")
    public Result saveBatch(@RequestBody TrainStationBatchDTO dto) {
        stationService.saveBatch(dto.getTrainId(), dto.getStationList());
        return Result.success();
    }
}