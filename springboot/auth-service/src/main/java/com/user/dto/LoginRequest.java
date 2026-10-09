package com.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String account;
    private String password;
    private String captchaCode;
    private String captchaKey;
}
