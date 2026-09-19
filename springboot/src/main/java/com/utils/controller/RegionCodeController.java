package com.utils.controller;

import com.sysconfig.Result;
import com.utils.entity.RegionCode;
import com.utils.service.RegionCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/support/area")
@Tag(name = "系统资源接口接口", description = "提供固定的系统资源表")
public class RegionCodeController {

    @Autowired
    private RegionCodeService regionCodeService;

    @GetMapping("/getByCode")
    @Operation(summary = "根据行政区编码查询行政区名称")
    public Result getByCode(@RequestParam String code) {
        RegionCode region = regionCodeService.getByCode(code);
        return Result.success(region);
    }

    @GetMapping("/fullName")
    @Operation(summary = "根据行政区编码查询行政区全名")
    public Result getFullName(@RequestParam String code) {
        String fullName = regionCodeService.getFullRegionName(code);
        return Result.success(fullName);
    }

    @GetMapping("/search")
    @Operation(summary = "按名称片段搜索行政区")
    public Result search(@RequestParam String keyword,
                         @RequestParam(required = false) Integer level) {
        return Result.success(regionCodeService.search(keyword, level));
    }
}
