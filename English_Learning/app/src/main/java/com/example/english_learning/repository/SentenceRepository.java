package com.example.english_learning.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.english_learning.room.AppDatabase;
import com.example.english_learning.room.SentenceDao;
import com.example.english_learning.room.SentenceEntity;

import java.util.List;

public class SentenceRepository {
    private SentenceDao sentenceDao;
    private LiveData<List<SentenceEntity>> allSentences;

    public SentenceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        sentenceDao = db.sentenceDao();
        allSentences = sentenceDao.getAllSentences();
    }

    public LiveData<List<SentenceEntity>> getAllSentences() {
        return allSentences;
    }

    public void insert(SentenceEntity sentence) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            sentenceDao.insert(sentence);
        });
    }
    public LiveData<List<SentenceEntity>> getSentencesByFolderId(int folderId) {
        return sentenceDao.getSentencesByFolderId(folderId);
    }
}