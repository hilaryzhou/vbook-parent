package com.vbook.exception;


import com.vbook.model.common.enums.HttpCodeEnum;

/**
 * @Description: 抛异常工具类
 * @Version: V1.0
 */
public class CustException {
    public static void cust(HttpCodeEnum codeEnum) {
        throw new BaseException(codeEnum);
    }

    public static void cust(HttpCodeEnum codeEnum, String msg) {
        throw new BaseException(codeEnum, msg);
    }
}