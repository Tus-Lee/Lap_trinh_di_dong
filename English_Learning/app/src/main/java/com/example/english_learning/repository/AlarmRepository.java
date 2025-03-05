package com.example.english_learning.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.english_learning.room.Alarm;
import com.example.english_learning.room.AlarmDao;
import com.example.english_learning.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> allAlarms;
    private ExecutorService executorService;

    public AlarmRepository(Application application, ExecutorService executorService) {
        AppDatabase db = AppDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        allAlarms = alarmDao.getAllAlarms();
        this.executorService = executorService;
    }

    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    public void insert(Alarm alarm) {
        executorService.execute(() -> alarmDao.insert(alarm));
    }

    public void update(Alarm alarm) {
        executorService.execute(() -> alarmDao.update(alarm));
    }

    public void delete(Alarm alarm) {
        executorService.execute(() -> alarmDao.delete(alarm));
    }
}