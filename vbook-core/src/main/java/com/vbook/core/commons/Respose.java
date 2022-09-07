package com.vbook.core.commons;

import com.vbook.core.enums.HttpCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: zhouhuan
 * @date: 2022-09-08 00:57
 * @description: 通用响应信息主体
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Respose<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer code;
    private String msg;
    private T data;

    public static <T> Respose<T> ok() {
        return restResult(null, HttpCodeEnum.SUCCESS);
    }

    public static <T> Respose<T> ok(T data) {
        return restResult(data, HttpCodeEnum.SUCCESS);
    }

    public static <T> Respose<T> ok(T data, String msg) {
        return restResult(data, HttpCodeEnum.SUCCESS, msg);
    }

    public static <T> Respose<T> failed(HttpCodeEnum codeEnum) {
        return restResult(null, codeEnum);
    }

    public static <T> Respose<T> failed(HttpCodeEnum codeEnum, String msg) {
        return restResult(null, codeEnum, msg);
    }

    private static <T> Respose<T> restResult(T data, HttpCodeEnum httpCodeEnum) {
        Respose<T> apiResult = new Respose<>();
        apiResult.setData(data);
        apiResult.setCode(httpCodeEnum.getCode());
        apiResult.setMsg(httpCodeEnum.getMsg());
        return apiResult;
    }

    private static <T> Respose<T> restResult(T data, HttpCodeEnum httpCodeEnum, String msg) {
        Respose<T> apiResult = new Respose<>();
        apiResult.setData(data);
        apiResult.setCode(httpCodeEnum.getCode());
        apiResult.setMsg(msg);
        return apiResult;
    }

    public boolean checkCode() {
        return this.getCode() == 0;
    }
}
