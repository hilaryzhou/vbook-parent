package com.vbook.model.reptile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vbook.model.BaseModel;
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
     * 类型
     */
    private String type;
    /**
     * 排序
     */
    @TableField("order_num")
    private Long orderNum;
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
     * 作者
     */
    private String author;
    /**
     * 简介
     */
    private String about;
}
