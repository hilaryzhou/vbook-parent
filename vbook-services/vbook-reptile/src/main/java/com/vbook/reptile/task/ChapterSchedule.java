package com.vbook.reptile.task;

import com.vbook.reptile.service.BookConfigPipeline;
import com.vbook.reptile.service.BookPipeline;
import com.vbook.reptile.service.ChapterPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.annotation.Resource;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 00:01
 * @description: 定时爬取小说章节详情
 **/
@Slf4j
@Component
@ConditionalOnProperty(prefix = "vbook.manage.scheduling.reptile", name = "enabled", havingValue = "true")
public class ChapterSchedule {
    private static final String B5200_URL = "http://www.b5200.net/xiaoshuodaquan/";
    @Resource
    private BookPipeline bookPipeline;
    @Resource
    private BookConfigPipeline bookConfigPipeline;

    @Async
    @Scheduled(cron = "0 0 1 * * ? ")
    public void crawlTasksRegular() {
        log.info("爬取笔趣阁全部小说");
        Spider.create(new ChapterPageProcessor()).addUrl(B5200_URL).addPipeline(bookConfigPipeline)
                .addPipeline(bookPipeline).setScheduler(new QueueScheduler().setDuplicateRemover(
                        new BloomFilterDuplicateRemover(10000000))).thread(5).runAsync();

    }
}
