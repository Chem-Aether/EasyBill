package com.easybill.controller;

import com.easybill.common.Result;
import com.easybill.service.CaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取验证码图片
     */
    @GetMapping("/image")
    public void getCaptchaImage(HttpServletResponse response) throws IOException {
        // 生成验证码
        CaptchaService.CaptchaWithKey captchaWithKey = captchaService.generateCaptcha();

        // 将验证码 key 作为响应头返回
        response.setHeader("Captcha-Key", captchaWithKey.getCaptchaKey());

        // 设置响应内容类型为图片
        response.setContentType("image/jpeg");
        response.getOutputStream().write(captchaWithKey.getCaptcha().getImageBytes());
        response.getOutputStream().flush();
    }

    /**
     * 校验验证码
     */
    @GetMapping("/validate")
    public Result validateCaptcha(
            @RequestParam String userCaptcha,  // 从 URL 查询参数中获取用户输入的验证码
            @RequestParam String captchaKey   // 从 URL 查询参数中获取验证码的 key
    ) {
        boolean isValid = captchaService.validateCaptcha(userCaptcha, captchaKey);
        return isValid ? Result.success() : Result.error("验证码错误");
    }
}