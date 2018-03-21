package com.flyhand.core.remote;

import android.content.Context;

/**
 * 调用Http请求返回的相关数据
 * 使用 isSuccess() 方法查看是否调用成功
 * 使用 getResult() 方法获得调用返回数据
 * 使用 getCode() 获得相关的发生错误的代码,相关的错误代码有
 * <string name="net_error_60001">您的手机网络不可用</string>
 * <string name="net_error_60002">没有连接上服务器</string>
 * <string name="net_error_60003">您的账号在其他地方登录了，请点击“重新登录”按钮重新登录或者“退出”应用。</string>
 * <string name="net_error_60004">服务器返回内容格式错误。</string>
 * <string name="net_error_60005">IOException 网络读取错误，请检查网络。</string>
 * <string name="net_error_60006">HttpHostConnectException ，UnknownHostException 网络读取错误，请检查网络。</string>
 */
public class NetResult<M> {
    private final static int NO_ERROR = 0;
    private M obj = null;
    private int code = NO_ERROR;
    private String errMsg = "";

    public NetResult() {
    }

    public NetResult(M obj) {
        this.obj = obj;
    }

    public NetResult(int errCode) {
        this.code = errCode;
    }

    public NetResult(String errMsg, int errCode) {
        this.errMsg = errMsg;
        if (errCode == 0) {
            this.code = 500000005;
        } else {
            this.code = errCode;
        }
    }

    public static String getErrString(Context context, int code) {
        /* switch (code) {
            case 60001:
                return context.getString(RUtils.getRStringID("net_error_60001"));
            case 60002:
                return context.getString(RUtils.getRStringID("net_error_60002"));
            case 60003:
                return context.getString(RUtils.getRStringID("net_error_60003"));
            case 60004:
                return context.getString(RUtils.getRStringID("net_error_60004"));
            case 60005:
                return context.getString(RUtils.getRStringID("net_error_60005"));
            default:
                return context.getString(RUtils.getRStringID("unknow_error"));
        }*/
        return "";
    }

    public boolean isSuccess() {
        return code == NO_ERROR;
    }

    public M getResult() {
        return obj;
    }

    public void setResult(M result) {
        this.obj = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isNetworkError() {
        return code == 60006 || code == 60005;
    }
}
