package com.flyhand.core.dto;

/**
 * Ryan
 * User: Administrator
 * Date: 11-11-9
 * Time: Afternoon 11:26
 */
public class AppConfig {
    private int id = 0;  //主键ID
    private int userId = 0;  //用户ID
    private String appkey = "";  //应用KEY
    private String name = "";  //应用名称
    private String type = "";  //应用类型
    private String intro = "";  //应用介绍
    private String appExpand = "";  //扩展信息
    private String appNote = "";  //应用备注
    private int versionCode = 0;  //版本号
    private String versionName = "";  //版本名称
    private String updateInfo = "";  //更新信息
    private String downloadUrl = "";  //下载地址
    private String iconUrl = "";  //图标地址
    private String email = "";  //配置邮件
    private String emailPwd = "";  //配置邮件密码
    private String emailRec = "";  //邮件接收者
    private String errorRecTactics = "";  //错误接收策略
    private String adTactics = "";  //错误接收策略
    private String state = "";  //状态
    private String addTime = "";  //加入时间

    public AppConfig() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAppExpand() {
        return appExpand;
    }

    public void setAppExpand(String appExpand) {
        this.appExpand = appExpand;
    }

    public String getAppNote() {
        return appNote;
    }

    public void setAppNote(String appNote) {
        this.appNote = appNote;
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

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPwd() {
        return emailPwd;
    }

    public void setEmailPwd(String emailPwd) {
        this.emailPwd = emailPwd;
    }

    public String getEmailRec() {
        return emailRec;
    }

    public void setEmailRec(String emailRec) {
        this.emailRec = emailRec;
    }

    public String getErrorRecTactics() {
        return errorRecTactics;
    }

    public void setErrorRecTactics(String errorRecTactics) {
        this.errorRecTactics = errorRecTactics;
    }

    public String getAdTactics() {
        return "{\"mjkfConfig\":\"refuse\"}";
//        return "{\"mjkfConfig\":\"admob|bottom|10000\"}";
//        return "{\"mjkfConfig\":\"admob1%domob1|bottom|10000\"}";
//        return "{\"mjkfConfig\":\"taonan1%domob1%mobwin1%aiwan1%admob1%youmi1%adview1%adwo1%lmmob1|bottom|10000\"}";
//        return adTactics;
    }

    public void setAdTactics(String adTactics) {
        this.adTactics = adTactics;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }


    @Override
    public String toString() {
        return new StringBuffer().
                append(id).append("|")
                .append(userId).append("|")
                .append(appkey).append("|")
                .append(name).append("|")
                .append(type).append("|")
                .append(intro.replace("|", "%sx%")).append("|")
                .append(appExpand.replace("|", "%sx%")).append("|")
                .append(appNote.replace("|", "%sx%")).append("|")
                .append(versionCode).append("|")
                .append(versionName.replace("|", "%sx%")).append("|")
                .append(updateInfo.replace("|", "%sx%")).append("|")
                .append(downloadUrl).append("|")
                .append(iconUrl).append("|")
                .append(email).append("|")
                .append(emailPwd.replace("|", "%sx%")).append("|")
                .append(emailRec).append("|")
                .append(errorRecTactics).append("|")
                .append(adTactics.replace("|", "%sx%")).append("|")
                .append(state).append("|")
                .append(addTime).toString();
    }

    public static AppConfig toAppConfig(String str) {
        if (null == str) {
            return null;
        }
        String[] ss = str.trim().split("\\|");
        AppConfig ac = new AppConfig();
        ac.setId(Integer.parseInt(ss[0]));
        ac.setUserId(Integer.parseInt(ss[1]));
        ac.setAppkey(ss[2]);
        ac.setName(ss[3]);
        ac.setType(ss[4]);
        ac.setIntro(ss[5].replace("%sx%", "|"));
        ac.setAppExpand(ss[6].replace("%sx%", "|"));
        ac.setAppNote(ss[7].replace("%sx%", "|"));
        ac.setVersionCode(Integer.parseInt(ss[8]));
        ac.setVersionName(ss[9].replace("%sx%", "|"));
        ac.setUpdateInfo(ss[10].replace("%sx%", "|"));
        ac.setDownloadUrl(ss[11]);
        ac.setIconUrl(ss[12]);
        ac.setEmail(ss[13]);
        ac.setEmailPwd(ss[14].replace("%sx%", "|"));
        ac.setEmailRec(ss[15]);
        ac.setErrorRecTactics(ss[16].replace("%sx%", "|"));
        ac.setAdTactics(ss[17].replace("%sx%", "|"));
        ac.setState(ss[18]);
        ac.setAddTime(ss[19]);
        return ac;
    }

    public boolean isRefuseAd() {
        String s = getAdTactics();
        return null != s && s.contains("mjkfConfig") && s.contains("refuse");
    }
}
