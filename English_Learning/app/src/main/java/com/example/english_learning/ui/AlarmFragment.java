package com.example.english_learning.ui;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.AlarmAdapter;
import com.example.english_learning.room.Alarm;
import com.example.english_learning.room.AlarmDao;
import com.example.english_learning.room.AppDatabase;
import com.example.english_learning.viewmodel.AlarmViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class AlarmFragment extends Fragment implements AlarmAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList;
    private FloatingActionButton btnAddAlarm;
    private Button btnTestNotification;
    private static final String CHANNEL_ID = "alarm_channel";
    private AlarmViewModel alarmViewModel;
    private AlarmManager alarmManager;
    private AlarmDao alarmDao;
    private ExecutorService executorService;
    private static final String DEFAULT_ALARM_TIME = "07:00 AM";
    private boolean isDefaultAlarmAdded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        recyclerView = view.findViewById(R.id.recyclerAlarm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        alarmList = new ArrayList<>();
        // Lấy AlarmDao và ExecutorService từ AppDatabase
        alarmDao = AppDatabase.getDatabase(getContext()).alarmDao();
        executorService = AppDatabase.databaseWriteExecutor;
        adapter = new AlarmAdapter(getContext(), alarmList, alarmDao, executorService);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // Khởi tạo AlarmViewModel
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        // Quan sát danh sách báo thức từ ViewModel
        alarmViewModel.getAllAlarms().observe(getViewLifecycleOwner(), alarms -> {
            alarmList.clear();
            alarmList.addAll(alarms);
            adapter.notifyDataSetChanged();
            // Kiểm tra và thêm báo thức mặc định nếu cần
            if (alarms.isEmpty() && !isDefaultAlarmAdded) {
                addDefaultAlarm();
                isDefaultAlarmAdded = true;
            }
            if (alarms.size() == 1) {
                adapter.setIsDefaultAlarm(true);
            } else {
                adapter.setIsDefaultAlarm(false);
            }
        });

        // Thêm chức năng khi nhấn vào nút FloatingActionButton
        btnAddAlarm = view.findViewById(R.id.btnAddAlarm);
        btnAddAlarm.setOnClickListener(v -> showTimePicker());

        // Thêm chức năng khi nhấn vào nút Test Notification
        btnTestNotification = view.findViewById(R.id.btnTestNotification);
        btnTestNotification.setOnClickListener(v -> sendTestNotification());

        createNotificationChannel();
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        return view;
    }

    private void addDefaultAlarm() {
        Alarm defaultAlarm = new Alarm(DEFAULT_ALARM_TIME, true, 1);
        alarmViewModel.insert(defaultAlarm);
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    String timeFormat = (selectedHour >= 12) ? "PM" : "AM";
                    int hourIn12Format = (selectedHour > 12) ? selectedHour - 12 : selectedHour;
                    hourIn12Format = (hourIn12Format == 0) ? 1
                            : hourIn12Format;
                    String time = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, timeFormat);

                    // Kiểm tra xem báo thức mới có trùng với báo thức mặc định hay không
                    if (alarmList.size() == 1 && time.equals(DEFAULT_ALARM_TIME)) {
                        Toast.makeText(getContext(), "Không thể thêm báo thức trùng với báo thức mặc định", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Thêm báo thức mới vào database
                    Alarm newAlarm = new Alarm(time, true, 1);
                    alarmViewModel.insert(newAlarm);
                },
                hour,
                minute,
                false
        );
        timePickerDialog.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Channel";
            String description = "Channel for alarm notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendTestNotification() {
        // Tạo Intent để mở ứng dụng khi nhấn vào thông báo
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Test Notification")
                .setContentText("This is a test notification from AlarmFragment")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(1, builder.build());
            } else {
                Toast.makeText(getContext(), "Notification is disabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public void onItemClick(Alarm alarm) {
        // Kiểm tra xem có phải là báo thức duy nhất không
        if (alarmList.size() == 1) {
            Toast.makeText(getContext(), "Không thể xóa! Bạn cần ít nhất 1 báo thức", Toast.LENGTH_SHORT).show();
        } else {
            // Xóa báo thức khỏi database
            alarmViewModel.delete(alarm);
        }
    }
}