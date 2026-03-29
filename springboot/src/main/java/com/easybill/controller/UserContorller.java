package com.easybill.controller;

import com.easybill.common.Result;
import com.easybill.entity.User;
import com.easybill.service.UserService;
import com.easybill.utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserContorller {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/getall")
    public ResponseEntity<Result> findAll() {
        return ResponseEntity.ok(
                Result.success(userService.findAll())
        );
    }


    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody User user) {
        try {
            // 验证账号密码
            User loginUser = userService.login(user.getAccount(), user.getPassword());
            String token = jwtUtil.createToken(loginUser.getUser_id(), loginUser.getAccount());

            loginUser.setPassword(null);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token);
            headers.set("Access-Control-Expose-Headers", "token");

            // 返回 200 + header + 数据
            return new ResponseEntity<>(
                    Result.success("登录成功", loginUser),
                    headers,
                    HttpStatus.OK
            );

        } catch (RuntimeException e) {
            // 登录失败 401
            return new ResponseEntity<>(
                    Result.error(e.getMessage()),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody User user) {
        try {
            userService.register(user);
            return ResponseEntity.ok(Result.success("注册成功"));
        } catch (RuntimeException e) {
            // 参数错误/账号已存在 400
            return new ResponseEntity<>(
                    Result.error(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}