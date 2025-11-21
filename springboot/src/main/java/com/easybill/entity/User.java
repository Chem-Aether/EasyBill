package com.easybill.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Integer user_id;          // 用户ID号
    private String user_name;         // 姓名
    private Integer age;             // 年龄
    private Integer gender;          // 性别，女0，男1
    private String ethnicity;        // 民族
    private String grade;            // 年级
    private String class_name;        // 班级
    private String education;        // 学制（如：本科、硕士、博士）
    private String stage;            // 目前培养阶段
    private String group;            // 组织名
    private Integer role;            // 用户类型：超级管理员0；管理员1；普通用户2
    private Date create_time;         // 创建时间
    private Date update_time;         // 更改时间
}
