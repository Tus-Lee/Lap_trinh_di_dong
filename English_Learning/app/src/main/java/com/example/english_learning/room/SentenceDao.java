package com.example.english_learning.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SentenceDao {
    @Insert
    void insert(SentenceEntity sentenceEntity);

    @Query("SELECT * FROM sentences")
    LiveData<List<SentenceEntity>> getAllSentences();

    @Query("SELECT * FROM sentences WHERE folderId = :folderId")
    LiveData<List<SentenceEntity>> getSentencesByFolderId(int folderId);
}