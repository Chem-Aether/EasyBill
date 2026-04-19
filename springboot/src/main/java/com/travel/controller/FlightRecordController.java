package com.travel.controller;

import com.sysconfig.Result;
import com.travel.dto.FlightRecordQueryDTO;
import com.travel.entity.FlightRecord;
import com.travel.service.FlightRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    // 修改
    @PutMapping("/update")
    public Result update(@RequestBody FlightRecord flightRecord) {
        return Result.success(flightRecordService.updateById(flightRecord));
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result remove(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("ID不能为空");
        }
        boolean success = flightRecordService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
