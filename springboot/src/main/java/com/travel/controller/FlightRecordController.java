package com.travel.controller;

import com.sysconfig.Result;
import com.travel.dto.FlightRecordQueryDTO;
import com.travel.entity.FlightRecord;
import com.travel.service.FlightRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel/flightTickets")
@Tag(name = "机票管理接口", description = "航班/火车/票务相关接口")
public class FlightRecordController {

    @Autowired
    private FlightRecordService flightRecordService;

    // 条件查询
    @GetMapping("/list")
    public Result list(FlightRecordQueryDTO dto) {
        if (dto == null) dto = new FlightRecordQueryDTO();
        return Result.success(flightRecordService.list(dto));
    }

    // 单个新增
    @PostMapping("/insert")
    public Result save(@RequestBody FlightRecord flightRecord) {
        return Result.success(flightRecordService.save(flightRecord));
    }

    // 批量新增
    @PostMapping("/insertBatch")
    public Result saveBatch(@RequestBody List<FlightRecord> list) {
        return Result.success(flightRecordService.saveBatch(list));
    }

    // 修改
    @PutMapping("/update")
    public Result update(@RequestBody FlightRecord flightRecord) {
        return Result.success(flightRecordService.updateById(flightRecord));
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result remove(@PathVariable Long id) {
        return Result.success(flightRecordService.removeById(id));
    }
}
