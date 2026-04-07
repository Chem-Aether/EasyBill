package com.user.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
public class User {
    @TableId
    private Integer userId;          // 用户ID号
    private String account;           // 账号
    private String userName;         // 姓名
    private String password;          // 密码
    private String role;              // 用户类型：超级管理员0；管理员1；普通用户2
}
