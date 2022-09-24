package com.vbook.model.reptile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vbook.model.commons.BaseModel;
import lombok.Data;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 12:48
 * @description: 爬取的书籍详情
 **/
@TableName("t_book")
@Data
public class BookInfo extends BaseModel {

    /**
     * 书名
     */
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 来源
     */
    private String source;
    /**
     * 序列号
     */
    @TableField("serial_no")
    private String SerialNo;
    /**
     * 排序
     */
    @TableField("order_num")
    private Long orderNum;
}
