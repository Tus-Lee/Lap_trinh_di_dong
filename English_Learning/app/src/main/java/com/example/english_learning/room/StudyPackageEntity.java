package com.example.english_learning.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_packages")
public class StudyPackageEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "study_package_id")
    private int studyPackageId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "question_count")
    private int questionCount;

    @ColumnInfo(name = "created_date")
    private String date; // Giữ lại trường date, đổi tên thành created_date

    @ColumnInfo(name = "difficulty_level")
    private String difficultyLevel;

    @ColumnInfo(name = "notification_interval")
    private long notificationInterval;

    @ColumnInfo(name = "last_notification_time")
    private long lastNotificationTime;

    @ColumnInfo(name = "is_alarm_set")
    private boolean isAlarmSet; // Đổi tên thành is_alarm_set

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted; // Thêm trường isCompleted

    public StudyPackageEntity(String name, int questionCount, String date) {
        this.name = name;
        this.questionCount = questionCount;
        this.date = date;
        this.difficultyLevel = ""; // Khởi tạo giá trị mặc định
        this.notificationInterval = 0; // Khởi tạo giá trị mặc định
        this.lastNotificationTime = 0; // Khởi tạo giá trị mặc định
        this.isAlarmSet = false; // Khởi tạo giá trị mặc định
        this.isCompleted = false; // Khởi tạo giá trị mặc định
    }

    // Getter và setter cho date (đã đổi tên thành created_date)
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Các getter và setter khác
    public int getStudyPackageId() {
        return studyPackageId;
    }

    public void setStudyPackageId(int studyPackageId) {
        this.studyPackageId = studyPackageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    // Getter và setter cho difficultyLevel
    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    // Getter và setter cho notificationInterval
    public long getNotificationInterval() {
        return notificationInterval;
    }

    public void setNotificationInterval(long notificationInterval) {
        this.notificationInterval = notificationInterval;
    }

    // Getter và setter cho lastNotificationTime
    public long getLastNotificationTime() {
        return lastNotificationTime;
    }

    public void setLastNotificationTime(long lastNotificationTime) {
        this.lastNotificationTime = lastNotificationTime;
    }

    // Getter và setter cho isAlarmSet (đã đổi tên từ isNotificationEnabled)
    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    public void setAlarmSet(boolean alarmSet) {
        isAlarmSet = alarmSet;
    }

    // Getter và setter cho isCompleted
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}