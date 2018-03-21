package com.flyhand.core.remote;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.config.Config;
import com.flyhand.core.dto.AppConfig;
import com.flyhand.core.dto.Faq;
import com.flyhand.core.dto.OrderRecord;
import com.flyhand.core.dto.UserToKnow;
import com.flyhand.core.dto.VersionUpdate;
import com.flyhand.core.utils.DESUtils;
import com.flyhand.core.utils.FileUtils;
import com.flyhand.core.utils.LogUtils;
import com.flyhand.core.utils.MD5Utils;
import com.flyhand.core.utils.NetworkUtils;
import com.flyhand.core.utils.StringUtil;
import com.flyhand.yunpos.http.BasicNameValuePair;
import com.flyhand.yunpos.http.NameValuePair;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: Ryan
 * Date: 11-4-16
 * Time: Afternoon 10:13
 */
public abstract class RemoteAccessImpl {

    public static String loadHtml(String url, String defaultCharset) throws Exception {
        return ConnectionUtil.requestUrl(url,defaultCharset);
    }

    public static <M> NetResult<M> dealException(Exception ex) {
        // ex.printStackTrace();
       if (ex instanceof JSONException) {
            return new NetResult<M>(60004);
        } else if (ex instanceof IOException) {
            return new NetResult<M>(60005);
        } else {
            return new NetResult<M>(-4);
        }
    }

    public static NetResult<UserToKnow> findUserToKnow(String appkey, int lastShowId, int lastIdShowCount) {
        try {
            String url = new StringBuffer(BASE_URL).append(Method.GET_USER_TO_KNOW).toString()
                    .replace("#appkey#", appkey)
                    .replace("#lastShowId#", String.valueOf(lastShowId))
                    .replace("#lastIdShowCount#", String.valueOf(lastIdShowCount));
            String result = requestPost(Method.GET_USER_TO_KNOW, url);
            JSONArray jsonArray = new JSONArray(result);
            UserToKnow userToKnow = new UserToKnow();
            int length = jsonArray.length();
            if (length > 0) {
                json2UserToKnow(jsonArray.getJSONObject(0), userToKnow);
            }
            return new NetResult<UserToKnow>(userToKnow);
        } catch (Exception e) {
            return dealException(e);
        }
    }

    private static void json2UserToKnow(JSONObject jo, UserToKnow userToKnow) throws JSONException {
        userToKnow.setAppkey(jo.getString("appkey"));
        userToKnow.setTitle(jo.getString("title"));
        userToKnow.setContent(jo.getString("content"));
        userToKnow.setBtnOneText(jo.getString("btnOneText"));
        userToKnow.setBtnOneUri(jo.getString("btnOneUri"));
        userToKnow.setBtnTwoText(jo.getString("btnTwoText"));
        userToKnow.setBtnTwoUri(jo.getString("btnTwoUri"));
        userToKnow.setBtnThreeText(jo.getString("btnThreeText"));
        userToKnow.setBtnThreeUri(jo.getString("btnThreeUri"));
        userToKnow.setShowCount(jo.getInt("showCount"));
        userToKnow.setId(jo.getInt("id"));
    }

    public static NetResult<VersionUpdate> getLastVersionInfo(String appkey) {
        try {
            String url = new StringBuffer(BASE_URL).append(Method.GET_LAST_VERSION_INFO).toString().replace("#appkey#", appkey);
            String result = requestPost(Method.GET_LAST_VERSION_INFO, url);
            JSONArray jsonArray = new JSONArray(result);
            VersionUpdate vu = new VersionUpdate();
            int length = jsonArray.length();
            if (length > 0) {
                json2VersionUpdate(jsonArray.getJSONObject(0), vu);
            }
            return new NetResult<VersionUpdate>(vu);
        } catch (Exception e) {
            return dealException(e);
        }
    }

    private static void json2VersionUpdate(JSONObject jo, VersionUpdate vu) throws JSONException {
        vu.setId(jo.getInt("id"));
        vu.setAppkey(jo.getString("appkey"));
        vu.setVersionCode(jo.getInt("versionCode"));
        vu.setVersionName(jo.getString("versionName"));
        vu.setVersionInfo(jo.getString("versionInfo"));
        vu.setDownloadUrl(jo.getString("downloadUrl"));
        if (jo.has("downloadUrlBak")) {
            vu.setDownloadUrlBak(jo.getString("downloadUrlBak"));
        }
        vu.setRequireUpdate(jo.getString("requireUpdate"));
        vu.setCount(jo.getInt("count"));
    }

    private static void json2Faq(JSONObject json, Faq faq) throws JSONException {
        faq.setId(json.getInt("id"));
        faq.setAnswer(json.getString("answer"));
        faq.setQuestion(json.getString("question"));
        faq.setSort(json.getInt("sort"));
    }

    public static NetResult<ArrayList<Faq>> loadFaqs(String appkey) {
        try {
            String url = new StringBuffer(BASE_URL).append(Method.LOAD_FAQS).toString().replace("#appkey#", appkey);
            String result = requestPost(Method.LOAD_FAQS, url);
            JSONArray jsonArray = new JSONArray(result);
            ArrayList<Faq> rss = new ArrayList<Faq>();
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                Faq faq = new Faq();
                json2Faq(jsonArray.getJSONObject(i), faq);
                rss.add(faq);
            }
            return new NetResult<ArrayList<Faq>>(rss);
        } catch (Exception e) {
            return dealException(e);
        }
    }

    public static NetResult<Boolean> sendError(String appkey, String title, String content) {
        try {
            ArrayList<NameValuePair> datas = new ArrayList<>();
            datas.add(new BasicNameValuePair("toMail", "true"));
            String subject = null == title ? "no subject" : title.trim()
                    .replace("\r\n", " ").replace("\r", " ").replace("\n", " ");
            datas.add(new BasicNameValuePair("subject", subject));
            datas.add(new BasicNameValuePair("content", content));
            datas.add(new BasicNameValuePair("send", "未发送"));
            datas.add(new BasicNameValuePair("type", "error"));
            String url = new StringBuffer(BASE_URL).append(Method.SEND_ERROR_EMAIL).toString().replace("#appkey#", appkey);
            String result = requestPost(Method.SEND_ERROR_EMAIL, url, datas);
            if (null != result && "{\"result\":\"ok\"}".equals(result.trim())) {
                return new NetResult<Boolean>(true);
            } else {
                return new NetResult<Boolean>(false);
            }
        } catch (Exception e) {
            return new NetResult<Boolean>(false);
        }
    }

    public static NetResult<Boolean> sendEmail(String appkey, String title, String content, String receiver, String type) {
        try {
            List<NameValuePair> datas = new ArrayList<>();
            datas.add(new BasicNameValuePair("toMail", "true"));
            String subject = null == title ? "no subject" : title.trim()
                    .replace("\r\n", " ").replace("\r", " ").replace("\n", " ");
            datas.add(new BasicNameValuePair("subject", subject));
            datas.add(new BasicNameValuePair("receiver", receiver));
            datas.add(new BasicNameValuePair("content", content));
            datas.add(new BasicNameValuePair("send", "未发送"));
            datas.add(new BasicNameValuePair("type", type));
            String url = new StringBuffer(BASE_URL).append(Method.SEND_ERROR_EMAIL).toString().replace("#appkey#", appkey);
            String result = requestPost(Method.SEND_ERROR_EMAIL, url, datas);
            if (null != result && "{\"result\":\"ok\"}".equals(result.trim())) {
                return new NetResult<Boolean>(true);
            } else {
                return new NetResult<Boolean>(false);
            }
        } catch (Exception e) {
            return new NetResult<Boolean>(false);
        }
    }

    public static NetResult<AppConfig> loadAppConfig(String appkey) {
        try {
            String url = new StringBuffer(BASE_URL).append(Method.LOAD_APP_CONFIG).toString().replace("#appkey#", appkey);
            String result = requestPost(Method.LOAD_APP_CONFIG, url);
            JSONArray jsonArray = new JSONArray(result);
            AppConfig ac = new AppConfig();
            json2AppConfig(jsonArray.getJSONObject(0), ac);
            return new NetResult<AppConfig>(ac);
        } catch (Exception e) {
            return dealException(e);
        }
    }

    private static void json2AppConfig(JSONObject jo, AppConfig ac) throws JSONException {
        if (jo.has("id")) {
            ac.setId(jo.getInt("id"));
        }
        if (jo.has("addTime")) {
            ac.setAddTime(jo.getString("addTime"));
        }
        if (jo.has("appkey")) {
            ac.setAppkey(jo.getString("appkey"));
        }
        if (jo.has("downloadUrl")) {
            ac.setDownloadUrl(jo.getString("downloadUrl"));
        }
        if (jo.has("email")) {
            ac.setEmail(jo.getString("email"));
        }
        if (jo.has("emailPwd")) {
            ac.setEmailPwd(jo.getString("emailPwd"));
        }
        if (jo.has("errorRecTactics")) {
            ac.setErrorRecTactics(jo.getString("errorRecTactics"));
        }
        if (jo.has("adTactics")) {
            ac.setAdTactics(jo.getString("adTactics"));
        }
        if (jo.has("emailRec")) {
            ac.setEmailRec(jo.getString("emailRec"));
        }
        if (jo.has("iconUrl")) {
            ac.setIconUrl(jo.getString("iconUrl"));
        }
        if (jo.has("intro")) {
            ac.setIntro(jo.getString("intro"));
        }
        if (jo.has("appExpand")) {
            ac.setAppExpand(jo.getString("appExpand"));
        }
        if (jo.has("appNote")) {
            ac.setAppNote(jo.getString("appNote"));
        }
        if (jo.has("name")) {
            ac.setName(jo.getString("name"));
        }
        if (jo.has("state")) {
            ac.setState(jo.getString("state"));
        }
        if (jo.has("type")) {
            ac.setType(jo.getString("type"));
        }
        if (jo.has("updateInfo")) {
            ac.setUpdateInfo(jo.getString("updateInfo"));
        }
        if (jo.has("userId")) {
            ac.setUserId(jo.getInt("userId"));
        }
        if (jo.has("versionCode")) {
            ac.setVersionCode(jo.getInt("versionCode"));
        }
        if (jo.has("versionName")) {
            ac.setVersionName(jo.getString("versionName"));
        }
    }

    public static void markDonation(String appkey) {
        try {
            String url = new StringBuffer(BASE_URL).append(Method.MARK_DONATION).toString().replace("#appkey#", appkey);
            requestPost(Method.MARK_DONATION, url);
        } catch (Exception e) {
            //return new HttpResult<AppConfig>(0);
        }
    }

    public static NetResult<Boolean> addOrderRecord(String appkey, OrderRecord or) {
        try {
             List<NameValuePair> datas = new ArrayList<>();
            datas.add(new BasicNameValuePair("appkey", appkey));
            datas.add(new BasicNameValuePair("appVCode", String.valueOf(Config.VERSION_CODE)));
            datas.add(new BasicNameValuePair("productName", or.productName));
            datas.add(new BasicNameValuePair("user", Config.Device_ID));
            datas.add(new BasicNameValuePair("mobileCode", Config.Device_ID));
            datas.add(new BasicNameValuePair("price", String.valueOf(or.price)));
            datas.add(new BasicNameValuePair("count", String.valueOf(or.count)));
            datas.add(new BasicNameValuePair("qq", or.qq));
            datas.add(new BasicNameValuePair("email", or.email));
            datas.add(new BasicNameValuePair("address", or.address));
            datas.add(new BasicNameValuePair("orderTime", ""));
            datas.add(new BasicNameValuePair("dealTime", ""));
            datas.add(new BasicNameValuePair("userNote", or.userNote));
            datas.add(new BasicNameValuePair("orderStatus", or.orderStatus));
            String url = new StringBuffer(BASE_URL).append(Method.ADD_ORDER_RECORD).toString();
            String result = request(Method.ADD_ORDER_RECORD, url, datas);
            if (null != result && "{\"result\":\"ok\"}".equals(result.trim())) {
                return new NetResult<Boolean>(true);
            } else {
                return new NetResult<Boolean>(false);
            }
        } catch (Exception e) {
            return new NetResult<Boolean>(false);
        }
    }

    public static NetResult<Boolean> validateRedeemCode(String appkey, String redeemCode) {
        try {
            List<NameValuePair> datas = new ArrayList<>();
            datas.add(new BasicNameValuePair("appkey", appkey));
            datas.add(new BasicNameValuePair("appVCode", String.valueOf(Config.VERSION_CODE)));
            datas.add(new BasicNameValuePair("mobileCode", Config.Device_ID));
            datas.add(new BasicNameValuePair("redeemCode", redeemCode));
            String url = BASE_URL + Method.VALIDATE_REDEEM_CODE;
            String result = request(Method.VALIDATE_REDEEM_CODE, url, datas);
            if (null != result && "{\"result\":\"ok\"}".equals(result.trim())) {
                return new NetResult<Boolean>(true);
            } else {
                JSONObject jo = new JSONObject(result);
                int errCode = jo.getInt("errCode");
                String errMsg = jo.getString("errMsg");
                return new NetResult<Boolean>(errMsg, errCode);
            }
        } catch (Exception e) {
            return new NetResult<Boolean>(false);
        }
    }

    public static NetResult<Boolean> checkRedeemCodeExist(String appkey) {
        try {
            List<NameValuePair> datas = new ArrayList<>();
            datas.add(new BasicNameValuePair("appkey", appkey));
            datas.add(new BasicNameValuePair("appVCode", String.valueOf(Config.VERSION_CODE)));
            datas.add(new BasicNameValuePair("mobileCode", Config.Device_ID));
            String url = BASE_URL + Method.CHECK_REDEEM_CODE_EXIST;
            String result = request(Method.CHECK_REDEEM_CODE_EXIST, url, datas);
            if (null != result && "{\"result\":\"true\"}".equals(result.trim())) {
                return new NetResult<Boolean>(true);
            } else {
                return new NetResult<Boolean>(false);
            }
        } catch (Exception e) {
            return new NetResult<Boolean>(e.getMessage(), -1);
        }
    }


    static class Method {
        public String name = "";
        public CacheConfig cacheConfig;
        public static final Method GET_USER_TO_KNOW = new Method("/admin/findUserToKnowByWherePageJson.action?appkey=#appkey#&pageSize=1&order.id=1&desc=false&lastShowId=#lastShowId#&lastIdShowCount=#lastIdShowCount#&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID);
        public static final Method FIND_RECOMMEND_SOFT = new Method("/admin/findRecommendSoftByWherePageJson.action?appkey=#appkey#&pageSize=200&order.sort=1&desc=true&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID
                , new CacheConfig(true, 1000 * 60 * 60 * 24 * 2));
        public static final Method GET_LAST_VERSION_INFO = new Method("/admin/findVersionUpdateByWherePageJson.action?appkey=#appkey#&pageSize=1&order.versionCode=1&desc=true&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID
                , new CacheConfig(true, 1000 * 60 * 60 * 24 * 2));
        public static final Method LOAD_FAQS = new Method("/admin/findFaqByWherePageJson.action?appkey=#appkey#&pageSize=200&order.sort=1&desc=true&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID
                , new CacheConfig(true, 1000 * 60 * 60 * 24 * 2));
        public static final Method SEND_ERROR_EMAIL = new Method("/admin/saveEmailJson.action?appkey=#appkey#&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID);
        public static final Method LOAD_APP_CONFIG = new Method("/admin/findAppConfigByWherePageJson.action?appkey=#appkey#&pageSize=1&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID
                , new CacheConfig(false, 1000 * 60 * 60 * 24 * 2));
        public static final Method MARK_DONATION = new Method("/admin/mark_donation.jsp?appkey=#appkey#&vc=" + Config.VERSION_CODE + "&device_id=" + Config.Device_ID);
        public static final Method ADD_ORDER_RECORD = new Method("/admin/saveOrderRecord.action");
        public static final Method VALIDATE_REDEEM_CODE = new Method("/admin/validateRedeemCode.action");
        public static final Method CHECK_REDEEM_CODE_EXIST = new Method("/admin/checkRedeemCodeExist.action");

        public Method(String name) {
            this.name = name;
            this.cacheConfig = new CacheConfig();
        }

        public Method(String name, CacheConfig cacheConfig) {
            this.name = name;
            this.cacheConfig = cacheConfig;
        }

        @Override
        public String toString() {
            return name;
        }

        public static class CacheConfig {
            public boolean cache = false;
            public long time = -1;

            public CacheConfig() {
            }

            public CacheConfig(boolean cache, long time) {
                this.cache = cache;
                this.time = time;
            }
        }
    }
    private static String requestPost(Method method, String url, List<NameValuePair> datas) throws Exception {
        String reqUrl;
        if (url.contains("?")) {
            reqUrl = url.substring(0, url.indexOf("?"));
            String paramsUrl = url.substring(url.indexOf("?") + 1);
            if (paramsUrl.contains("&")) {
                String[] dd = paramsUrl.split("&");
                for (String s : dd) {
                    if (s.contains("=")) {
                        String[] ss = s.split("=");
                        if (ss.length == 2) {
                            datas.add(new BasicNameValuePair(ss[0].trim(), ss[1].trim()));
                        }
                    }
                }
            }
        } else {
            reqUrl = url;
        }
        return request(method, reqUrl, datas);
    }

    private static String requestPost(Method method, String url) throws Exception {
        return requestPost(method, url, new ArrayList<NameValuePair>());
    }

    private static String request(Method method, String reqUrl, List<NameValuePair> data) throws Exception {
        String result = _request(method, reqUrl, data);
        try {
            return DESUtils.decryptDES(result.trim(), getKey());
        } catch (Exception e) {
            return result;
        }
    }

    static String key = null;

    private static String getKey() {
        try {
            if (null == key) {
                key = DESUtils.decryptDES("g91TpcjoD3Owg5xEjuWiTQ==", "79832683");
            }
            return key;
        } catch (Exception e) {
            return "";
        }
    }

    private static String _request(Method method, String reqUrl, List<NameValuePair> data) throws Exception {
        String result;
        LogUtils.log("HttpAccess ReqUrl : " + reqUrl);
        RemoteAccessImpl.kSort(data);
        boolean isAvailable = NetworkUtils.isAvailable(AbstractCoreApplication.get());
        if (!isAvailable && Config.Use_Cache) {
            result = requestFromCache(method, data);
            if (StringUtil.isNotEmpty(result)) {
                return result;
            }
        }
        if (isAvailable) {
            HttpResponse resp = requestFromNetwork(reqUrl, data, method.cacheConfig.cache);
            int code = resp.getStatusLine().getStatusCode();
            if (code == 200) {
                return _dealNetworkResponse(method, data, resp);
            } else if (code == 304) {
                //服务器没有改变内容，继续使用缓存
                result = requestFromCache(method, data);
                if (StringUtil.isNotEmpty(result)) {
                    return result;
                } else {
                    //没有读取到缓存，需要强制请求
                    HttpResponse resp1 = requestFromNetwork(reqUrl, data, false);
                    int code1 = resp1.getStatusLine().getStatusCode();
                    if (code1 == 200) {
                        return _dealNetworkResponse(method, data, resp1);
                    } else {
                        throw new RuntimeException("the server has error. please try again latter");
                    }
                }
            } else {
                throw new RuntimeException("the server has error. please try again latter");
            }
        } else {
            //网络不可用，强制从缓存读取
            result = requestFromCache(method, data);
            if (StringUtil.isNotEmpty(result)) {
                markCacheAvailable(method, data);
                return result;
            } else {
                throw new UnknownHostException("the network is not available.");
            }
        }

    }

    private static HttpResponse requestFromNetwork(String reqUrl, List<NameValuePair> data, boolean cache) throws Exception {
        BasicNameValuePair sv = new BasicNameValuePair("sv", RemoteAccess.MJKF_SERVER_VERSION);
        BasicNameValuePair useCache = new BasicNameValuePair("cache", String.valueOf(cache));
        data.add(sv);
        data.add(useCache);
        BasicNameValuePair sig = new BasicNameValuePair("sig", GetSig(data));
        data.add(sig);
        List<NameValuePair> list = new ArrayList<>();
        for (NameValuePair pair : data) {
            list.add(pair);
        }
        String responseBody = ConnectionUtil.request(reqUrl, new ConnectionUtil.RequestConfig().setTryCount(1), list);
        data.remove(sv);
        data.remove(useCache);
        data.remove(sig);
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.setEntity(new StringEntity(responseBody));
        return response;
    }

    private static String GetSig(List<NameValuePair> data) throws UnsupportedEncodingException {
        kSort(data);
        StringBuilder result = new StringBuilder();
        for (NameValuePair nvp : data) {
            result.append(nvp.getName()).append("=").append(nvp.getValue());
        }
        return MD5Utils.MD5(URLEncoder.encode(result.toString() + "#" + RemoteAccess.MJKF_SERVER_KEY, UTF8));
    }

    public static void kSort(List<NameValuePair> list) {
        Collections.sort(list, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (null == o1 || null == o2) {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private static String _dealNetworkResponse(Method method, List<NameValuePair> data, HttpResponse resp) throws IOException {
        String result;
        result = EntityUtils.toString(resp.getEntity());
        LogUtils.log("HttpAccess Result: " + result);
        if (null != result && !"".equals(result.trim())) {
            putResultToCache(method, data, result);
        }
        return result;
    }

    private static String requestFromCache(Method method, List<NameValuePair> data) {
        Method.CacheConfig mcc = method.cacheConfig;
        if (!mcc.cache) {
            return null;
        } else {
            try {
                String key = MD5Utils.MD5(method + "_" + data);
                File cacheFile = getNetworkCacheFile(key);
                if (cacheFile.exists()) {
                    String result = FileUtils.readFileToString(cacheFile, UTF8);
                    LogUtils.log("HttpAccess Request [requestFromCache]: " + data);
                    long lastModified = cacheFile.lastModified();
                    long ct = System.currentTimeMillis();
                    if ((ct - lastModified) > mcc.time) {
                        synchronized (modifyLock) {
                            cacheFile.delete();
                        }
                    }
                    return result;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static boolean markCacheAvailable(Method method, List<NameValuePair> data) {
        Method.CacheConfig mcc = method.cacheConfig;
        if (!mcc.cache) {
            return false;
        } else {
            try {
                String key = MD5Utils.MD5(method + "_" + data);
                File cacheFile = getNetworkCacheFile(key);
                if (cacheFile.exists()) {
                    synchronized (modifyLock) {
                        cacheFile.setLastModified(System.currentTimeMillis());
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static boolean putResultToCache(Method method, List<NameValuePair> data, String result) {
        Method.CacheConfig mcc = method.cacheConfig;
        if (mcc.cache) {
            String key = MD5Utils.MD5(method + "_" + data);
            File cacheFile = getNetworkCacheFile(key);
            synchronized (modifyLock) {
                try {
                    cacheFile.delete();
                    FileUtils.write(cacheFile, result, UTF8);
                    cacheFile.setLastModified(System.currentTimeMillis());
                    return true;
                } catch (Exception e) {
                    cacheFile.delete();
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    private static File getNetworkCacheFile(String key) {
        File necDir = new File("/sdcard/.mjkf/");
        File file = new File(necDir, "httpcache");
        file.mkdirs();
        return new File(file, key);
    }

    private static final int HTTP_TIMEOUT = 30000;
    private static final String BASE_URL = RemoteAccess.BASE_URL;
    private static final String UTF8 = "utf-8";
    private static final Object modifyLock = new Object();
}
