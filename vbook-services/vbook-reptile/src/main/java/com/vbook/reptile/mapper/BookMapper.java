package com.vbook.reptile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vbook.model.reptile.BookInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 12:47
 * @description: 保存爬取的小说内容
 **/
@Mapper
public interface BookMapper extends BaseMapper<BookInfo> {
    /**
     * 定时删除表中重复内容
     */
    void clearDuplicate();
}
