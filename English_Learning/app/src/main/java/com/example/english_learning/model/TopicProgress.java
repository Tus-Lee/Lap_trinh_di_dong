package com.example.english_learning.model;

public class TopicProgress {
    private String topicName;
    private int totalCount;
    private int completedCount;

    public TopicProgress(String topicName, int totalCount, int completedCount) {
        this.topicName = topicName;
        this.totalCount = totalCount;
        this.completedCount = completedCount;
    }

    public String getTopicName() { return topicName; }
    public int getTotalCount() { return totalCount; }
    public int getCompletedCount() { return completedCount; }
}

