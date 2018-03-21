package com.flyhand.core.remote;


import com.flyhand.core.dto.AppConfig;
import com.flyhand.core.dto.Faq;
import com.flyhand.core.dto.OrderRecord;
import com.flyhand.core.dto.UserToKnow;
import com.flyhand.core.dto.VersionUpdate;

import java.util.ArrayList;

/**
 * User: Ryan
 * Date: 11-4-16
 * Time: Afternoon 10:06
 */
public abstract class RemoteAccess {
    //    public static String BASE_URL = "http://192.168.1.101:8080/";
    public static final String BASE_URL = "http://www.hianzuo.com/mjkf/";
    //    public static String BASE_URL = "http://192.168.3.158:8080/";
    public static final String MJKF_SERVER_VERSION = "3";
    public static final String MJKF_SERVER_KEY = "g91TpcjoD3Owg5xEjuWiTQ==";

    public static NetResult<AppConfig> loadAppConfig(String appkey) {
        return RemoteAccessImpl.loadAppConfig(appkey);
    }

    public static NetResult<UserToKnow> findUserToKnow(String appkey, int lastShowId, int lastIdShowCount) {
        return RemoteAccessImpl.findUserToKnow(appkey, lastShowId, lastIdShowCount);
    }

    public static NetResult<VersionUpdate> getLastVersionInfo(String appkey) {
        return RemoteAccessImpl.getLastVersionInfo(appkey);
    }

    public static NetResult<ArrayList<Faq>> loadFaqs(String appkey) {
        return RemoteAccessImpl.loadFaqs(appkey);
    }

    public static NetResult<Boolean> sendError(String appkey, String title, String content) {
        return RemoteAccessImpl.sendError(appkey, title, content);
    }

    public static NetResult<Boolean> sendEmail(String appkey, String title, String content, String receiver, String type) {
        return RemoteAccessImpl.sendEmail(appkey, title, content, receiver, type);
    }

    public static NetResult<Boolean> addOrderRecord(String appkey, OrderRecord or) {
        return RemoteAccessImpl.addOrderRecord(appkey, or);
    }

    public static String loadHtmlByURL(String url, String charset) {
        try {
            return RemoteAccessImpl.loadHtml(url, charset);
        } catch (Exception e) {
            return "";
        }
    }

    public static void markDonation(String appkey) {
        RemoteAccessImpl.markDonation(appkey);
    }

    public static NetResult<Boolean> validateRedeemCode(String appkey, String redeemCode) {
        return RemoteAccessImpl.validateRedeemCode(appkey, redeemCode);
    }

    public static NetResult<Boolean> checkRedeemCodeExist(String appkey) {
        return RemoteAccessImpl.checkRedeemCodeExist(appkey);
    }
}
