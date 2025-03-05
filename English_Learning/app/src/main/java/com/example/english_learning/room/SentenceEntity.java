package com.example.english_learning.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "sentences", foreignKeys = @ForeignKey(entity = FolderEntity.class, parentColumns = "id", childColumns = "folderId", onDelete = ForeignKey.CASCADE))
public class SentenceEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "folderId")
    private int folderId;

    @ColumnInfo(name = "key")
    private String key;

    public SentenceEntity(String text, int folderId, String key) {
        this.text = text;
        this.folderId = folderId;
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}