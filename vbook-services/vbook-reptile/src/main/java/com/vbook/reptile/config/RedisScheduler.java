package com.vbook.reptile.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.Arrays;

@Component
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {


    private RedisTemplate redisTemplate;
    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";


    public RedisScheduler(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        redisTemplate.delete(getSetKey(task));
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return redisTemplate.opsForSet().add(getSetKey(task), "\"" + request.getUrl() + "\"") == 0;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        redisTemplate.opsForList().leftPush(getQueueKey(task), request.getUrl());
        if (request.getExtras() != null) {
            String field = DigestUtils.shaHex(request.getUrl());
            String value = JSON.toJSONString(request);
            redisTemplate.opsForHash().put((ITEM_PREFIX + task.getUUID().getBytes()), field.getBytes(), value);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        String url = (String) redisTemplate.opsForList().leftPop(getQueueKey(task));
        if (url == null) {
            return null;
        }
        String key = Arrays.toString((ITEM_PREFIX + task.getUUID()).getBytes());
        String field = Arrays.toString(DigestUtils.sha1Hex(url).getBytes());
        String bytes = (String) redisTemplate.opsForHash().get(key, field);
        if (bytes != null) {
            Request o = JSON.parseObject(bytes, Request.class);
            return o;
        }
        Request request = new Request(url);
        return request;
    }

    protected String getSetKey(Task task) {
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    protected String getItemKey(Task task) {
        return ITEM_PREFIX + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Long size = redisTemplate.opsForList().size(getQueueKey(task));
        return size.intValue();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Long size = redisTemplate.opsForSet().size(getSetKey(task));
        return size.intValue();
    }


}