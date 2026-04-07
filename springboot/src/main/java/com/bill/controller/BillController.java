package com.bill.controller;

import com.bill.entity.BillCategory;
import com.bill.entity.BillRecord;
import com.bill.service.BillService;
import com.sysconfig.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
@Tag(name = "账单管理模块", description = "账单查询、账单记录、分类统计、分类管理")
public class BillController {

    @Autowired
    private BillService billService;



    @GetMapping("/records")
    @Operation(summary = "查询账单记录")
    public ResponseEntity<Result> listRecords(
            @RequestParam(required = false) Integer payType,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) String cateId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer userId
    ) {
        return ResponseEntity.ok(Result.success(billService.queryBillDetails(userId, payType, accountId, cateId, keyword, startTime, endTime)));
    }

    @PostMapping("/record")
    @Operation(summary = "新增账单记录")
    public ResponseEntity<Result> createRecord(@RequestBody BillRecord record) {
        try {
            billService.createBillRecord(record);
            return ResponseEntity.ok(Result.success("账单记录已保存"));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/statistics/categories")
    @Operation(summary = "分类统计")
    public ResponseEntity<Result> categoryStatistics(
            @RequestParam(required = false) Integer payType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer userId
    ) {
        return ResponseEntity.ok(Result.success(billService.categoryStatistics(userId, payType, startTime, endTime)));
    }
}
