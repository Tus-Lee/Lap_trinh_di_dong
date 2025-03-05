package com.example.english_learning.room;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class PackageWithQuestions {
    @Embedded
    public StudyPackageEntity studyPackage;
    @Relation(
            parentColumn = "study_package_id", // Thay đổi ở đây
            entityColumn = "questionId",
            associateBy = @Junction(PackageQuestionCrossRef.class)
    )
    public List<QuestionEntity> questions;
}