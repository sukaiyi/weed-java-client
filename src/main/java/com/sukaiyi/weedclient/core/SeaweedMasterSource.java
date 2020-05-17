package com.sukaiyi.weedclient.core;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;
import com.sukaiyi.weedclient.exception.SeaweedfsException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author sukaiyi
 * @date 2020/05/15
 */
public class SeaweedMasterSource {

    private static final Log log = LogFactory.get();

    private static final ConcurrentHashMap<String, Integer> IN_ACTIVE = new ConcurrentHashMap<>(8);

    private static final Consumer<List<String>> IN_ACTIVE_UPDATE_JOB = urls -> {
        for (String url : urls) {
            try {
                HttpUtil.get(url + WeedRestEndpoint.DIR_STATUS, 3000);
                if (IN_ACTIVE.containsKey(url)) {
                    IN_ACTIVE.remove(url);
                    log.info("seaweedfs node: {} is now available", url);
                }
            } catch (Exception ignore) {
                IN_ACTIVE.put(url, 0);
                log.log(Level.ERROR, "seaweedfs node: {} is not available now", url);
            }
        }
    };

    private final List<String> urls;

    public SeaweedMasterSource(List<String> urls) {
        Objects.requireNonNull(urls);
        this.urls = urls;
        IN_ACTIVE_UPDATE_JOB.accept(urls);
        new InActiveUpdateThread(urls).start();
    }

    public String choose() {
        if (this.urls.isEmpty() || this.urls.size() <= IN_ACTIVE.size()) {
            throw new SeaweedfsException("no active master available");
        }
        for (String url : urls) {
            if (IN_ACTIVE.containsKey(url)) {
                continue;
            }
            return url;
        }
        throw new SeaweedfsException("no active master available");
    }

    private static final class InActiveUpdateThread extends Thread {
        private final List<String> urls;

        public InActiveUpdateThread(List<String> urls) {
            this.urls = urls;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    throw ExceptionUtil.wrap(e, SeaweedfsException.class);
                }
                IN_ACTIVE_UPDATE_JOB.accept(this.urls);
            }
        }
    }
}
