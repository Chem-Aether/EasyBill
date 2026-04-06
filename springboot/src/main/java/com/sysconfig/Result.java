package com.sysconfig;

import lombok.Data;

/**
 * 统一返回结果（标准RESTful风格）
 * 状态码走 HTTP 响应码，此类不再存放 code
 */
@Data
public class Result {

    // 提示信息
    private String msg;

    // 返回的数据
    private Object data;

    // 私有化构造
    private Result() {}

    // ===================== 成功返回 =====================
    public static Result success() {
        Result result = new Result();
        result.setMsg("操作成功");
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // ===================== 失败返回 =====================
    public static Result error(String msg) {
        Result result = new Result();
        result.setMsg(msg);
        return result;
    }
}
