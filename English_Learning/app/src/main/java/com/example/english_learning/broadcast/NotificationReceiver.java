package com.example.english_learning.broadcast;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.english_learning.R;
import com.example.english_learning.room.StudyPackageEntity;
import com.example.english_learning.repository.StudyPackageRepository;
import com.example.english_learning.ui.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL_ID = "study_package_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Notification received");
        int studyPackageId = intent.getIntExtra("studyPackageId", -1);
        if (studyPackageId == -1) {
            Log.e(TAG, "studyPackageId not found in intent");
            return;
        }

        // Tạo Intent để mở MainActivity khi nhấp vào thông báo
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("studyPackageId", studyPackageId);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, studyPackageId, mainIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo và hiển thị thông báo
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.createNotification(
                "Nhắc nhở học tập",
                "Đã đến lúc học bài rồi!"
        );
        builder.setContentIntent(pendingIntent); // Thêm PendingIntent vào thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(studyPackageId, builder.build());
        }

        // Cập nhật lastNotificationTime và lên lịch thông báo tiếp theo
        Application application = (Application) context.getApplicationContext();
        StudyPackageRepository repository = new StudyPackageRepository(application);
        repository.getPackagesWithQuestions(packages -> {
            for (int i = 0; i < packages.size(); i++) {
                if (packages.get(i).studyPackage.getStudyPackageId() == studyPackageId) {
                    StudyPackageEntity studyPackage = packages.get(i).studyPackage;
                    long notificationInterval = studyPackage.getNotificationInterval();
                    long currentTime = System.currentTimeMillis();
                    studyPackage.setLastNotificationTime(currentTime);
                    repository.updateStudyPackage(studyPackage); // Thay đổi ở đây
                    scheduleNextNotification(context, notificationInterval, studyPackageId);
                    break;
                }
            }
        });
    }

    private void scheduleNextNotification(Context context, long notificationInterval, int studyPackageId) {
        Log.d(TAG, "Scheduling next notification");
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("studyPackageId", studyPackageId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, studyPackageId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        long nextNotificationTime = currentTime + notificationInterval;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextNotificationTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextNotificationTime, pendingIntent);
        }
    }
}