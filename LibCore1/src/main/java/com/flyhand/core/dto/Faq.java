package com.flyhand.core.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-10-27
 * Time: Afternoon 3:31
 */
public class Faq {
    private int id;
    private String question;
    private String answer;
    private int sort;

    public Faq() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
