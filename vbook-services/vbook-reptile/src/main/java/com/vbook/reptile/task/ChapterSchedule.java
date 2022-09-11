package com.vbook.reptile.task;

import com.vbook.reptile.config.RedisScheduler;
import com.vbook.reptile.service.BookConfigPipeline;
import com.vbook.reptile.service.BookPipeline;
import com.vbook.reptile.service.ChapterPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

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
    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;


//    @Async
//    @Scheduled(cron = "0 0 1 * * ? ")
//    public void crawlTasksRegular() {
//        log.info("爬取笔趣阁全部小说");
//        Spider.create(new ChapterPageProcessor()).addUrl(B5200_URL).addPipeline(bookConfigPipeline)
//                .addPipeline(bookPipeline).thread(5).runAsync();
//    }
    @Async
    @Scheduled(cron = "0 0/10 * * * ?")
    public void incrementalCrawl() {
        log.info("增量爬取笔趣阁小说");
        Spider.create(new ChapterPageProcessor())
                //从这个url开始抓取
                .addUrl(B5200_URL)
                //使用redis进行去重，达到增量爬取的目的
                .setScheduler(new RedisScheduler(redisTemplate))
                //设置5个线程同时抓取
                .thread(5)
                //使用自己的Pipeline将结果保存到数据库中
                .addPipeline(bookConfigPipeline)
                .addPipeline(bookPipeline).runAsync();
    }
}
