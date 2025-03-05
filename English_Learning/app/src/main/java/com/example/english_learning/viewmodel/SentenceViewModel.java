package com.example.english_learning.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.english_learning.repository.SentenceRepository;
import com.example.english_learning.room.SentenceEntity;

import java.util.List;

public class SentenceViewModel extends AndroidViewModel {
    private SentenceRepository sentenceRepository;
    private LiveData<List<SentenceEntity>> allSentences;

    public SentenceViewModel(Application application) {
        super(application);
        sentenceRepository = new SentenceRepository(application);
        allSentences = sentenceRepository.getAllSentences();
    }

    public void insert(SentenceEntity sentence) {
        sentenceRepository.insert(sentence);
    }

    public LiveData<List<SentenceEntity>> getAllSentences() {
        return allSentences;
    }
    public LiveData<List<SentenceEntity>> getSentencesByFolderId(int folderId) {
        return sentenceRepository.getSentencesByFolderId(folderId);
    }
}