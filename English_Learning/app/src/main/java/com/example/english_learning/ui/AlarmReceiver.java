package com.example.english_learning.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.english_learning.R;
import com.example.english_learning.room.AppDatabase;
import com.example.english_learning.room.AlarmDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final String TAG = "AlarmReceiver";
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static int notificationId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("ALARM_ID", -1);
        String alarmTime = intent.getStringExtra("ALARM_TIME");
        int repeatCount = intent.getIntExtra("REPEAT_COUNT", 1);
        final int currentCount = intent.getIntExtra("CURRENT_COUNT", 0); // Biến currentCount được khai báo final

        Log.d(TAG, "onReceive: alarmId: " + alarmId + ", alarmTime: " + alarmTime + ", repeatCount: " + repeatCount + ", currentCount: " + currentCount);

        if (alarmId == -1) {
            Log.e(TAG, "Không tìm thấy ID báo thức!");
            return;
        }

        executorService.execute(() -> {
            AlarmDao alarmDao = AppDatabase.getDatabase(context).alarmDao();
            boolean isEnable = alarmDao.isAlarmEnabled(alarmId);

            if (isEnable) {
                showNotification(context, alarmTime);
                final int nextCount = currentCount + 1; // Tạo biến nextCount để lưu giá trị currentCount + 1
                if (repeatCount == -1 || nextCount < repeatCount) {
                    scheduleNextNotification(context, alarmId, alarmTime, repeatCount, nextCount); // Truyền nextCount vào scheduleNextNotification
                }
            } else {
                Log.d(TAG, "Báo thức đã bị tắt, không hiển thị thông báo.");
            }
        });
    }

    private void showNotification(Context context, String alarmTime) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Đã đến giờ học")
                .setContentText("Bây giờ là: " + (alarmTime != null ? alarmTime : "Không xác định"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notificationId++, builder.build());
            }
        } else {
            notificationManager.notify(notificationId++, builder.build());
        }
    }

    private void scheduleNextNotification(Context context, int alarmId, String alarmTime, int repeatCount, int currentCount) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarmId);
        intent.putExtra("ALARM_TIME", alarmTime);
        intent.putExtra("REPEAT_COUNT", repeatCount);
        intent.putExtra("CURRENT_COUNT", currentCount);

        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, flag);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long triggerTime = System.currentTimeMillis() + 30000; // 30 giây sau
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }
}