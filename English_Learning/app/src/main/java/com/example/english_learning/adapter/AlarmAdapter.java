package com.example.english_learning.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.room.Alarm;
import com.example.english_learning.room.AlarmDao;
import com.example.english_learning.ui.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;
    private OnItemClickListener listener;
    private AlarmDao alarmDao;
    private ExecutorService executorService;
    private Context context;
    private boolean isDefaultAlarm = false;

    public AlarmAdapter(Context context, List<Alarm> alarmList, AlarmDao alarmDao, ExecutorService executorService) {
        this.context = context;
        this.alarmList = alarmList;
        this.alarmDao = alarmDao;
        this.executorService = executorService;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setIsDefaultAlarm(boolean isDefaultAlarm) {
        this.isDefaultAlarm = isDefaultAlarm;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm currentAlarm = alarmList.get(position);
        holder.tvTime.setText(currentAlarm.getTime());
        holder.switchAlarm.setChecked(currentAlarm.isEnable());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private Switch switchAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvAlarmTime);
            switchAlarm = itemView.findViewById(R.id.switchAlarm);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showEditDialog(alarmList.get(position));
                }
            });

            switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Alarm alarm = alarmList.get(position);
                    alarm.setEnable(isChecked);
                    executorService.execute(() -> alarmDao.update(alarm));

                    if (isChecked) {
                        setAlarm(context, alarm);
                    } else {
                        cancelAlarm(context, alarm);
                    }
                }
            });
        }
    }

    private void showEditDialog(Alarm alarm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_alarm, null);
        builder.setView(dialogView);

        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView btnSave = dialogView.findViewById(R.id.btnSave);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        // Khởi tạo Spinner
        String[] repeatOptions = {"1 lần", "2 lần", "3 lần", "5 lần", "Unlimited"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, repeatOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(adapter);

        // Set mặc định là 1 lần nếu là báo thức mới
        if (alarm.getRepeatCount() == 0) {
            spinnerRepeat.setSelection(0); // Chọn "1 lần"
            alarm.setRepeatCount(1);
        } else {
            // Set số lần lặp lại hiện tại của báo thức
            spinnerRepeat.setSelection(getSpinnerPosition(alarm.getRepeatCount()));
        }

        // Set thời gian hiện tại của báo thức vào TimePicker
        String currentTime = alarm.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(currentTime));
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String newTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                    hour % 12 == 0 ? 12 : hour % 12,
                    minute,
                    hour < 12 ? "AM" : "PM");
            alarm.setTime(newTime);

            String selectedRepeat = spinnerRepeat.getSelectedItem().toString();
            alarm.setRepeatCount(getRepeatCount(selectedRepeat));

            executorService.execute(() -> {
                alarmDao.update(alarm);
            });
            setAlarm(context, alarm);
            Toast.makeText(context, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(alarm);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        // Truyền ID báo thức và repeatCount vào Intent
        intent.putExtra("ALARM_ID", alarm.getId());
        intent.putExtra("ALARM_TIME", alarm.getTime());
        intent.putExtra("REPEAT_COUNT", alarm.getRepeatCount());

        // Sử dụng FLAG_IMMUTABLE khi tạo PendingIntent
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, flag);

        long triggerTime = convertTimeToMillis(alarm.getTime());
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    private void cancelAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        // Sử dụng FLAG_IMMUTABLE khi tạo PendingIntent
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, flag);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private long convertTimeToMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Calendar now = Calendar.getInstance(); // Lấy thời gian hiện tại
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(sdf.parse(time));

            // Đặt ngày giống ngày hiện tại
            alarmTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
            alarmTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
            alarmTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

            // Nếu thời gian đã qua trong ngày, đặt báo thức cho ngày hôm sau
            if (alarmTime.before(now) || alarmTime.equals(now)) {
                alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            }

            return alarmTime.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    private int getRepeatCount(String selectedRepeat) {
        switch (selectedRepeat) {
            case "1 lần":
                return 1;
            case "2 lần":
                return 2;
            case "3 lần":
                return 3;
            case "5 lần":
                return 5;
            case "Unlimited":
                return -1; // Hoặc một giá trị đặc biệt khác để biểu thị "Unlimited"
            default:
                return 1; // Mặc định là 1 lần
        }
    }

    private int getSpinnerPosition(int repeatCount) {
        switch (repeatCount) {
            case 1:
                return 0; // "1 lần"
            case 2:
                return 1; // "2 lần"
            case 3:
                return 2; // "3 lần"
            case 5:
                return 3; // "5 lần"
            case -1:
                return 4; // "Unlimited"
            default:
                return 0; // Mặc định là "1 lần"
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Alarm alarm);
    }
}