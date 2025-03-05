package com.example.english_learning.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.english_learning.R;
import com.example.english_learning.databinding.ActivityMainBinding;
import com.example.english_learning.room.QuestionEntity;
import com.example.english_learning.viewmodel.StudyPackageViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ThemeChangeListener {

    ActivityMainBinding binding;
    Toolbar toolbar;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private StudyPackageViewModel studyPackageViewModel;
    private static final String TAG = "MainActivity";
    private boolean isHomeFragmentVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Áp dụng theme dựa trên SharedPreferences
        loadTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        studyPackageViewModel = new ViewModelProvider(this).get(StudyPackageViewModel.class);

        // Yêu cầu quyền SCHEDULE_EXACT_ALARM và POST_NOTIFICATIONS
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your app.
                        // Không cần Toast ở đây
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        // Không cần Toast ở đây
                    }
                }
        );

        // Kích hoạt chế độ toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        toolbar = binding.ToolBar;
        setSupportActionBar(toolbar);
        // Tắt hiển thị title của Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Kiểm tra xem Activity có đang được tái tạo lại hay không
        if (savedInstanceState == null) {
            // Đây là lần đầu tiên Activity được tạo
            // Kiểm tra xem có studyPackageId trong Intent hay không
            int studyPackageId = getIntent().getIntExtra("studyPackageId", -1);
            Log.d(TAG, "onCreate: studyPackageId: " + studyPackageId);
            if (studyPackageId != -1) {
                // Mở PackageDetailFragment với studyPackageId tương ứng
                // Kiểm tra xem có phải mở từ notification hay không
                boolean fromNotification = getIntent().getBooleanExtra("fromNotification", false);
                openPackageDetailFragment(studyPackageId, fromNotification);
            } else {
                // Nếu không có studyPackageId, mở fragment mặc định (ví dụ: HomeFragment)
                openHomeFragment();
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                openHomeFragment();
            } else if (itemId == R.id.courses) {
                replaceFragment(new CoursesFragment());
            } else if (itemId == R.id.schedule) {
                replaceFragment(new ScheduleFragment());
                // Kiểm tra và yêu cầu quyền SCHEDULE_EXACT_ALARM khi vào ScheduleFragment
                checkAndRequestScheduleExactAlarmPermission();
            } else if (itemId == R.id.setting) {
                replaceFragment(new SettingFragment());
            }
            return true;
        });
        // Kiểm tra và yêu cầu quyền POST_NOTIFICATIONS khi khởi động
        checkAndRequestPostNotificationsPermission();
    }

    private void loadTheme() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        // Sử dụng commitAllowingStateLoss() thay vì commit()
        fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.addToBackStack(null);
        // Cập nhật trạng thái HomeFragment
        if (fragment instanceof HomeFragment) {
            isHomeFragmentVisible = true;
        } else {
            isHomeFragmentVisible = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void checkAndRequestScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Nếu chưa có quyền, yêu cầu người dùng cấp quyền
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
            // Kiểm tra quyền sau khi đã kiểm tra canScheduleExactAlarms
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM);
            }
        }
    }

    private void checkAndRequestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void openPackageDetailFragment(int studyPackageId, boolean fromNotification) {
        studyPackageViewModel.getStudyPackageById(studyPackageId).observe(this, studyPackageEntity -> {
            if (studyPackageEntity != null) {
                // Lấy danh sách câu hỏi từ PackageWithQuestions
                studyPackageViewModel.getStudyPackageWithQuestions(studyPackageId).observe(this, packageWithQuestions -> {
                    if (packageWithQuestions != null) {
                        List<QuestionEntity> questions = packageWithQuestions.questions;
                        String packageName = studyPackageEntity.getName();
                        int questionCount = studyPackageEntity.getQuestionCount();
                        // Truyền thêm tham số fromNotification
                        PackageDetailFragment packageDetailFragment = PackageDetailFragment.newInstance(packageName, questionCount, questions, studyPackageId, fromNotification);
                        replaceFragment(packageDetailFragment);
                    }
                });
            }
        });
    }

    private void openHomeFragment() {
        // Kiểm tra xem HomeFragment đã hiển thị chưa
        if (!isHomeFragmentVisible) {
            replaceFragment(new HomeFragment());
        }
    }

    @Override
    public void onThemeChanged() {
        // Tái tạo Activity khi theme thay đổi
        recreate();
    }
}