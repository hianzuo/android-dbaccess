package com.flyhand.core.dto;

/**
 * User: Ryan
 * Date: 11-9-15
 * Time: Afternoon 5:32
 */
public class LrcSentence {
    private String text;
    private String translation;
    private String time;
    private boolean selected = false;
    private int millisecond = -1;

    public LrcSentence() {
    }

    public LrcSentence(String time, String text, String translation) {
        this.time = time;
        this.text = text;
        this.translation = translation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getMillisecond() {
        if (millisecond != -1) {
            return millisecond;
        } else {
            if (null != this.time) {
                try {
                    String s = this.time;
                    if (s.contains(":")) {
                        int second = Integer.valueOf(s.substring(0, s.indexOf(":"))) * 60 * 1000;
                        int m = Integer.valueOf(s.substring(s.indexOf(":") + 1, s.indexOf("."))) * 1000;
                        int hao_m = Integer.valueOf(s.substring(s.indexOf(".") + 1) + "0");
                        millisecond = second + m + hao_m;
                        return millisecond;
                    } else {
                        return -1;
                    }
                } catch (NumberFormatException e) {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }

    @Override
    public String toString() {
        return this.text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
