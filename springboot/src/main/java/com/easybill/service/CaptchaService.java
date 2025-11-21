package com.easybill.service;

import com.easybill.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 生成验证码，并将验证码文本存入 Redis
     *
     * @return CaptchaUtil.Captcha 包含验证码文本和图片字节数组
     */
    public CaptchaWithKey generateCaptcha() {
        CaptchaUtil.Captcha captcha = CaptchaUtil.generateCaptcha();

        // 生成一个唯一的 key（例如 UUID）
        String captchaKey = UUID.randomUUID().toString();

        // 将验证码文本存入 Redis，设置过期时间为 5 分钟
        redisTemplate.opsForValue().set(captchaKey, captcha.getText(), 5, TimeUnit.MINUTES);

        // 返回验证码对象和 key
        return new CaptchaWithKey(captcha, captchaKey);
    }

    /**
     * 校验用户输入的验证码
     *
     * @param userCaptcha 用户输入的验证码
     * @param captchaKey  Redis 中存储验证码的 key
     * @return 校验结果
     */
    public boolean validateCaptcha(String userCaptcha, String captchaKey) {
        // 从 Redis 中获取验证码文本
        String captcha = redisTemplate.opsForValue().get(captchaKey);

        if (captcha != null && captcha.equalsIgnoreCase(userCaptcha)) {
            // 验证码正确，删除 Redis 中的验证码
            redisTemplate.delete(captchaKey);
            return true;
        }
        return false;
    }

    /**
     * 封装验证码和 key 的对象
     */
    public static class CaptchaWithKey {
        private final CaptchaUtil.Captcha captcha;
        private final String captchaKey;

        public CaptchaWithKey(CaptchaUtil.Captcha captcha, String captchaKey) {
            this.captcha = captcha;
            this.captchaKey = captchaKey;
        }

        public CaptchaUtil.Captcha getCaptcha() {
            return captcha;
        }

        public String getCaptchaKey() {
            return captchaKey;
        }
    }
}
