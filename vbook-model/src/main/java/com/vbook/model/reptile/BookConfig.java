package com.vbook.model.reptile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vbook.model.BaseModel;
import lombok.Data;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 20:07
 * @description:
 **/
@Data
@TableName("t_book_config")
public class BookConfig extends BaseModel {
    /**
     * 书名
     */
    private String name;
    /**
     * 作者
     */
    private String author;
    /**
     * 简介
     */
    private String about;
    /**
     * 类型
     */
    private String type;
    /**
     * 状态
     */
    private String status;
    /**
     * 序列号
     */
    @TableField("serial_no")
    private String SerialNo;
}
