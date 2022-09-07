package com.vbook.core.constants;

/**
 * @author: zhouhuan
 * @date: 2022-09-08 00:15
 * @description:
 **/
public class SecurityConstants {
    /**
     * 角色前缀
     */
    public static final String ROLE = "ROLE_";
    /**
     * 前缀
     */
    public static final String PROJECT_PREFIX = "vbook";
    /**
     * 内部
     */
    public static final String FROM_IN = "Y";
    /**
     * 标志
     */
    public static final String FROM = "from";
    /**
     * 默认登录URL
     */
    public static final String OAUTH_TOKEN_URL = "/oauth2/token";
    /**
     * 验证码有效期,默认 60秒
     */
    public static final long CODE_TIME = 60;
    /**
     * 验证码长度
     */
    public static final String CODE_SIZE = "6";


}
