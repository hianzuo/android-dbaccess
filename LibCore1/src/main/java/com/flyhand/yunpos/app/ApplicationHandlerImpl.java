package com.flyhand.yunpos.app;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.app.ApplicationHandler;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-6-7
 * Time: 上午9:40
 */
public class ApplicationHandlerImpl extends ApplicationHandler {
    private final HashMap<String, Object> forwardParams
            = new HashMap<String, Object>();
    private AppPropertiesHandler mAppPropertiesHandler;

    @Override
    public void onCreate(AbstractCoreApplication application) {
        mAppPropertiesHandler = new AppPropertiesHandler(application);
    }

    @Override
    public String getApplicationResource(String key) {
        return mAppPropertiesHandler.get(key);
    }

    @Override
    public void putForwardParams(String key, Object param) {
        synchronized (forwardParams) {
            if (forwardParams.containsKey(key)) {
                //throw new RuntimeException("the key[" + key + "] already exist");
                forwardParams.remove(key);
            }
            forwardParams.put(key, param);
        }
    }

    @Override
    public Object takeForwardParams(String key) {
        synchronized (forwardParams) {
            Object obj = forwardParams.get(key);
            if (null != obj) {
                forwardParams.remove(key);
            }
            return obj;
        }
    }
}
