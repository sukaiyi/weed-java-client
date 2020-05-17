package com.sukaiyi.weedclient.exception;

/**
 * @author sukaiyi
 * @date 2020/01/14
 */
public class SeaweedfsException extends RuntimeException {

    public SeaweedfsException(Exception e) {
        super(e);
    }

    public SeaweedfsException(String message) {
        super(message);
    }
}
