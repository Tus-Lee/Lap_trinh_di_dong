package com.example.english_learning.model;

import java.util.List;

public class Question {
    private String text;
    private List<String> topics;
    private int index; // Thêm trường index

    public Question(String text, List<String> topics, int index) {
        this.text = text;
        this.topics = topics;
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}