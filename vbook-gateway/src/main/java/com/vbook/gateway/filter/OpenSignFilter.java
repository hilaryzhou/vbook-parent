package com.vbook.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vbook.core.utils.SignAwsUtils;
import com.vbook.exception.CustException;
import com.vbook.gateway.config.RequestConfig;
import com.vbook.gateway.mapper.SsoKeyMapper;
import com.vbook.model.auth.SsoKey;
import com.vbook.model.common.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.vbook.core.common.HttpConstants.*;


@Slf4j
@Order(2)
@Component
public class OpenSignFilter implements GlobalFilter {
    @Autowired
    RequestConfig requestConfig;

    private Integer signDuration;
    @Autowired
    SsoKeyMapper ssoKeyMapper;
    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    private boolean checkHeaders(String sign, String timestamp) {
        return StringUtils.isNotBlank(sign) && StringUtils.isNotBlank(timestamp);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = null;
        try {
            request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            String sign = headers.getFirst(HTTP_SIGN_HEADER_ACCEPT_SIGN);
            String timestamp = headers.getFirst(HTTP_SIGN_HEADER_ACCEPT_TIME);
            String accessKey = headers.getFirst(HTTP_SIGN_HEADER_ACCEPT_ACCESS_KEY);
            String uri = String.valueOf(request.getURI());
            uri = URLDecoder.decode(uri, "UTF-8");
            Boolean canSkipSign = requestConfig.getSkipSign();
            //获取对应项目密钥
            SsoKey ssoKey = ssoKeyMapper.selectOne(Wrappers.lambdaQuery(SsoKey.class).eq(SsoKey::getAccessKey, accessKey));
            String secret = ssoKey.getSecret();

            if (null != canSkipSign && requestConfig.getSkipSign()
                    && StringUtils.isNotBlank(sign)) {
                return chain.filter(exchange);
            }
            if (!checkHeaders(sign, timestamp)) {
                log.error("请求校验参数不能为空 uri={}", request.getRemoteAddress());
                return writeMessage(exchange, HttpCodeEnum.REQUEST_PARAM_MISS.getErrorMessage());
            }
            //判断sign是否过期
            if (!Objects.isNull(signDuration) && signDuration != -1) {
                //兼容10位requestTime和13位requestTime
                if (System.currentTimeMillis() - Long.parseLong(timestamp.length() == 10 ? timestamp + "000" : timestamp) > signDuration) {
                    return writeMessage(exchange, HttpCodeEnum.SIGN_TIME_EXPIRE.getErrorMessage());
                }
            }
            String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
            String body = null;
            MediaType mediaType =
                    StringUtils.isBlank(contentType) ? null : MediaType.parseMediaType(contentType);
            if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(mediaType)) {
                //获取body参数
                Flux<DataBuffer> cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
                if (cachedBody != null) {
                    body = resolveBodyFromRequest(cachedBody);
                    log.info("request body is:{}", body);
                } else {
                    Map<String, String> parameterMap = request.getQueryParams().toSingleValueMap();
                    List<String> paramKeys = Arrays.stream(parameterMap.keySet()
                                    .toArray(new String[]{})).sorted(Comparator.naturalOrder())
                            .collect(Collectors.toList());
                    body = paramKeys.stream()
                            .map(key -> String.join("=", key, parameterMap.keySet().stream().findFirst().get()))
                            .collect(Collectors.joining("&")).trim();
                }
            }
            body = JSONObject.parseObject(body, String.class);
            if (Objects.isNull(body) || "{}".equals(body)) {
                body = "";
            }
            boolean valid = SignAwsUtils
                    .isValid(sign, accessKey, timestamp, uri, secret, body);
            if (!valid) {
                log.error("请求校验签名失败,请重试！");
                return writeMessage(exchange, HttpCodeEnum.SIGN_ERROR.getErrorMessage());
            }
        } catch (UnsupportedEncodingException e) {
            CustException.cust(HttpCodeEnum.SIGN_ERROR);
        }
        ServerHttpRequest.Builder builder = request.mutate();
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }


    /**
     * 用于获取请求参数
     *
     * @param body
     * @return
     */
    private static String resolveBodyFromRequest(Flux<DataBuffer> body) {
        AtomicReference<String> rawRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            rawRef.set(Strings.fromUTF8ByteArray(bytes));
        });
        return rawRef.get();
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
