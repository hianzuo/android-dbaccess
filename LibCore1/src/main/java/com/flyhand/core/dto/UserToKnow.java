package com.flyhand.core.dto;

import com.flyhand.core.utils.StringUtil;

/**
 * User: Ryan
 * Date: 11-10-16
 * Time: Afternoon 12:32
 */
public class UserToKnow {
    protected int id = 0;  //主键ID
    protected String appkey;  //应用KEY
    protected String title;  //标题
    protected String content;  //公告内容
    protected String btnOneText;  //按钮一文字
    protected String btnOneUri;  //按钮一URI
    protected String btnTwoText;  //按钮二文字
    protected String btnTwoUri;  //按钮二URI
    protected String btnThreeText;  //按钮三文字
    protected String btnThreeUri;  //按钮三URI
    protected int showCount;  //显示次数
    protected int count;  //查看次数

    public UserToKnow() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBtnOneText() {
        return btnOneText;
    }

    public void setBtnOneText(String btnOneText) {
        this.btnOneText = btnOneText;
    }

    public String getBtnOneUri() {
        return btnOneUri;
    }

    public void setBtnOneUri(String btnOneUri) {
        this.btnOneUri = btnOneUri;
    }

    public String getBtnTwoText() {
        return btnTwoText;
    }

    public void setBtnTwoText(String btnTwoText) {
        this.btnTwoText = btnTwoText;
    }

    public String getBtnTwoUri() {
        return btnTwoUri;
    }

    public void setBtnTwoUri(String btnTwoUri) {
        this.btnTwoUri = btnTwoUri;
    }

    public String getBtnThreeText() {
        return btnThreeText;
    }

    public void setBtnThreeText(String btnThreeText) {
        this.btnThreeText = btnThreeText;
    }

    public String getBtnThreeUri() {
        return btnThreeUri;
    }

    public void setBtnThreeUri(String btnThreeUri) {
        this.btnThreeUri = btnThreeUri;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "UserToKnow{" +
                "id=" + id +
                ", appkey='" + appkey + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", btnOneText='" + btnOneText + '\'' +
                ", btnOneUri='" + btnOneUri + '\'' +
                ", btnTwoText='" + btnTwoText + '\'' +
                ", btnTwoUri='" + btnTwoUri + '\'' +
                ", btnThreeText='" + btnThreeText + '\'' +
                ", btnThreeUri='" + btnThreeUri + '\'' +
                ", showCount=" + showCount +
                ", count=" + count +
                '}';
    }

    public boolean hasBtnTwo() {
        return StringUtil.isNotEmpty(getBtnTwoText()) && !"null".equals(getBtnTwoText());
    }

    public boolean hasBtnThree() {
        return StringUtil.isNotEmpty(getBtnThreeText()) && !"null".equals(getBtnThreeText());
    }
}
