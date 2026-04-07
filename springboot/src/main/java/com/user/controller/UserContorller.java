package com.user.controller;

import com.sysconfig.Result;
import com.user.dto.ForgotPasswordRequest;
import com.user.entity.User;
import com.user.service.CaptchaService;
import com.user.service.UserService;
import com.user.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户模块", description = "用户登录、注册、密码重置接口")
public class UserContorller {

    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/getall")
    @Operation(summary = "查询所有用户")
    public ResponseEntity<Result> findAll() {
        return ResponseEntity.ok(
                Result.success(userService.findAll())
        );
    }


    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<Result> login(@RequestBody User user) {
        try {
            // 验证账号密码
            User loginUser = userService.login(user.getAccount(), user.getPassword());
            String token = jwtUtil.createToken(loginUser.getUserId(), loginUser.getAccount());

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
    @Operation(summary = "用户注册")
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

    @PostMapping("/forgot-password")
    @Operation(summary = "忘记密码（图像验证码验证）")
    public ResponseEntity<Result> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            boolean valid = captchaService.validateCaptcha(request.getCaptchaCode(), request.getCaptchaKey());
            if (!valid) {
                return new ResponseEntity<>(
                        Result.error("验证码错误或已过期"),
                        HttpStatus.BAD_REQUEST
                );
            }
            userService.resetPassword(request.getAccount(), request.getNewPassword());
            return ResponseEntity.ok(Result.success("密码已重置"));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    Result.error(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}