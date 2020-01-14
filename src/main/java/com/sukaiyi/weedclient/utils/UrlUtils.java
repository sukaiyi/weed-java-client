package com.sukaiyi.weedclient.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/01/14
 */
public class UrlUtils {

    private UrlUtils() {

    }

    public static String concatParam(String url, Map<String, String> param) {
        if (param == null || param.isEmpty()) {
            return url;
        }
        String baseUrl = Optional.ofNullable(url).orElse("");
        char firstChar = baseUrl.indexOf('?') >= 0 ? '&' : '?';
        StringBuilder sb = new StringBuilder(baseUrl).append(firstChar);
        for (Map.Entry<String, String> entry : param.entrySet()) {
            String key = urlEncode(entry.getKey());
            String value = urlEncode(entry.getValue());
            sb.append(key).append("=").append(value).append('&');
        }
        return sb.delete(sb.length() - 1, sb.length()).toString();
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        return str;
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        return str;
    }
}
