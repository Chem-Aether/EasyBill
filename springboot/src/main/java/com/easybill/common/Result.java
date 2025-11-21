package com.easybill.common;

import lombok.Data;

@Data
public class Result {

//    状态码
    private String code;

    // 状态信息
    private String msg;

    //返回数据
    private Object data;

    public static Result success(){
        Result result = new Result();

        result.setCode("200");
        return result;
    }

    public static Result success(Object data){
        Result result = new Result();

        result.setCode("200");

        result.setData(data);
        return result;
    }

    public static Result error(String msg){
        Result result = new Result();

        result.setCode("500");
        result.setMsg(msg);
        return result;
    }
}
