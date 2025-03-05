package com.example.english_learning.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.english_learning.room.AppDatabase;
import com.example.english_learning.room.QuestionDao;
import com.example.english_learning.room.QuestionEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QuestionRepository {
    private QuestionDao questionDao;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public QuestionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        questionDao = db.questionDao();
    }

    public void insertAll(List<QuestionEntity> questions) {
        executor.execute(() -> {
            questionDao.insertAll(questions);
        });
    }

    public int getCount() {
        return questionDao.getCount();
    }

    public void getAllQuestions(OnDataLoadedCallback callback) {
        executor.execute(() -> {
            List<QuestionEntity> questions = questionDao.getAll();
            mainThreadHandler.post(() -> {
                callback.onDataLoaded(questions);
            });
        });
    }

    public void getQuestionsByText(String text, OnDataLoadedCallback callback) {
        executor.execute(() -> {
            List<QuestionEntity> questions = questionDao.getQuestionsByText(text);
            mainThreadHandler.post(() -> {
                callback.onDataLoaded(questions);
            });
        });
    }

    public void getQuestionsByTopics(String topic, OnDataLoadedCallback callback) {
        executor.execute(() -> {
            List<QuestionEntity> questions = questionDao.getQuestionsByTopics(topic);
            mainThreadHandler.post(() -> {
                callback.onDataLoaded(questions);
            });
        });
    }

    public void updateSuccessStatus(int questionId, boolean isSuccess) {
        executor.execute(() -> {
            questionDao.updateSuccessStatus(questionId, isSuccess);
        });
    }

    // Phương thức mới để cập nhật nhiều QuestionEntity
    public void updateAll(List<QuestionEntity> questions) {
        executor.execute(() -> {
            questionDao.updateAll(questions);
        });
    }

    public void resetAllSuccess() {
        executor.execute(() -> {
            questionDao.resetAllSuccess();
        });
    }

    public void getCompletedQuestions(OnDataLoadedCallback callback) {
        executor.execute(() -> {
            List<QuestionEntity> questions = questionDao.getCompletedQuestions();
            mainThreadHandler.post(() -> {
                callback.onDataLoaded(questions);
            });
        });
    }

    // Phương thức mới: Lấy tổng số câu hỏi thuộc một chủ đề
    public void getCountByTopic(String topic, OnCountLoadedCallback callback) {
        executor.execute(() -> {
            int count = questionDao.getCountByTopic(topic);
            mainThreadHandler.post(() -> {
                callback.onCountLoaded(count);
            });
        });
    }

    // Phương thức mới: Lấy số câu hỏi đã học thuộc một chủ đề
    public void getCompletedCountByTopic(String topic, OnCountLoadedCallback callback) {
        executor.execute(() -> {
            int count = questionDao.getCompletedCountByTopic(topic);
            mainThreadHandler.post(() -> {
                callback.onCountLoaded(count);
            });
        });
    }

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<QuestionEntity> questionEntities);
    }

    // Thêm interface mới cho việc trả về số lượng
    public interface OnCountLoadedCallback {
        void onCountLoaded(int count);
    }
}