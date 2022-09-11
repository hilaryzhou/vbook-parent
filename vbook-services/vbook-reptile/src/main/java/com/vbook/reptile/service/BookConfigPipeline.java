package com.vbook.reptile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.vbook.core.utils.EmptyUtil;
import com.vbook.model.reptile.BookConfig;
import com.vbook.model.reptile.BookInfo;
import com.vbook.reptile.mapper.ReptileMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 21:56
 * @description:
 **/
@Component
public class BookConfigPipeline implements Pipeline {
    @Resource
    private ReptileMapper mapper;

    @Override
    @Transactional
    public void process(ResultItems resultItems, Task task) {
        BookConfig config = resultItems.get("config");
        if (EmptyUtil.isNullOrEmpty(config)) {
           return;
        }
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BookInfo::getName, config.getName());
        boolean flag = mapper.exists(wrapper);
        BookInfo book = new BookInfo();
        book.setAuthor(config.getAuthor());
        book.setAbout(config.getAbout());
        book.setName(config.getName());
        //修改
        if (flag) {
            LambdaUpdateWrapper<BookInfo> update = new LambdaUpdateWrapper<>();
            update.eq(BookInfo::getName, config.getName());
            book.setUpdateTime(new Date());
            mapper.update(book, update);
        }
        //插入
        mapper.insert(book);
    }
}
