package com.utils.controller;

import com.sysconfig.Result;
import com.utils.entity.RegionCode;
import com.utils.service.RegionCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/area")
public class RegionCodeController {

    @Autowired
    private RegionCodeService regionCodeService;

    @GetMapping("/getByCode")
    public Result getByCode(@RequestParam String code) {
        RegionCode region = regionCodeService.getByCode(code);
        return Result.success(region);
    }

    @GetMapping("/fullName")
    public Result getFullName(@RequestParam String code) {
        String fullName = regionCodeService.getFullRegionName(code);
        return Result.success(fullName);
    }
}