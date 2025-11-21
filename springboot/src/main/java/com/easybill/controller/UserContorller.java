package com.easybill.controller;

import com.easybill.common.Result;
import com.easybill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/user")
public class UserContorller {
    @Autowired
    private UserService userService;

    @GetMapping("/getall")
    public Result findAll() {
        return Result.success(userService.findAll());
    }

    @PostMapping("/login")
    public  Result login() {
        return  Result.success();
    }
}
