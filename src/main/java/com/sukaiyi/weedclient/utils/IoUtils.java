package com.sukaiyi.weedclient.utils;

import java.io.Closeable;

/**
 * @author sukaiyi
 * @date 2020/01/14
 */
public class IoUtils {
    private IoUtils() {

    }

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }
}
