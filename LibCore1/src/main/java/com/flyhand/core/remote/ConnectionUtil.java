package com.flyhand.core.remote;

import com.flyhand.core.apphelper.AppHelper;
import com.flyhand.core.utils.IOUtils;
import com.flyhand.yunpos.http.HttpURLConnectionHelper;
import com.flyhand.yunpos.http.NameValuePair;
import com.hianzuo.logger.Log;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryan
 * On 14/11/30.
 */
public class ConnectionUtil {
    private static final String TAG = ConnectionUtil.class.getSimpleName();
    private static final String UTF8 = "utf-8";

    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            try {
                result.append(URLEncoder.encode(pair.getName(), UTF8));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), UTF8));
            } catch (Exception e) {
                Log.e(TAG, "getQuery for(name:" + pair.getName() + ",value:" + pair.getValue() + ") error.",e);
                throw e;
            }
        }
        return result.toString();
    }

    public static String request(String requestUrl, RequestConfig config, List<NameValuePair> data) throws Exception {
        return ___request_1(requestUrl, config, data, config.tryCount);
    }

    private static String ___request_1(String requestUrl, RequestConfig config, List<NameValuePair> data, int tryCount) throws Exception {
        try {
            boolean isLANRequest = isLANRequest(requestUrl);
            if (AppHelper.canAccessInternet()   //可以访问外网， 任何请求都可以
                            || isLANRequest && AppHelper.networkAvailable() //局域网请求，需网络可用
                    ) {
                return ___request_2(requestUrl, config, data, UTF8);

            } else {
                throw new IllegalAccessException("连接不上服务器");
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (null == msg) {
                msg = "null";
            }
            //如果是connect timed out 就重试连接
            //connect failed: ECONNREFUSED (Connection refused)
            msg = msg.toLowerCase();
            boolean isConnectFailed = msg.contains("connect failed") || msg.contains("failed to connect to");
            boolean isConnectTimeOut = msg.contains("connect timed out");
            if (isConnectFailed || isConnectTimeOut) {
                try {
                    Thread.sleep(isConnectFailed ? 500 : 100);
                } catch (Exception ignored) {
                }
                if (tryCount > 0) {
                    RequestConfig requestConfig = config.copy();
                    requestConfig.addTimeout(requestConfig.timeout);
                    requestConfig.addConnectTimeout(requestConfig.connectTimeout);
                    return ___request_1(requestUrl, requestConfig, data, --tryCount);
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

     static boolean isLANRequest(String requestUrl) {
        return null != requestUrl && (
                requestUrl.startsWith("http://192.") ||
                        requestUrl.startsWith("http://172.")
        );
    }

    private static String ___request_2(final String requestUrl, final RequestConfig config, List<NameValuePair> data, String charset) throws Exception {
        InputStream in = null;
        OutputStream os = null;
        OutputStreamWriter writer = null;
        BufferedWriter out = null;
        try {
            final String requestData = getQuery(data);
            HttpURLConnection conn = createHttpURLConnection(requestUrl, config, requestData.length());
            HttpURLConnectionHelper.set(conn);
            os = conn.getOutputStream();
            if (null == os) {
                throw new IOException("connect timed out " + (null == config ? "[null config]" : config.connectTimeout));
            }
            writer = new OutputStreamWriter(os, charset);
            out = new BufferedWriter(writer);
            out.write(requestData);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(os);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                //处理服务器的响应结果
                String result = IOUtils.toString(in, charset);
                IOUtils.closeQuietly(in);
                return result;
            } else {
                throw new IllegalAccessException(
                        "The server has error,status " +
                                "code(" + responseCode + "), " +
                                "url(" + requestUrl + ") please try again latter");
            }
        } finally {
            HttpURLConnectionHelper.remove();
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(in);
        }
    }

    private static HttpURLConnection createHttpURLConnection(String requestUrl) throws IOException {
        return createHttpURLConnection(requestUrl, -1, -1, -1);
    }

    private static HttpURLConnection createHttpURLConnection(String requestUrl, RequestConfig config, int contentLength) throws IOException {
        return createHttpURLConnection(requestUrl, config.connectTimeout, config.timeout, contentLength);
    }

    private static HttpURLConnection createHttpURLConnection(String requestUrl, int connectTimeout, int readTimeout, int contentLength) throws IOException {
        return createHttpURLConnection(requestUrl, "POST", connectTimeout, readTimeout, contentLength);
    }

    private static HttpURLConnection createHttpURLConnection(String requestUrl, String method, int connectTimeout, int readTimeout, int contentLength) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
        conn.setRequestMethod(method);          //设置以Post方式提交数据
        if (connectTimeout > 0) {
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connectTimeout));
            conn.setConnectTimeout(connectTimeout);    //返回读取数据数据所需要的实际
        }
        if (readTimeout > 0) {
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(readTimeout));
            conn.setReadTimeout(readTimeout);
        }
        if ("POST".equalsIgnoreCase(method)) {
            //设置请求体的类型是文本类型
            conn.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            conn.setUseCaches(false);              //不使用缓存
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            System.setProperty("http.keepAlive", "false");
            if (contentLength > 0) {
                conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
            }
            conn.setRequestProperty("Connection", "close");
        }
        return conn;
    }

    public static String postContent(String serverUrl, String content) throws Exception {
        HttpURLConnection conn = null;
        DataOutputStream out = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            conn = createHttpURLConnection(serverUrl);
            if (null != content) {
                StringEntity entity = new StringEntity(content, "utf-8");
                conn.setRequestProperty("Content-Type", entity.getContentType().getValue());
                Header contentEncodingHeader = entity.getContentEncoding();
                String charset = "utf-8";
                if (null != contentEncodingHeader) {
                    charset = contentEncodingHeader.getValue();
                }
                conn.setRequestProperty("Content-Encoding", charset);
                os = conn.getOutputStream();
                out = new DataOutputStream(os);
                entity.writeTo(out);
                out.close();
                conn.connect();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                //处理服务器的响应结果
                return IOUtils.toString(is, UTF8);
            } else {
                throw new IllegalAccessException(
                        "The server has error,status " +
                                "code(" + responseCode + "), " +
                                "url(" + serverUrl + ") please try again latter");
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(conn);
        }
    }

    public static int getContentLength(String serverUrl) throws IOException {
        HttpURLConnection conn = null;
        try {
            int responseCode = 0;
            try {
                conn = (HttpURLConnection) new URL(serverUrl).openConnection();
                conn.setRequestMethod("HEAD");
                conn.connect();
                responseCode = conn.getResponseCode();
            } catch (Exception e) {
                String msg = e.getMessage() == null ? "" : e.getMessage();
                if (!msg.toUpperCase().contains("HEAD ")) {
                    throw e;
                }
            }
            if (!isOkResponseCode(responseCode)) {
                if (responseCode == 501 || isLANRequest(serverUrl)) {
                    IOUtils.closeQuietly(conn);
                    conn = (HttpURLConnection) new URL(serverUrl).openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    responseCode = conn.getResponseCode();
                }
            }
            if (null != conn) {
                if (isOkResponseCode(responseCode)) {
                    return conn.getContentLength();
                } else {
                    return responseCode - 2 * responseCode;
                }
            } else {
                return -1;
            }
        } catch (Exception ex) {
            return -404;
        } finally {
            IOUtils.closeQuietly(conn);
        }
    }

    private static boolean isOkResponseCode(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == HttpURLConnection.HTTP_OK;
    }


    private static Map<String, String> getHttpResponseHeader(HttpURLConnection http) throws UnsupportedEncodingException {
        Map<String, String> header = new LinkedHashMap<>();
        for (int i = 0; ; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null) {
                break;
            }
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }

    public static InputStream openUrl(String url) throws IOException {
        return new URL(url).openStream();
    }

    public static InputStream getUrl(String url, int timeout) throws IOException {
        return requestUrl(url, "GET", timeout);
    }

    public static InputStream postUrl(String url, int timeout) throws IOException {
        return requestUrl(url, "POST", timeout);
    }

    public static InputStream requestUrl(String url, String method, int timeout) throws IOException {
        HttpURLConnection conn = createHttpURLConnection(url, method, 10000, timeout, -1);
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            return conn.getInputStream();
        } else {
            throw new RuntimeException(
                    "The server has error,status " +
                            "code(" + responseCode + "), " +
                            "url(" + url + ") please try again latter");
        }
    }

    public static String requestUrl(String url, String charset) throws Exception {
        return ___request_2(url, null, new ArrayList<NameValuePair>(), charset);
    }


    //默认连接超时时间
    protected static final int CONNECT_TIMEOUT = 8000;
    //云POS主机请求云平台默认超时时间
    protected static final int SO_TIMEOUT = 12000;
    //云POS客机请求云POS主机，但是这个请求主机可能会请求云平台， 这类型的客机默认超时时间。
    protected static final int SO_TIMEOUT_CLIENT = SO_TIMEOUT + 5000;
    //云POS客机连接主机默认超时时间
    protected static final int CONNECT_TIMEOUT_LOCAL = 5000;
    //云POS客机请求读取主机默认超时时间， 这个连接不会和云平台连接，并且处理很快。
    protected static final int SO_TIMEOUT_LOCAL = 10000;
    //连接超时默认重连次数
    protected static final int DEF_TRY_COUNT = 1;

    public static class RequestConfig {
        public int connectTimeout = CONNECT_TIMEOUT;
        public int timeout = SO_TIMEOUT;
        public String charset = UTF8;
        public int tryCount = DEF_TRY_COUNT;

        public RequestConfig setTryCount(int count) {
            this.tryCount = count;
            return this;
        }

        public RequestConfig copy() {
            RequestConfig rc = new RequestConfig();
            rc.connectTimeout = this.connectTimeout;
            rc.timeout = this.timeout;
            rc.charset = this.charset;
            rc.tryCount = this.tryCount;
            return rc;
        }

        public void addTimeout(int add) {
            this.timeout = this.timeout + add;
        }

        public void addConnectTimeout(int add) {
            this.connectTimeout = this.connectTimeout + add;
        }
    }

}
