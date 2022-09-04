package com.vbook.model.common;

import com.vbook.model.common.enums.HttpCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 通用的结果返回类
 * @Auther: zhouhuan
 * @Date: 2022/8/1-16:11
 */

@Data
@Slf4j
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;

    public Response() {
    }

    public Response(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Response(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Response success(Object data) {
        Response result = setHttpCodeEnum(HttpCodeEnum.SUCCESS, HttpCodeEnum.SUCCESS.getErrorMessage());
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static Response success() {
        return success(null);
    }

    public static Response success(int code, String msg) {
        Response result = new Response();
        return result.ok(code, null, msg);
    }

    public static Response fail(HttpCodeEnum codeEnum) {
        return setHttpCodeEnum(codeEnum, codeEnum.getErrorMessage());
    }

    public static Response fail(HttpCodeEnum codeEnum, String errorMessage) {
        return setHttpCodeEnum(codeEnum, errorMessage);
    }

    public static Response setHttpCodeEnum(HttpCodeEnum codeEnum) {
        return success(codeEnum.getCode(), codeEnum.getErrorMessage());
    }

    private static Response setHttpCodeEnum(HttpCodeEnum codeEnum, String errorMessage) {
        return success(codeEnum.getCode(), errorMessage);
    }

    public Response<?> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public Response<?> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        return this;
    }

    public Response<?> ok(T data) {
        this.data = data;
        return this;
    }

    public boolean checkCode() {
        if(this.getCode().intValue() != 0){
            return false;
        }
        return true;
    }
}
