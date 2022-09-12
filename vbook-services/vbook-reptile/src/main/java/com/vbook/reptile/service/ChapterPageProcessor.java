package com.vbook.reptile.service;

import com.vbook.model.reptile.BookConfig;
import com.vbook.model.reptile.BookInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author: zhouhuan
 * @date: 2022-09-11 11:24
 * @description:
 **/
@Component
@Slf4j
public class ChapterPageProcessor implements PageProcessor {

    /**
     * 常用 user agent 列表
     */
    static List<String> USER_AGENT = new ArrayList<String>(10) {
        {
            add("Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");
            add("Mozilla/5.0 (Linux; U; Android 4.0.4; en-gb; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            add("Mozilla/5.0 (Linux; U; Android 2.2; en-gb; GT-P1000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            add("Mozilla/5.0 (Android; Mobile; rv:14.0) Gecko/14.0 Firefox/14.0");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
            add("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
            add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
            add("Mozilla/5.0 (iPod; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3A101a Safari/419.3");
        }
    };

    private Site site = Site.me().setRetryTimes(10).setSleepTime(1000).setTimeOut(10000)
            .addHeader("User-Agent", USER_AGENT.get(new Random().nextInt(USER_AGENT.size())));

    @Override
    public void process(Page page) {
        //获取首页：http://www.b5200.net/xiaoshuodaquan/
        String url = page.getUrl().get();
        if (url.contains(".html")) {
            //如果是章节的链接则进入
            this.getContent(page);
        } else if (url.equals("http://www.b5200.net/xiaoshuodaquan/")) {
            //获取全部链接
            List<String> links = page.getHtml().links().regex("http:\\/\\/www.b5200.net\\/14_\\d+").all();
            //加入请求队列等待处理
            page.addTargetRequests(links);
            //跳过保存管道类
            page.setSkip(true);
        } else {
            //获取该小说的所有章节的链接
            List<String> links = page.getHtml().links()
                    .regex(".*\\d+\\.html").all();
            this.getOtherInfo(page);
            //加入请求队列等待处理
            page.addTargetRequests(links);
        }
    }

    /**
     * 获取其他配置信息
     * @param page
     */
    private void getOtherInfo(Page page) {
        //书名
        String name = page.getHtml().xpath("//div[@id='info']/h1/text()").get();
        String author = page.getHtml().xpath("//div[@id='info']/p/text()").get();
        //作者
        author = author.substring(author.indexOf("：") + 1);
        //简介
        String about = page.getHtml().xpath("//div[@id='intro']/p/text()").get();
        //类型
        String type = page.getHtml().xpath("//div[@class='con_top']/a[2]/text()").get();
        String url = page.getRequest().getUrl();
        //序列号
        String serialNo = url.substring(url.lastIndexOf("/") + 1);
        BookConfig config = new BookConfig();
        config.setName(name);
        config.setAuthor(author);
        config.setAbout(about);
        config.setType(type);
        config.setSerialNo(serialNo);
        config.setCreateTime(new Date());
        //把结果传递给pipeline
        page.putField("config", config);
    }


    /**
     * 获得爬虫内容
     * @param page
     */
    private void getContent(Page page) {
        String url = page.getRequest().getUrl();
        String orderNum = url.substring(url.lastIndexOf("/",url.lastIndexOf("/")-1) + 1, url.lastIndexOf("."));
        //书的序列号
        String serialNo = orderNum.substring(0, orderNum.indexOf("/"));
        Long order = Long.valueOf(orderNum.substring(orderNum.indexOf("/")+1));
        //书名
        String name = page.getHtml().xpath("//div[@class='con_top']/a[3]/text()").get();
        //标题
        String title = page.getHtml().xpath("//div[@class='con_top']/text()").get().replace("> ", "");
        //内容
        List<String> all = page.getHtml().xpath("//div[@id='content']/p/text()").all();
        String content = all.stream().collect(Collectors.joining());
        BookInfo bookInfo = new BookInfo();
        bookInfo.setName(name);
        bookInfo.setSerialNo(serialNo);
        bookInfo.setTitle(title);
        bookInfo.setContent(content);
        bookInfo.setOrderNum(order);
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
