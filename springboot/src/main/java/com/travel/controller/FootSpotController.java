package com.travel.controller;


import com.sysconfig.Result;
import com.travel.entity.FootSpot;
import com.travel.service.FootSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel")
@Tag(name = "足迹地点管理接口", description = "点亮足迹地点")
public class FootSpotController{
    @Autowired
    private FootSpotService footSpotService;

    @GetMapping("/footprints")
    @Operation(summary = "查询足迹管理列表")
    public Result footprints(@RequestParam(required = false) Integer year) {
        return Result.success(footSpotService.findAllWithRegion(year));
    }

    @PostMapping("/footprints")
    @Operation(summary = "新增足迹并点亮所属地区")
    public Result add(@RequestBody FootSpot spot) {
        return Result.success(footSpotService.add(spot));
    }

    @PutMapping("/footprints/{id}")
    @Operation(summary = "修改足迹")
    public Result update(@PathVariable Long id, @RequestBody FootSpot spot) {
        return Result.success(footSpotService.update(id, spot));
    }

    @DeleteMapping("/footprints/{id}")
    @Operation(summary = "删除足迹")
    public Result delete(@PathVariable Long id) {
        footSpotService.delete(id);
        return Result.success();
    }
}
