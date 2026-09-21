package com.travel.controller;

import com.sysconfig.Result;
import com.travel.dto.TrainRecordQueryDTO;
import com.travel.dto.TrainTicketSaveRequestDTO;
import com.travel.entity.TrainRecord;
import com.travel.service.TrainTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 车票接口（对外分开）：
 * - 列表只返回车票+途径站数量
 * - 详情不展开途径站（站点详情走独立接口）
 * - 保存接口支持同时传入 stations，后端事务原子保存
 */
@RestController
@RequestMapping("/travel/trains")
@Tag(name = "火车票管理接口", description = "列表/详情/保存/删除")
public class TrainTicketController {

    @Autowired
    private TrainTicketService trainTicketService;

    @GetMapping
    public Result list(TrainRecordQueryDTO dto) {
        return Result.success(trainTicketService.listWithStationCount(dto));
    }

    @GetMapping("/{trainId}")
    public Result get(@PathVariable Long trainId) {
        TrainRecord ticket = trainTicketService.getTicket(trainId);
        return Result.success(ticket);
    }

    @PostMapping
    public Result add(@RequestBody TrainTicketSaveRequestDTO dto) {
        Long trainId = trainTicketService.add(dto);
        return Result.success(trainId);
    }

    @PutMapping("/{trainId}")
    public Result update(@PathVariable Long trainId, @RequestBody TrainTicketSaveRequestDTO dto) {
        dto.getTicket().setTrainId(trainId);
        Long updatedTrainId = trainTicketService.update(dto);
        return Result.success(updatedTrainId);
    }

    @DeleteMapping("/{trainId}")
    public Result delete(@PathVariable Long trainId) {
        trainTicketService.delete(trainId);
        return Result.success();
    }
}
