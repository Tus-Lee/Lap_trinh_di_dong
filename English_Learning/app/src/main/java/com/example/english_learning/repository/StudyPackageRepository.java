package com.example.english_learning.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.english_learning.room.AppDatabase;
import com.example.english_learning.room.PackageQuestionCrossRef;
import com.example.english_learning.room.PackageWithQuestions;
import com.example.english_learning.room.QuestionEntity;
import com.example.english_learning.room.StudyPackageDao;
import com.example.english_learning.room.StudyPackageEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StudyPackageRepository {
    private StudyPackageDao studyPackageDao;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private QuestionRepository questionRepository;

    public StudyPackageRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        studyPackageDao = db.studyPackageDao();
        questionRepository = new QuestionRepository(application);
    }

    public void insert(StudyPackageEntity studyPackage) {
        executor.execute(() -> {
            studyPackageDao.insert(studyPackage);
        });
    }

    public void insertStudyPackages(List<QuestionEntity> questions, int dailyCount, final OnPackagesInsertedCallback callback) {
        executor.execute(() -> {
            int totalQuestions = questions.size();
            int effectiveDailyCount = dailyCount;
            if (effectiveDailyCount > totalQuestions) {
                effectiveDailyCount = totalQuestions;
            }
            int numPackages = (int) Math.ceil((double) totalQuestions / effectiveDailyCount);
            int start = 0;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());

            for (int i = 0; i < numPackages; i++) {
                int actualQuestionCount = Math.min(effectiveDailyCount, totalQuestions - start);
                String newPackageName = "Gói " + (i + 1);
                String currentDate = dateFormat.format(calendar.getTime());

                StudyPackageEntity studyPackage = new StudyPackageEntity(newPackageName, actualQuestionCount, currentDate);
                long studyPackageId = studyPackageDao.insert(studyPackage);

                for (int j = 0; j < actualQuestionCount; j++) {
                    QuestionEntity question = questions.get(start + j);
                    PackageQuestionCrossRef crossRef = new PackageQuestionCrossRef((int) studyPackageId, question.getQuestionId());
                    studyPackageDao.insertPackageQuestionCrossRef(crossRef);
                }
                start += actualQuestionCount;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            mainThreadHandler.post(() -> {
                callback.onPackagesInserted(numPackages);
            });
        });
    }

    public void insertStudyPackage(StudyPackageEntity studyPackage, List<QuestionEntity> questions) {
        executor.execute(() -> {
            long studyPackageId = studyPackageDao.insert(studyPackage);
            for (QuestionEntity question : questions) {
                PackageQuestionCrossRef crossRef = new PackageQuestionCrossRef((int) studyPackageId, question.getQuestionId());
                studyPackageDao.insertPackageQuestionCrossRef(crossRef);
            }
        });
    }

    public void getPackagesWithQuestions(final OnDataLoadedCallback callback) {
        executor.execute(() -> {
            List<PackageWithQuestions> packages = studyPackageDao.getPackagesWithQuestions();
            mainThreadHandler.post(() -> {
                callback.onDataLoaded(packages);
            });
        });
    }

    public LiveData<PackageWithQuestions> getStudyPackageWithQuestions(int studyPackageId) {
        MutableLiveData<PackageWithQuestions> data = new MutableLiveData<>();
        executor.execute(() -> {
            PackageWithQuestions packageWithQuestions = studyPackageDao.getStudyPackageWithQuestions(studyPackageId);
            mainThreadHandler.post(() -> {
                data.setValue(packageWithQuestions);
            });
        });
        return data;
    }

    public void deleteAllPackages() {
        executor.execute(() -> {
            studyPackageDao.deleteAllPackages();
        });
    }

    public void deleteAllPackageQuestionCrossRef() {
        executor.execute(() -> {
            studyPackageDao.deleteAllPackageQuestionCrossRef();
        });
    }

    public void updateDifficultyLevel(int studyPackageId, String difficultyLevel) {
        executor.execute(() -> {
            studyPackageDao.updateDifficultyLevel(studyPackageId, difficultyLevel);
        });
    }

    public void updateNotificationInterval(int studyPackageId, long notificationInterval) {
        executor.execute(() -> {
            studyPackageDao.updateNotificationInterval(studyPackageId, notificationInterval);
        });
    }

    public void updateLastNotificationTime(int studyPackageId, long lastNotificationTime) {
        executor.execute(() -> {
            studyPackageDao.updateLastNotificationTime(studyPackageId, lastNotificationTime);
        });
    }

    public void updateIsAlarmSet(int studyPackageId, boolean isAlarmSet) {
        executor.execute(() -> {
            studyPackageDao.updateIsAlarmSet(studyPackageId, isAlarmSet);
        });
    }

    public void updateStudyPackage(StudyPackageEntity studyPackage) {
        executor.execute(() -> {
            studyPackageDao.updateStudyPackage(studyPackage);
        });
    }

    public LiveData<StudyPackageEntity> getStudyPackageById(int studyPackageId) {
        return studyPackageDao.getStudyPackageById(studyPackageId);
    }

    public void updateIsCompleted(int studyPackageId, boolean isCompleted) {
        executor.execute(() -> {
            if (isCompleted) {
                // Nếu gói học được đánh dấu là hoàn thành, cập nhật trạng thái success của các câu hỏi
                studyPackageDao.updateIsCompleted(studyPackageId, true);
                PackageWithQuestions packageWithQuestions = studyPackageDao.getStudyPackageWithQuestions(studyPackageId);
                if (packageWithQuestions != null) {
                    for (QuestionEntity question : packageWithQuestions.questions) {
                        questionRepository.updateSuccessStatus(question.getQuestionId(), true);
                    }
                }
            } else {
                // Nếu gói học bị hủy hoàn thành, chỉ cập nhật trạng thái gói học, không thay đổi trạng thái câu hỏi
                studyPackageDao.updateIsCompleted(studyPackageId, false);
            }
        });
    }

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<PackageWithQuestions> packages);
    }

    public interface OnPackagesInsertedCallback {
        void onPackagesInserted(int numPackages);
    }
}