package com.user.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Integer user_id;          // 用户ID号
    private String account;           // 账号
    private String user_name;         // 姓名
    private String password;          // 密码
    private String role;              // 用户类型：超级管理员0；管理员1；普通用户2
    private Date create_time;         // 创建时间
    private Date update_time;         // 更改时间
}
