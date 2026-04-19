package com.travel.controller;

import com.sysconfig.Result;
import com.travel.dto.TrainRecordQueryDTO;
import com.travel.entity.TrainRecord;
import com.travel.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/train")
public class TrainController {

    @Autowired
    private TrainService trainService;

    @GetMapping("/list")
    public Result list(TrainRecordQueryDTO dto) {
        return Result.success(trainService.list(dto));
    }

    @GetMapping("/get")
    public Result get(Long trainId) {
        return Result.success(trainService.get(trainId));
    }

    @PostMapping("/insert")
    public Result add(@RequestBody TrainRecord record) {
        trainService.insert(record);
        return Result.success(record);
    }

    @PutMapping("/update")
    public Result update(@RequestBody TrainRecord record) {
        trainService.update(record);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(Long trainId) {
        trainService.delete(trainId);
        return Result.success();
    }
}