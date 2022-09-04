package com.vbook.gateway.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Auther: zhouhuan
 * @Date: 2022/8/1-16:57
 */
@Configuration
@Data
public class RequestConfig {
    /**
     * 签名过期时间：秒
     */
    private Integer signTtl = 600;

    /**
     * skipSign
     */
//    @Value("${vbook.manage.auth.skinSign}")
    private Boolean skipSign;


}
