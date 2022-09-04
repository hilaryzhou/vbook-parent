package com.vbook.model.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @auther: zhouhuan
 * @Date: 2022/8/24-16:07
 */
@Data
@TableName("t_ssokey")
public class SsoKey {
    @TableId(value = "access_key")
    private String accessKey;
    @TableField("secret_key")
    private String secret;
    /**
     * 描述信息
     */
    @TableField("description")
    private String description;

}
