package com.travel.controller;


import com.sysconfig.Result;
import com.travel.service.FootSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
@Tag(name = "足迹地点管理接口", description = "点亮足迹地点")
public class FootSpotController{
    @Autowired
    private FootSpotService footSpotService;

    @GetMapping("/getFootprint")
    @Operation(summary = "查询去过的所有地点")
    public ResponseEntity<Result> findAll() {
        return ResponseEntity.ok(
                Result.success(footSpotService.findAll())
        );
    }

    @GetMapping("/statsSpotCount")
    @Operation(summary = "查询各行政区级别数")
    public Result statsCount() {
        return Result.success(footSpotService.statsProvinceCityDistrictCount());
    }

    @GetMapping("/getVisitedCities")
    @Operation(summary = "查询已探索城市列表")
    public Result getVisitedCities(){
        return Result.success(footSpotService.getVisitedCities());
    }
}