package com.sukaiyi.weedclient.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sukaiyi.weedclient.exception.SeaweedfsException;
import com.sukaiyi.weedclient.utils.IoUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/01/14
 */
public class SeaweedJsonResultHttpClient {

    private ObjectMapper objectMapper = new ObjectMapper();
    private CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    public <T> T get(String url, Class<T> clazz) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet, clazz);
    }

    public <T> T delete(String url, Class<T> clazz) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        return execute(httpDelete, clazz);
    }


    public <T> T postMultipart(String url, Map<String, InputStream> binaryBody, Map<String, String> textBody, Class<T> clazz) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        binaryBody = Optional.ofNullable(binaryBody).orElse(Collections.emptyMap());
        textBody = Optional.ofNullable(textBody).orElse(Collections.emptyMap());
        for (Map.Entry<String, InputStream> entry : binaryBody.entrySet()) {
            String name = entry.getKey();
            InputStream is = entry.getValue();
            builder.addBinaryBody(name, is, ContentType.DEFAULT_BINARY, name);
        }
        for (Map.Entry<String, String> entry : textBody.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                continue;
            }
            builder.addTextBody(name, value);
        }
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        return execute(httpPost, clazz);
    }

    private <T> T execute(HttpUriRequest request, Class<T> clazz) throws IOException {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            Map map = objectMapper.readValue(result, Map.class);
            if (map.get("error") != null) {
                throw new SeaweedfsException((String) map.get("error"));
            }
            return objectMapper.readValue(result, clazz);
        } finally {
            IoUtils.close(response);
        }
    }
}
