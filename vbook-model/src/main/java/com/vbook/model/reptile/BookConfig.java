package com.vbook.model.reptile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 20:07
 * @description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookConfig {
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
}
