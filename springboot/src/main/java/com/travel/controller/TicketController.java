package com.travel.controller;

import com.sysconfig.Result;
import com.travel.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/getTicketData")
    public ResponseEntity<Result> findAll(@RequestParam String type) {
        return ResponseEntity.ok(
                Result.success(ticketService.getTicketList(type))
        );
    }
}
