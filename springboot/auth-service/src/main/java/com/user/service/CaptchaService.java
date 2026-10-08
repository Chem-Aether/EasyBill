package com.user.service;

import com.user.utils.CaptchaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    // 常量提取
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;

    // 构造器注入
    private final StringRedisTemplate redisTemplate;

    /**
     * 生成验证码，存入 Redis，返回验证码图片 + key
     */
    public CaptchaWithKey generateCaptcha() {
        CaptchaUtil.Captcha captcha = CaptchaUtil.generateCaptcha();
        String captchaKey = UUID.randomUUID().toString();

        // 存入 Redis 5分钟过期
        redisTemplate.opsForValue()
                .set(captchaKey, captcha.getText(), CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return new CaptchaWithKey(captcha, captchaKey);
    }

    /**
     * 校验验证码
     */
    public boolean validateCaptcha(String userInputCode, String captchaKey) {
        if (userInputCode == null || captchaKey == null) {
            return false;
        }

        String correctCode = redisTemplate.opsForValue().get(captchaKey);
        if (correctCode == null) {
            return false;
        }

        // 验证成功 → 删除验证码，防止重复使用
        boolean isValid = correctCode.equalsIgnoreCase(userInputCode);
        if (isValid) {
            redisTemplate.delete(captchaKey);
        }

        return isValid;
    }

    /**
     * 验证码返回对象（标准静态内部类 + Lombok 简化）
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class CaptchaWithKey {
        private CaptchaUtil.Captcha captcha;
        private String captchaKey;
    }
}