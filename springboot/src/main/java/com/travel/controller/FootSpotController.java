package com.travel.controller;


import com.sysconfig.Result;
import com.travel.entity.FootSpot;
import com.travel.service.FootSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/footprints")
    @Operation(summary = "查询足迹管理列表")
    public Result footprints() {
        return Result.success(footSpotService.findAllWithRegion());
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
