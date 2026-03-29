package com.easybill.controller;

import com.easybill.common.Result;
import com.easybill.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    // 标准构造器注入
    private final CaptchaService captchaService;

    /**
     * 获取验证码图片
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> getCaptchaImage() {
        // 生成验证码
        CaptchaService.CaptchaWithKey captchaWithKey = captchaService.generateCaptcha();

        // 响应头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Captcha-Key", captchaWithKey.getCaptchaKey());
        headers.setContentType(MediaType.IMAGE_JPEG);

        // 返回：200 + 头 + 图片字节流
        return new ResponseEntity<>(
                captchaWithKey.getCaptcha().getImageBytes(),
                headers,
                HttpStatus.OK
        );
    }

    /**
     * 校验验证码
     */
    @GetMapping("/validate")
    public ResponseEntity<Result> validateCaptcha(
            @RequestParam String userCaptcha,
            @RequestParam String captchaKey
    ) {
        boolean isValid = captchaService.validateCaptcha(userCaptcha, captchaKey);

        if (isValid) {
            return ResponseEntity.ok(Result.success("验证码正确"));
        } else {
            return new ResponseEntity<>(
                    Result.error("验证码错误或已过期"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}