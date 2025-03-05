package com.example.english_learning.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "packagequestioncrossref",
        primaryKeys = {"study_package_id", "questionId"},
        foreignKeys = {
                @ForeignKey(entity = StudyPackageEntity.class,
                        parentColumns = "study_package_id", // Đúng với tên cột trong StudyPackageEntity
                        childColumns = "study_package_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = QuestionEntity.class,
                        parentColumns = "questionId", // Đúng với tên cột trong QuestionEntity
                        childColumns = "questionId",
                        onDelete = ForeignKey.CASCADE)
        })
public class PackageQuestionCrossRef {
    @ColumnInfo(name = "study_package_id", index = true)
    public int studyPackageId; // Đổi từ packageId -> studyPackageId để khớp với StudyPackageEntity

    @ColumnInfo(index = true)
    public int questionId; // Giữ nguyên vì đã khớp với QuestionEntity

    public PackageQuestionCrossRef(int studyPackageId, int questionId) {
        this.studyPackageId = studyPackageId;
        this.questionId = questionId;
    }

    // Getters and setters
    public int getStudyPackageId() {
        return studyPackageId;
    }

    public void setStudyPackageId(int studyPackageId) {
        this.studyPackageId = studyPackageId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}