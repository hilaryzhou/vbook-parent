package com.vbook.reptile;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .addCookie("Cookie", "Hm_lvt_007bc30c1abb0ffb7a93b4f3c8e10c5e=1662214067,1662379937,1662386148; __gads=ID=85a40a74607d8c35-2263bd6e34d6004c:T=1662214299:RT=1662214299:S=ALNI_MYzZ7ln0qdiCoW958mQwuHHxJldsw; __gpi=UID=0000096dcb4c80d9:T=1662214299:RT=1662379967:S=ALNI_MbRsUd5fcb3hwgqsfv4jpTgC3xQxg; bcolor=%23E9FAFF; txtcolor=; fonttype=24px; scrollspeed=5; Hm_lpvt_007bc30c1abb0ffb7a93b4f3c8e10c5e=1662386152")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:104.0) Gecko/20100101 Firefox/104.0");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        page.putField("author", page.getHtml().xpath("//div[@class='leftBox border']/div[@class='uplist']/div[@id='tlist']/ul/li/a/test()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

//        Spider.create(new GithubRepoPageProcessor())
//                //从"https://github.com/code4craft"开始抓
//                .addUrl("https://github.com/code4craft")
//                //开启5个线程抓取
//                .thread(5)
//                //启动爬虫
//                .run();
        Spider spider = Spider.create(new GithubRepoPageProcessor())
                .addUrl("https://www.bbiquge.net/fenlei/1_1/")
//                .setDownloader(new PhantomJSDownloader())
                .addPipeline(new ConsolePipeline());
//                .get("https://www.bbiquge.net/fenlei/1_1/");
        Object result = spider.get("https://www.bbiquge.net/fenlei/1_1/");
        System.out.println("result" + result);
    }
}