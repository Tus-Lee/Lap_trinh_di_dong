package com.example.english_learning.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.english_learning.repository.FolderRepository;
import com.example.english_learning.room.FolderEntity;

import java.util.List;

public class FolderViewModel extends AndroidViewModel {
    private FolderRepository folderRepository;
    private LiveData<List<FolderEntity>> allFolders;
    private MutableLiveData<String> folderNameToCheck = new MutableLiveData<>();
    private MediatorLiveData<Boolean> isFolderNameExistsResult = new MediatorLiveData<>();

    public FolderViewModel(@NonNull Application application) {
        super(application);
        folderRepository = new FolderRepository(application);
        allFolders = folderRepository.getAllFolders();

        // Kết hợp kết quả từ isFolderNameExists() và tên thư mục cần kiểm tra
        isFolderNameExistsResult.addSource(folderNameToCheck, folderName -> {
            if (folderName != null) {
                isFolderNameExistsResult.setValue(folderRepository.isFolderNameExists(folderName));
            }
        });
    }

    public void insert(FolderEntity folder) {
        folderRepository.insert(folder);
    }

    public void update(FolderEntity folder) {
        folderRepository.update(folder);
    }

    public void delete(FolderEntity folder) {
        folderRepository.delete(folder);
    }

    public LiveData<List<FolderEntity>> getAllFolders() {
        return allFolders;
    }

    public LiveData<Boolean> getIsFolderNameExistsResult() {
        return isFolderNameExistsResult;
    }

    public void checkFolderName(String folderName) {
        folderNameToCheck.setValue(folderName);
    }
}