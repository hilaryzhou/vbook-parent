package com.vbook.reptile.task;

import com.vbook.reptile.service.BookPipeline;
import com.vbook.reptile.service.ChapterPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @Resource
    private BookPipeline pipeline;

    private static final String FANTASY = "xuanhuan/";
    @Scheduled(cron = "0 0 1 * * ? ")
    public void crawlTasksRegular() {
        log.info("爬取笔趣阁玄幻频道");
        Spider.create(new ChapterPageProcessor()).addUrl("https://www.biquge9.com/" + FANTASY)
                .addPipeline(pipeline)
                .thread(5).runAsync();
    }
}
