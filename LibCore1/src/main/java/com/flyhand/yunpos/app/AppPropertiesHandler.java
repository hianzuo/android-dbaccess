package com.flyhand.yunpos.app;

import com.flyhand.core.app.AbstractCoreApplication;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-6-7
 * Time: 上午10:09
 */
public class AppPropertiesHandler {
    private AbstractCoreApplication mExApplication;
    private HashMap<String, String> properties;

    public AppPropertiesHandler(AbstractCoreApplication application) {
        this.mExApplication = application;
        properties = new HashMap<String, String>() {{
            put("channel_recommend_soft", "com.flyhand.jrpy.channel.RECOMMEND_SOFT");
            put("channel_about", "com.flyhand.jrpy.channel.ABOUT");
            put("channel_exit_app", "com.flyhand.jrpy.channel.EXIT_APP");
            put("channel_logout_app", "com.flyhand.jrpy.channel.LOGOUT_APP");
            put("work_group_name", "迅手科技集团");
            put("work_group_url", "Contact:<a href=\"mailto:flayhand@qq.com\">flayhand@qq.com</a>");
            put("image_loader_path", "pic");
            put("mjkf_about_slogan", "");
            put("everything_you_want_write_here", "");
            put("image_loader_expired_time", "0x19bfcc00");
            put("remote_access_cache_dir", "network_cache");
            put("remote_access_use_cache", "false");
            put("config_dev", "false");
            put("proguard", "false");
        }};
    }

    String get(String key) {
        return properties.get(key);
    }
}
