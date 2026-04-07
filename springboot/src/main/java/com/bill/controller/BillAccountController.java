package com.bill.controller;

import com.bill.service.BillAccountService;
import com.sysconfig.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bill")
public class BillAccountController {

    @Autowired
    private BillAccountService billAccountService;

    @GetMapping("/accounts")
    @Operation(summary = "查询账户列表")
    public ResponseEntity<Result> listAccounts(@RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(Result.success(billAccountService.listAccounts(userId)));
    }
}
