package com.vbook.core.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SignAwsUtils
 */
@Slf4j
public class SignAwsUtils {

    public static boolean isValid(String sign, String requestId, String timestamp,
                                  String uri, String secret, String params) throws UnsupportedEncodingException {
        String checkSign = generateSign(requestId, timestamp, uri, secret, params);
        if (log.isDebugEnabled()) {
            log.debug("awsSign {} checkedWith {} and params {}", sign, checkSign, params);
        }

        boolean isValid = StringUtils.equals(sign, checkSign);
        if (!isValid) {
            log.error("sign:{}, checkSign:{}, requestId:{}, timestamp:{}, uri:{}, secret:{}, params:{}", sign, checkSign, requestId, timestamp, uri, secret, params);
            log.error("=========================> 签名校验错误 sign error!!!");
        }
        return isValid;
    }


    public static String generateSign(String accessKey, String requestTime,
                                      String uri, String secretKey, String params) {
        //第一步根据时间戳做一次加密
        String stringBuffer = accessKey + "|" + requestTime + "|" + uri;
        HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, stringBuffer);
        byte[] kDate = hmac.hmac(secretKey);

        //原始基数
        byte[] messageType = DigestUtils.sha256(params);

        //加密后的secretKey再对数据源做一层加密
        hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, kDate);
        return hmac.hmacHex(messageType);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String requestTime = "1661491851216";
        String secretKey = "70419565cdd697b03a230f4bbd2e5a50a6f48c4f557c35ca0bd9f09c6b1071f7";
        String requestId = "ffa-90d6-c99e21d0c4cc";
        JSONObject bodys = new JSONObject();
        bodys.put("platformId", "792218184714614");
//        bodys.put("AccessKey", "43425f03-2f7");
//        bodys.put("SecretKey", "2436D9FFFA4CEF6685AD2");
        bodys.put("branchId", "546991277420822528");
        bodys.put("platformName", "qqqq3");
        bodys.put("platformStatus", 2);
        String params = JSONObject.toJSONString(bodys);
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "792218184714614", "156146338306991", "919676803251704", "357880158308439", "899360753837360", "930047078093708");
//        bodys.put("platformIdList", list);
        params = JSONObject.toJSONString(bodys);
        params = "";
        System.out.println("requestTime = " + requestTime);
        System.out.println(params);
//        String uri = "http://120.27.135.107:40010/south/platforms/overview";
//        String uri = "http://120.27.135.107:40010/south/platforms";
//        String uri = "http://120.27.135.107:40010/south/platforms/edit";
        String uri = "http://120.27.135.107:40010/south/platforms/del/919676803251704";

        String checkSign = generateSign(requestId, requestTime, uri, secretKey, params);
        System.out.println(checkSign);

//        String requestId = UUID.randomUUID().toString().substring(15);
//        System.out.println("requestId = " + requestId);
//        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, requestId);
//        String secretKey = hmacUtils.hmacHex(requestId);
//        System.out.println("secretKey = " + secretKey);
    }
}
