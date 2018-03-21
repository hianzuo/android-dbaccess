package com.flyhand.core.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-10-24
 * Time: Afternoon 5:04
 */
public class VersionUpdate {
    private int id;  //ID主键
    private String appkey;  //应用KEY
    private int versionCode;  //版本号
    private String versionName;  //版本名字
    private String versionInfo;  //更新信息
    private String downloadUrl;  //下载地址
    private String downloadUrlBak;  //备用地址
    private String requireUpdate;  //更新策略
    private int count;  //下载次数

    public VersionUpdate() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrlBak() {
        return downloadUrlBak;
    }

    public void setDownloadUrlBak(String downloadUrlBak) {
        this.downloadUrlBak = downloadUrlBak;
    }

    public String getRequireUpdate() {
        return requireUpdate;
    }

    public void setRequireUpdate(String requireUpdate) {
        this.requireUpdate = requireUpdate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isRequiredUpdate() {
        return "required".equals(this.requireUpdate);
    }
}
