package com.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "jack")
    private String account;

    @Schema(description = "图形验证码内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "ABCD")
    private String captchaCode;

    @Schema(description = "验证码 Key，由 /captcha/image 响应头 Captcha-Key 返回", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaKey;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "newPassword123")
    private String newPassword;
}
