package com.travel.controller;


import com.sysconfig.Result;
import com.travel.service.FootSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
public class FootSpotController{
    @Autowired
    private FootSpotService footSpotService;

    @GetMapping("/getFootprint")
    public ResponseEntity<Result> findAll() {
        return ResponseEntity.ok(
                Result.success(footSpotService.findAll())
        );
    }

    @GetMapping("/statsSpotCount")
    public Result statsCount() {
        return Result.success(footSpotService.statsProvinceCityDistrictCount());
    }

    @GetMapping("/getVisitedCities")
    public Result getVisitedCities(){
        return Result.success(footSpotService.getVisitedCities());
    }
}