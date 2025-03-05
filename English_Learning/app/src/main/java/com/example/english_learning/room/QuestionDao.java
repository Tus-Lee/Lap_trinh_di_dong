package com.example.english_learning.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    void insertAll(List<QuestionEntity> questions);

    @Query("SELECT COUNT(*) FROM questions")
    int getCount();

    @Query("SELECT * FROM questions ORDER BY `index` ASC")
    List<QuestionEntity> getAll();

    @Query("SELECT * FROM questions WHERE text LIKE '%' || :text || '%' ORDER BY `index` ASC")
    List<QuestionEntity> getQuestionsByText(String text);

    @Query("SELECT * FROM questions WHERE topicsJson LIKE '%' || :topic || '%' ORDER BY `index` ASC")
    List<QuestionEntity> getQuestionsByTopics(String topic);

    // Lấy danh sách câu hỏi đã hoàn thành
    @Query("SELECT * FROM questions WHERE isSuccess = 1 ORDER BY `index` ASC")
    List<QuestionEntity> getCompletedQuestions();

    // Cập nhật trạng thái hoàn thành của một câu hỏi
    @Query("UPDATE questions SET isSuccess = :isSuccess WHERE questionId = :questionId")
    void updateSuccessStatus(int questionId, boolean isSuccess);

    // Đặt lại tất cả câu hỏi về chưa hoàn thành
    @Query("UPDATE questions SET isSuccess = 0")
    void resetAllSuccess();

    // Phương thức mới để cập nhật nhiều QuestionEntity
    @Update
    void updateAll(List<QuestionEntity> questions);

    // Phương thức mới: Lấy tổng số câu hỏi thuộc một chủ đề
    @Query("SELECT COUNT(*) FROM questions WHERE topicsJson LIKE '%' || :topic || '%'")
    int getCountByTopic(String topic);

    // Phương thức mới: Lấy số câu hỏi đã học thuộc một chủ đề
    @Query("SELECT COUNT(*) FROM questions WHERE topicsJson LIKE '%' || :topic || '%' AND isSuccess = 1")
    int getCompletedCountByTopic(String topic);
}