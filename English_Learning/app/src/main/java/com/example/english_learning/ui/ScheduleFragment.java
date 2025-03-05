package com.example.english_learning.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.english_learning.R;

public class ScheduleFragment extends Fragment {
    private Button btnAlarm, btnCalendar;
    private FrameLayout frameContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        btnAlarm = view.findViewById(R.id.btnAlarm);
        btnCalendar = view.findViewById(R.id.btnCalendar);
        frameContainer = view.findViewById(R.id.frameContainer);

        // Mặc định hiển thị AlarmFragment và chọn btnAlarm
        replaceFragment(new AlarmFragment());
        btnAlarm.setSelected(true);
        btnCalendar.setSelected(false);

        btnAlarm.setOnClickListener(v -> {
            replaceFragment(new AlarmFragment());
            btnAlarm.setSelected(true);
            btnCalendar.setSelected(false);
        });

        btnCalendar.setOnClickListener(v -> {
            replaceFragment(new CalendarFragment());
            btnAlarm.setSelected(false);
            btnCalendar.setSelected(true);
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.commit();
    }
}