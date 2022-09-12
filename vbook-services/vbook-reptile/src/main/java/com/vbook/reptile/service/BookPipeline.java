package com.vbook.reptile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.vbook.core.utils.EmptyUtil;
import com.vbook.model.reptile.BookInfo;
import com.vbook.reptile.mapper.BookMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class BookPipeline implements Pipeline {
    @Resource
    private BookMapper mapper;

    @Override
    @Transactional
    public void process(ResultItems resultItems, Task task) {
        BookInfo book = resultItems.get("book");
        if (EmptyUtil.isNullOrEmpty(book)) {
            return;
        }
        //如果书名和标题都一样则判断同一条记录
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BookInfo::getName, book.getName())
                .eq(BookInfo::getTitle, book.getTitle());
        boolean flag = mapper.exists(wrapper);
        //不为空走更新
        if (flag) {
            LambdaUpdateWrapper<BookInfo> update = new LambdaUpdateWrapper<>();
            update.eq(BookInfo::getName, book.getName())
                    .eq(BookInfo::getTitle, book.getTitle());
            book.setUpdateTime(new Date());
            mapper.update(book, update);
        }
        //为空走插入
        mapper.insert(book);
    }
}