package com.bill.controller;

import com.bill.dto.AccountRequest;
import com.bill.dto.BillTransactionRequest;
import com.bill.service.BillService;
import com.sysconfig.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/bill")
public class BillController {
    public record CategoryRequest(String name, Long parentId, Integer sortOrder, String icon) {}
    public record CategoryUpdateRequest(String name, String icon) {}
    public record ImportRecordsRequest(List<BillTransactionRequest> records) {}

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/accounts")
    public Result accounts() { return Result.success(billService.accounts()); }

    @PostMapping("/accounts")
    public Result createAccount(@RequestBody AccountRequest request) {
        return Result.success(billService.createAccount(request));
    }

    @PutMapping("/accounts/{id}")
    public Result updateAccount(@PathVariable long id, @RequestBody AccountRequest request) {
        billService.updateAccount(id, request);
        return Result.success();
    }

    @DeleteMapping("/accounts/{id}")
    public Result deleteAccount(@PathVariable long id) {
        billService.deleteAccount(id);
        return Result.success();
    }

    @GetMapping("/categories")
    public Result categories() { return Result.success(billService.categories()); }

    @PostMapping("/categories")
    public Result createCategory(@RequestBody CategoryRequest request) {
        return Result.success(billService.createCategory(request.name(), request.parentId(), request.sortOrder(), request.icon()));
    }

    @PutMapping("/categories/{id}")
    public Result updateCategory(@PathVariable long id, @RequestBody CategoryUpdateRequest request) {
        billService.updateCategory(id, request.name(), request.icon());
        return Result.success();
    }

    @DeleteMapping("/categories/{id}")
    public Result deleteCategory(@PathVariable long id) {
        billService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/accounts/{id}/records")
    public Result accountRecords(@PathVariable long id,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 @RequestParam(defaultValue = "occurredAt") String sortBy,
                                 @RequestParam(defaultValue = "desc") String sortOrder) {
        if (page < 1 || pageSize < 1 || pageSize > 100)
            throw new ResponseStatusException(BAD_REQUEST, "分页参数无效");
        if (!java.util.List.of("asc", "desc").contains(sortOrder.toLowerCase()))
            throw new ResponseStatusException(BAD_REQUEST, "排序方向无效");
        return Result.success(billService.accountRecords(id, page, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/records")
    public Result records(@RequestParam(required = false) String direction,
                          @RequestParam(required = false) Long accountId,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String startTime,
                          @RequestParam(required = false) String endTime) {
        return Result.success(billService.records(direction, accountId, categoryId, keyword, startTime, endTime));
    }

    @PostMapping("/records/import")
    public Result importRecords(@RequestBody ImportRecordsRequest request) {
        if (request == null) throw new ResponseStatusException(BAD_REQUEST, "导入数据不能为空");
        return Result.success("导入完成", billService.importRecords(request.records()));
    }

    @GetMapping(value = "/records/export", produces = "text/csv;charset=UTF-8")
    public ResponseEntity<byte[]> exportRecords(@RequestParam(required = false) String direction,
                                                @RequestParam(required = false) Long accountId,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String startTime,
                                                @RequestParam(required = false) String endTime) {
        byte[] csv = billService.exportRecords(direction, accountId, categoryId, keyword, startTime, endTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill-records.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv);
    }

    @PostMapping("/records")
    public Result createRecord(@RequestBody BillTransactionRequest request) {
        return Result.success(billService.createRecord(request));
    }

    @PutMapping("/records/{id}")
    public Result updateRecord(@PathVariable long id, @RequestBody BillTransactionRequest request) {
        billService.updateRecord(id, request);
        return Result.success();
    }

    @DeleteMapping("/records/{id}")
    public Result deleteRecord(@PathVariable long id) {
        billService.deleteRecord(id);
        return Result.success();
    }

    @GetMapping("/statistics/summary")
    public Result summary(@RequestParam(required = false) String startTime,
                          @RequestParam(required = false) String endTime) {
        return Result.success(billService.summary(startTime, endTime));
    }

    @GetMapping("/statistics/timeline")
    public Result timeline(@RequestParam int year) {
        if (year < 1900 || year > 2200)
            throw new ResponseStatusException(BAD_REQUEST, "统计年份无效");
        return Result.success(billService.timeline(year));
    }

    @GetMapping("/statistics/categories")
    public Result categoryStatistics(@RequestParam(required = false) String direction,
                                     @RequestParam(required = false) String startTime,
                                     @RequestParam(required = false) String endTime,
                                     @RequestParam(defaultValue = "false") boolean detailed) {
        return Result.success(billService.categoryStatistics(direction, startTime, endTime, detailed));
    }
}
