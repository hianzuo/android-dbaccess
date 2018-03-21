package com.flyhand.core.ex;

/**
 * Created by Ryan
 * On 2016/6/12.
 */
public class GsonParseException extends RuntimeException{
    public GsonParseException(String json, Throwable target) {
        super(json, target);
    }

    public GsonParseException(String detailMessage) {
        super(detailMessage);
    }
}
