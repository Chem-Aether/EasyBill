package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.TrainRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
public class TrainRecordController {

    @Autowired
    private TrainRecordService trainRecordService;

    @GetMapping("/getall")
    public ResponseEntity<Result> findAll() {
        return ResponseEntity.ok(
                Result.success(trainRecordService.findAll())
        );
    }
}
