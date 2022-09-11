package com.vbook.reptile.service;

import com.vbook.model.reptile.BookInfo;
import com.vbook.reptile.mapper.ReptileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 11:24
 * @description:
 **/
@Component
@Slf4j
public class ChapterPageProcessor implements PageProcessor {
    @Autowired
    ReptileMapper reptileMapper;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");


    @Override
    public void process(Page page) {
        //获取首页
        String url = page.getUrl().get();

        if (url.contains(".html")) {
            //如果是章节的链接则进入
            this.getContent(page);
        } else if (url.equals("https://www.biquge9.com/xuanhuan/")) {
            //获取全部链接
            List<String> links = page.getHtml().links().regex(".*book/\\d+/").all();
            //加入请求队列等待处理
            page.addTargetRequests(links);
            //跳过保存管道类
            page.setSkip(true);
        } else {
            //获取该小说的所有章节的链接
            List<String> links = page.getHtml().links()
                    .regex(".*\\d+\\.html").all();
            //加入请求队列等待处理
            page.addTargetRequests(links);
            //跳过保存管道类
            page.setSkip(true);
        }
    }
    /**
     * 获得爬虫内容
     * @param page
     */
    private void getContent(Page page) {
        //书名
        String name = page.getHtml().xpath("//div[@class='path wap_none']/a[2]/text()").get();
        //标题
        String title = page.getHtml().xpath("//div[@class='content']/h1/text()").get();
        //内容
        List<String> all = page.getHtml().xpath("//div[@id='chaptercontent']/text()").all();
        String content = all.stream().collect(Collectors.joining("::"));
        BookInfo bookInfo = new BookInfo();
        bookInfo.setName(name);
        bookInfo.setTitle(title);
        bookInfo.setContent(content);
        bookInfo.setSource("笔趣阁");
        bookInfo.setCreateTime(new Date());
        //把结果传递给pipeline
        page.putField("book", bookInfo);

    }

    @Override
    public Site getSite() {
        return site;
    }
}
