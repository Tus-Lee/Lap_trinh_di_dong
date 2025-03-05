package com.example.english_learning.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.QuestionAdapter;
import com.example.english_learning.broadcast.NotificationReceiver;
import com.example.english_learning.repository.QuestionRepository;
import com.example.english_learning.room.QuestionEntity;
import com.example.english_learning.room.StudyPackageEntity;
import com.example.english_learning.viewmodel.StudyPackageViewModel;

import java.util.ArrayList;
import java.util.List;

public class PackageDetailFragment extends Fragment {

    private static final String ARG_PACKAGE_NAME = "package_name";
    private static final String ARG_QUESTION_COUNT = "question_count";
    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_STUDY_PACKAGE_ID = "study_package_id";
    public static final String ARG_FROM_NOTIFICATION = "from_notification";

    private String packageName;
    private int questionCount;
    private List<QuestionEntity> questions;
    private int studyPackageId;
    private boolean fromNotification = false;

    private TextView textViewPackageName;
    private RecyclerView recyclerViewQuestions;
    private QuestionAdapter questionAdapter;
    private ImageView imageBack;
    private Button btnSelectLevel;
    private StudyPackageViewModel studyPackageViewModel;
    private TextView textViewCancelAlarm;
    private TextView textViewDifficultyLevel;
    private StudyPackageEntity currentStudyPackage;
    private PendingIntent pendingIntent;
    private TextView textViewComplete;
    private QuestionRepository questionRepository;

    public static PackageDetailFragment newInstance(String packageName, int questionCount, List<QuestionEntity> questions, int studyPackageId, boolean fromNotification) {
        PackageDetailFragment fragment = new PackageDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PACKAGE_NAME, packageName);
        args.putInt(ARG_QUESTION_COUNT, questionCount);
        args.putParcelableArrayList(ARG_QUESTIONS, new ArrayList<>(questions));
        args.putInt(ARG_STUDY_PACKAGE_ID, studyPackageId);
        args.putBoolean(ARG_FROM_NOTIFICATION, fromNotification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        questionRepository = new QuestionRepository(getActivity().getApplication());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageName = getArguments().getString(ARG_PACKAGE_NAME);
            questionCount = getArguments().getInt(ARG_QUESTION_COUNT);
            questions = getArguments().getParcelableArrayList(ARG_QUESTIONS, QuestionEntity.class);
            studyPackageId = getArguments().getInt(ARG_STUDY_PACKAGE_ID);
            fromNotification = getArguments().getBoolean(ARG_FROM_NOTIFICATION, false);
        }
        studyPackageViewModel = new ViewModelProvider(this).get(StudyPackageViewModel.class);
        Intent intent = new Intent(requireContext(), NotificationReceiver.class);
        intent.putExtra("studyPackageId", studyPackageId);
        pendingIntent = PendingIntent.getBroadcast(requireContext(), studyPackageId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_detail, container, false);

        textViewPackageName = view.findViewById(R.id.textViewPackageName);
        recyclerViewQuestions = view.findViewById(R.id.recyclerViewQuestions);
        imageBack = view.findViewById(R.id.imageBack);
        btnSelectLevel = view.findViewById(R.id.btnSelectLevel);
        textViewCancelAlarm = view.findViewById(R.id.textViewCancelAlarm);
        textViewDifficultyLevel = view.findViewById(R.id.textViewDifficultyLevel);
        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewCancelAlarm.setVisibility(View.GONE);
        textViewDifficultyLevel.setText("Độ khó: ");

        imageBack.setOnClickListener(v -> {
            // Always navigate to StudyPackageFragment when imageBack is clicked
            replaceFragment(new StudyPackageFragment());
        });

        textViewPackageName.setText(packageName);

        if (questions != null) {
            QuestionAdapter.OnItemClickListener itemClickListener = question -> Toast.makeText(requireContext(), "Clicked: " + question.getText(), Toast.LENGTH_SHORT).show();
            questionAdapter = new QuestionAdapter(questions, itemClickListener);
            recyclerViewQuestions.setAdapter(questionAdapter);
            recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        studyPackageViewModel.getStudyPackageById(studyPackageId).observe(getViewLifecycleOwner(), studyPackageEntity -> {
            if (studyPackageEntity != null) {
                currentStudyPackage = studyPackageEntity;
                updateUI(currentStudyPackage);
            }
        });
        btnSelectLevel.setOnClickListener(v -> showSelectLevelDialog());
        textViewCancelAlarm.setOnClickListener(v -> cancelAlarm());
        textViewComplete.setOnClickListener(v -> showCompleteConfirmationDialog());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerViewQuestions.setAdapter(null);
    }

    private void showSelectLevelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_level, null);
        builder.setView(dialogView);

        RadioGroup radioGroupLevels = dialogView.findViewById(R.id.radioGroupLevels);
        RadioButton radioEasy = dialogView.findViewById(R.id.radioEasy);
        RadioButton radioMedium = dialogView.findViewById(R.id.radioMedium);
        RadioButton radioHard = dialogView.findViewById(R.id.radioHard);

        AlertDialog dialog = builder.create();

        radioGroupLevels.setOnCheckedChangeListener((group, checkedId) -> {
            String difficultyLevel = "";
            long notificationInterval = 0;
            if (checkedId == R.id.radioEasy) {
                difficultyLevel = "easy";
                notificationInterval = 3600 * 1000 * 3; // 3 tiếng
                Toast.makeText(requireContext(), "Bạn đã chọn Dễ", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioMedium) {
                difficultyLevel = "medium";
                notificationInterval = 3600 * 1000; // 15 giây
                Toast.makeText(requireContext(), "Bạn đã chọn Trung Bình", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioHard) {
                difficultyLevel = "hard";
                notificationInterval = 60 * 15 * 1000; // 10 giây
                Toast.makeText(requireContext(), "Bạn đã chọn Khó", Toast.LENGTH_SHORT).show();
            }
            // Lưu độ khó và thời gian vào database
            if (!difficultyLevel.isEmpty()) {
                if (currentStudyPackage != null) {
                    currentStudyPackage.setDifficultyLevel(difficultyLevel);
                    currentStudyPackage.setNotificationInterval(notificationInterval);
                    currentStudyPackage.setLastNotificationTime(System.currentTimeMillis());
                    currentStudyPackage.setAlarmSet(true);
                    studyPackageViewModel.updateStudyPackage(currentStudyPackage);
                    // Lên lịch thông báo
                    scheduleNotification(notificationInterval);
                    // Hiển thị TextView "Hủy báo thức"
                    textViewCancelAlarm.setVisibility(View.VISIBLE);
                    // Hiển thị độ khó đã chọn
                    textViewDifficultyLevel.setText("Độ khó: " + difficultyLevel);
                }
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void scheduleNotification(long notificationInterval) {
        // Lấy AlarmManager
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Lấy thời gian hiện tại
        long currentTime = System.currentTimeMillis();

        // Tính thời gian cho lần thông báo đầu tiên
        long notificationTime = currentTime + notificationInterval;

        // Lên lịch thông báo
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
        if (currentStudyPackage != null) {
            currentStudyPackage.setAlarmSet(false);
            // Xóa độ khó khi hủy báo thức
            currentStudyPackage.setDifficultyLevel(null);
            studyPackageViewModel.updateStudyPackage(currentStudyPackage);
        }
        textViewCancelAlarm.setVisibility(View.GONE);
        textViewDifficultyLevel.setText("Độ khó: ");
    }

    private void updateUI(StudyPackageEntity studyPackage) {
        if (studyPackage.isAlarmSet()) {
            textViewCancelAlarm.setVisibility(View.VISIBLE);
            // Nếu báo thức đã được đặt, hủy báo thức cũ và đặt lại
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            scheduleNotification(studyPackage.getNotificationInterval());
        } else {
            textViewCancelAlarm.setVisibility(View.GONE);
        }
        if (studyPackage.getDifficultyLevel() != null && !studyPackage.getDifficultyLevel().isEmpty()) {
            textViewDifficultyLevel.setText("Độ khó: " + studyPackage.getDifficultyLevel());
        } else {
            textViewDifficultyLevel.setText("Độ khó: ");
        }
        // Cập nhật UI cho trạng thái hoàn thành
        if (studyPackage.isCompleted()) {
            textViewComplete.setText("Đã hoàn thành");
            textViewComplete.setBackgroundResource(R.drawable.rounded_green_bg);
            textViewComplete.setClickable(false);
        } else {
            textViewComplete.setText("Chưa hoàn thành");
            textViewComplete.setBackgroundResource(R.drawable.rounded_red_bg);
            textViewComplete.setClickable(true);
        }
    }

    private void showCompleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận hoàn thành");
        builder.setMessage("Bạn có chắc chắn muốn hoàn thành gói học này?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            if (currentStudyPackage != null) {
                currentStudyPackage.setCompleted(true);
                studyPackageViewModel.updateStudyPackage(currentStudyPackage);
                // Đánh dấu tất cả câu hỏi trong gói là đã hoàn thành
                markAllQuestionsAsCompleted(questions);
                updateUI(currentStudyPackage);
            }
        });
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Phương thức mới để đánh dấu tất cả câu hỏi là đã hoàn thành
    private void markAllQuestionsAsCompleted(List<QuestionEntity> questions) {
        if (questions != null && !questions.isEmpty()) {
            for (QuestionEntity question : questions) {
                question.setSuccess(true); // Sử dụng setSuccess() thay vì setCompleted()
            }
            questionRepository.updateAll(questions);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }
}
