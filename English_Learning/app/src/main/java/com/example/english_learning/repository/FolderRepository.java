package com.example.english_learning.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.english_learning.room.AppDatabase;
import com.example.english_learning.room.FolderDao;
import com.example.english_learning.room.FolderEntity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FolderRepository {
    private FolderDao folderDao;
    private LiveData<List<FolderEntity>> allFolders;

    public FolderRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        folderDao = db.folderDao();
        allFolders = folderDao.getAllFolders();
    }

    public LiveData<List<FolderEntity>> getAllFolders() {
        return allFolders;
    }

    public void insert(FolderEntity folder) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            folderDao.insert(folder);
        });
    }

    public void update(FolderEntity folder) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            folderDao.update(folder);
        });
    }

    public void delete(FolderEntity folder) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            folderDao.delete(folder);
        });
    }

    public Boolean isFolderNameExists(String folderName) {
        Callable<Boolean> callable = () -> folderDao.isFolderNameExists(folderName);
        Future<Boolean> future = AppDatabase.databaseWriteExecutor.submit(callable);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}