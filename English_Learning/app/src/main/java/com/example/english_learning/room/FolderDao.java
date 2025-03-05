package com.example.english_learning.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FolderDao {
    @Insert
    void insert(FolderEntity folder);

    @Update
    void update(FolderEntity folder);

    @Delete
    void delete(FolderEntity folder);

    @Query("SELECT * FROM folders")
    LiveData<List<FolderEntity>> getAllFolders();

    @Query("SELECT EXISTS(SELECT 1 FROM folders WHERE name = :folderName)")
    Boolean isFolderNameExists(String folderName);
}