package com.flyhand.content;

/**
 * Created by Administrator on 2015/5/30.
 */
public class IntentJsonException extends RuntimeException {
    public IntentJsonException() {
        super();
    }

    public IntentJsonException(String detailMessage) {
        super(detailMessage);
    }

    public IntentJsonException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IntentJsonException(Throwable throwable) {
        super(throwable);
    }
}
