package com.example.english_learning.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.english_learning.room.PackageWithQuestions;
import com.example.english_learning.room.StudyPackageEntity;
import com.example.english_learning.repository.StudyPackageRepository;

public class StudyPackageViewModel extends AndroidViewModel {
    private StudyPackageRepository repository;

    public StudyPackageViewModel(@NonNull Application application) {
        super(application);
        repository = new StudyPackageRepository(application);
    }

    public LiveData<StudyPackageEntity> getStudyPackageById(int studyPackageId) {
        return repository.getStudyPackageById(studyPackageId);
    }

    public void updateDifficultyLevel(int studyPackageId, String difficultyLevel) {
        repository.updateDifficultyLevel(studyPackageId, difficultyLevel);
    }

    public void updateNotificationInterval(int studyPackageId, long notificationInterval) {
        repository.updateNotificationInterval(studyPackageId, notificationInterval);
    }

    public void updateLastNotificationTime(int studyPackageId, long lastNotificationTime) {
        repository.updateLastNotificationTime(studyPackageId, lastNotificationTime);
    }

    public void updateIsAlarmSet(int studyPackageId, boolean isAlarmSet) {
        repository.updateIsAlarmSet(studyPackageId, isAlarmSet);
    }
    public void updateStudyPackage(StudyPackageEntity studyPackage) {
        repository.updateStudyPackage(studyPackage);
    }
    // Thêm phương thức getStudyPackageWithQuestions
    public LiveData<PackageWithQuestions> getStudyPackageWithQuestions(int studyPackageId) {
        return repository.getStudyPackageWithQuestions(studyPackageId);
    }
}