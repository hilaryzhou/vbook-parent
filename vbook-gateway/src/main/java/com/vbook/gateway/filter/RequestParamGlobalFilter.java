package com.vbook.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.vbook.core.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.vbook.core.common.HttpConstants.*;

/**
 * @Description: 读取request请求中的body内容，重写getBody方法，并把包装后的请求放到过滤器链中传递下去。
 * @author: zhouhuan
 * @Date: 2022/8/24-10:39
 */
@Component
@Slf4j
@Order(-1)
public class RequestParamGlobalFilter implements GlobalFilter {

    private static final String DEFAULT_ERROR = "/error";
    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        MediaType contentType = headers.getContentType();
        if (contentType == null) {
            return chain.filter(exchange);
        }
        String uri = String.valueOf(request.getURI());
        if (!DEFAULT_ERROR.equals(uri)) {
            String method = String.valueOf(request.getMethod());
            Map<String, String> parameterMap = request.getQueryParams().toSingleValueMap();
            String sign = headers.getFirst(HTTP_SIGN_HEADER_ACCEPT_SIGN);
            String accessKey = headers.getFirst(HTTP_SIGN_HEADER_ACCEPT_ACCESS_KEY);
            String requestTime = headers.getFirst(HTTP_SIGN_HEADER_ACCEPT_TIME);
            String ip = IpUtils.getIpAddr(request);
            log.info(
                    "Before request ip:{}, uri:{}, method:{}; header[contentType:{} accessKey:{} requestTime:{} sign:{}]; parameter:{}",
                    ip, uri, method, contentType, accessKey, requestTime, sign,
                    JSON.toJSONString(parameterMap));
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        DataBufferUtils.retain(dataBuffer);
                        Flux<DataBuffer> cachedFlux = Flux
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }

                        };
                        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, cachedFlux);

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }
        return writeMessage(exchange, DEFAULT_ERROR);
    }

    /**
     * 返回错误提示信息
     *
     * @return
     */
    private Mono<Void> writeMessage(ServerWebExchange exchange, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("msg", message);
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //设置状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //设置返回类型
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //设置返回数据
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONBytes(map));
        //响应数据回浏览器
        return response.writeWith(Flux.just(buffer));
    }
}
