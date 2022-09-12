package com.vbook.reptile.task;

import com.vbook.reptile.mapper.BookConfigMapper;
import com.vbook.reptile.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 00:01
 * @description: 定时删除小说重复章节
 **/
@Slf4j
@Component
@ConditionalOnProperty(prefix = "vbook.manage.scheduling.clear", name = "enabled", havingValue = "true")
public class ClearDuplicateSchedule {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookConfigMapper bookConfigMapper;

    @Async
    @Scheduled(cron = "0 0/30 * * * ? ")
    public void incrementalCrawl() {
        Long start = System.currentTimeMillis();
        THREAD_LOCAL.set(start);
        bookMapper.clearDuplicate();
        bookConfigMapper.clearDuplicate();
        Long take = System.currentTimeMillis() - THREAD_LOCAL.get();
        log.info("进行定时删除重复标题数据,耗时=>{} ms", take);
    }
}
