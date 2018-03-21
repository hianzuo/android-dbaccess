package com.flyhand.core;

/**
 * Created by Ryan
 * On 2016/4/23.
 */
public class LockObj {
    private boolean isWaiting = false;

    public LockObj() {
        super();
    }

    public synchronized boolean waitMillis(int millis) {
        if (millis > 0) {
            try {
                isWaiting = true;
                wait((long) millis);
                return true;
            } catch (Exception ignored) {
                return false;
            } finally {
                isWaiting = false;
            }
        } else {
            return true;
        }
    }

    public synchronized boolean sleep(int millis) {
        if (millis > 0) {
            try {
                isWaiting = true;
                Thread.sleep(millis);
                return true;
            } catch (Exception ignored) {
                return false;
            } finally {
                isWaiting = false;
            }
        } else {
            return true;
        }
    }

    public synchronized boolean waiting() {
        return isWaiting;
    }
}
