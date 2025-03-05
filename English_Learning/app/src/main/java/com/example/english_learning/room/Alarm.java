package com.example.english_learning.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm_table")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "is_enable")
    private boolean isEnable;

    @ColumnInfo(name = "repeat_count") // Thêm trường repeatCount
    private int repeatCount;

    public Alarm(String time, boolean isActive, int repeatCount) {
        this.time = time;
        this.isActive = isActive;
        this.isEnable = isActive; // Mặc định khi tạo thì isEnable = isActive
        this.repeatCount = repeatCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
}