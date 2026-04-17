package com.travel.controller;

import com.sysconfig.Result;
import com.travel.dto.TrainRecordQueryDTO;
import com.travel.entity.TrainRecord;
import com.travel.service.TrainRecordService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/trainTickets")
@Tag(name = "火车票管理接口", description = "航班/火车/票务相关接口")
public class TrainRecordController {

    @Autowired
    private TrainRecordService trainRecordService;

    @GetMapping("/list")
    public Result list(
            @Parameter(description = "查询条件")
            TrainRecordQueryDTO dto) {
        return Result.success(
                trainRecordService.getTrainWithStationList(dto)
        );
    }

    @PostMapping("/add")
    public int add(@RequestBody TrainRecord record) {
        return trainRecordService.add(record);
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable Long id) {
        return trainRecordService.deleteById(id);
    }

    @PutMapping("/update")
    public int update(@RequestBody TrainRecord record) {
        return trainRecordService.updateById(record);
    }

    @GetMapping("/get/{id}")
    public TrainRecord get(@PathVariable Long id) {
        return trainRecordService.getById(id);
    }

}