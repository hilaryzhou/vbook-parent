package com.vbook.reptile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.vbook.core.utils.EmptyUtil;
import com.vbook.model.reptile.BookConfig;
import com.vbook.reptile.mapper.BookConfigMapper;
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
    private BookConfigMapper mapper;

    @Override
    @Transactional
    public void process(ResultItems resultItems, Task task) {
        BookConfig config = resultItems.get("config");
        if (EmptyUtil.isNullOrEmpty(config)) {
            return;
        }
        LambdaQueryWrapper<BookConfig> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BookConfig::getName, config.getName())
                .eq(BookConfig::getSerialNo, config.getSerialNo());
        boolean flag = mapper.exists(wrapper);

        //修改
        if (flag) {
            LambdaUpdateWrapper<BookConfig> update = new LambdaUpdateWrapper<>();
            update.eq(BookConfig::getName, config.getName())
                    .eq(BookConfig::getSerialNo, config.getSerialNo());
            config.setUpdateTime(new Date());
            mapper.update(config, update);
        }
        //插入
        mapper.insert(config);
    }
}
