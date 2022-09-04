package com.vbook.model.common.enums;


/**
 * @Description: 平台统一错误码枚举
 * @Auther: zhouhuan
 * @Date: 2022/8/1-16:19
 */
public enum HttpCodeEnum {

    SUCCESS(0, "success"),
    UNKNOW_ERROR(-1, "unknow error"),
    INVALID_PARAM_ERROR(24001, "invalid params"),
    REQUEST_PARAM_MISS(24002, "请求校验参数为空,请添加！"),
    SYSTEM_ERROR(24003, "system error"),
    SIGN_ERROR(24004, "签名校验失败,请检查签名格式！"),
    TARGER_NOT_EXIST_ERROR(24005, "target not exist error"),
    PERMISSION_ERROR(24006, "permission error"),
    SIGN_TIME_EXPIRE(24007, "sign time expire"),
    REDIS_HOST_IP_NULL(24008, "host ip is null"),
    REMOTE_SERVER_ERROR(24009, "remote server error"),
    GENERATE_SIGN_ERROR(24010, "generate Signature error");


    int code;
    String errorMessage;

    HttpCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
