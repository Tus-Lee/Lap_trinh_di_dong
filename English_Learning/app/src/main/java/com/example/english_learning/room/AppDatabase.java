package com.example.english_learning.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {StudyPackageEntity.class, QuestionEntity.class, PackageQuestionCrossRef.class, Alarm.class, SentenceEntity.class, FolderEntity.class}, version = 11) // Tăng version lên 11 và thêm FolderEntity
public abstract class AppDatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();
    public abstract StudyPackageDao studyPackageDao();
    public abstract AlarmDao alarmDao();
    public abstract SentenceDao sentenceDao();
    public abstract FolderDao folderDao(); // Thêm FolderDao

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    // Thêm mainThreadExecutor vào AppDatabase
    public static final MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .addMigrations(MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11) // Thêm MIGRATION_10_11
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we added a new table, there's nothing else to do here.
        }
    };
    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we added a new table and a new column, there's nothing else to do here.
            database.execSQL("ALTER TABLE sentences ADD COLUMN folderId INTEGER NOT NULL DEFAULT 0");
        }
    };
}