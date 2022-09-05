package com.vbook.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author: zhouhuan
 * @date: 2022-09-06 00:02
 * @description: 全局拦截器，作用所有的微服务 1. 对请求的API调用过滤，记录接口的请求时间，方便日志审计、告警、分析等运维操作
 **/
@Slf4j
@Component
@Order(1)
public class ApiLoggingFilter implements GlobalFilter {
    private static final String START_TIME = "startTime";
    // nginx需要配置
    private static final String X_REAL_IP = "X-Real-IP";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (log.isDebugEnabled()) {
            String info = String.format("Method:{%s} Host:{%s} Path:{%s} Query:{%s}",
                    exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getHost(),
                    exchange.getRequest().getURI().getPath(), exchange.getRequest().getQueryParams());
            log.debug(info);
        }
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                Long executeTime = (System.currentTimeMillis() - startTime);
                List<String> ips = exchange.getRequest().getHeaders().get(X_REAL_IP);
                String ip = ips != null ? ips.get(0) : null;
                String api = exchange.getRequest().getURI().getRawPath();
                int code = 500;
                if (exchange.getResponse().getStatusCode() != null) {
                    code = exchange.getResponse().getStatusCode().value();
                }
                // 当前仅记录日志，后续可以添加日志队列，来过滤请求慢的接口
                if (log.isDebugEnabled()) {
                    log.debug("来自IP地址：{}的请求接口：{}，响应状态码：{}，请求耗时：{}ms", ip, api, code, executeTime);
                }
            }
        }));
    }
}
