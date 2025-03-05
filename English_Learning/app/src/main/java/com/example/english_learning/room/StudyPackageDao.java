package com.example.english_learning.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudyPackageDao {
    @Insert
    long insert(StudyPackageEntity studyPackage);

    @Insert
    void insertPackageQuestionCrossRef(PackageQuestionCrossRef crossRef);

    @Transaction
    @Query("SELECT * FROM study_packages")
    List<PackageWithQuestions> getPackagesWithQuestions();

    @Transaction
    @Query("SELECT * FROM study_packages WHERE study_package_id = :studyPackageId")
    PackageWithQuestions getStudyPackageWithQuestions(int studyPackageId);

    // Xóa tất cả các gói học (chỉ xóa StudyPackageEntity, không xóa QuestionEntity)
    @Query("DELETE FROM study_packages")
    void deleteAllPackages();

    // Xóa tất cả các liên kết giữa gói học và câu hỏi (chỉ xóa PackageQuestionCrossRef)
    @Query("DELETE FROM packagequestioncrossref")
    void deleteAllPackageQuestionCrossRef();

    // Cập nhật thông tin khóa học
    @Query("UPDATE study_packages SET difficulty_level = :difficultyLevel WHERE study_package_id = :studyPackageId")
    void updateDifficultyLevel(int studyPackageId, String difficultyLevel);

    @Query("UPDATE study_packages SET notification_interval = :notificationInterval WHERE study_package_id = :studyPackageId")
    void updateNotificationInterval(int studyPackageId, long notificationInterval);

    @Query("UPDATE study_packages SET last_notification_time = :lastNotificationTime WHERE study_package_id = :studyPackageId")
    void updateLastNotificationTime(int studyPackageId, long lastNotificationTime);

    @Query("UPDATE study_packages SET is_alarm_set = :isAlarmSet WHERE study_package_id = :studyPackageId")
    void updateIsAlarmSet(int studyPackageId, boolean isAlarmSet);

    @Query("SELECT * FROM study_packages WHERE study_package_id = :studyPackageId")
    LiveData<StudyPackageEntity> getStudyPackageById(int studyPackageId);

    @Update
    void updateStudyPackage(StudyPackageEntity studyPackage);

    @Query("UPDATE study_packages SET is_completed = :isCompleted WHERE study_package_id = :studyPackageId")
    void updateIsCompleted(int studyPackageId, boolean isCompleted);
}