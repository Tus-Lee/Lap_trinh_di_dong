package com.example.english_learning.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.StudyPackageAdapter;
import com.example.english_learning.repository.StudyPackageRepository;
import com.example.english_learning.room.PackageWithQuestions;
import com.example.english_learning.room.StudyPackageEntity;
import com.example.english_learning.room.QuestionEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StudyPackageFragment extends Fragment {

    private RecyclerView recyclerViewStudyPackages;
    private StudyPackageAdapter studyPackageAdapter;
    private StudyPackageRepository studyPackageRepository;
    private ImageView imageBack;
    private Button btnDeletePackages;
    private ProgressBar progressBarLoading;
    private int expectedPackageCount = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        studyPackageRepository = new StudyPackageRepository(getActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_package, container, false);

        recyclerViewStudyPackages = view.findViewById(R.id.recyclerViewStudyPackages);
        imageBack = view.findViewById(R.id.imageBack);
        btnDeletePackages = view.findViewById(R.id.btnDeletePackages);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);

        recyclerViewStudyPackages.setLayoutManager(new LinearLayoutManager(getContext()));

        // Hiển thị ProgressBar và ẩn RecyclerView
        progressBarLoading.setVisibility(View.VISIBLE);
        recyclerViewStudyPackages.setVisibility(View.GONE);

        // Lấy dữ liệu từ Room và hiển thị
        displayStudyPackages();

        // Xử lý sự kiện click cho imageBack
        imageBack.setOnClickListener(v -> {
            // Quay lại fragment trước đó
            getParentFragmentManager().popBackStack();
        });

        // Xử lý sự kiện click cho btnDeletePackages
        btnDeletePackages.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận xóa
            showConfirmDeleteDialog();
        });

        return view;
    }

    private void displayStudyPackages() {
        studyPackageRepository.getPackagesWithQuestions(new StudyPackageRepository.OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(List<PackageWithQuestions> packages) {
                // Nếu có dữ liệu, hiển thị RecyclerView
                studyPackageAdapter = new StudyPackageAdapter(packages, (studyPackage, questions) -> {
                    // Điều hướng đến PackageDetailFragment
                    navigateToPackageDetail(studyPackage, questions);
                });
                recyclerViewStudyPackages.setAdapter(studyPackageAdapter);
                // Ẩn ProgressBar và hiển thị RecyclerView
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewStudyPackages.setVisibility(View.VISIBLE);
            }
        });
    }

    private void navigateToPackageDetail(StudyPackageEntity studyPackage, List<QuestionEntity> questions) {
        // Tạo PackageDetailFragment và truyền dữ liệu
        // Truyền thêm tham số false vì không phải từ notification
        PackageDetailFragment packageDetailFragment = PackageDetailFragment.newInstance(
                studyPackage.getName(),
                studyPackage.getQuestionCount(),
                questions,
                studyPackage.getStudyPackageId(),
                false // Thêm tham số false ở đây
        );

        // Thay thế StudyPackageFragment bằng PackageDetailFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, packageDetailFragment);
        transaction.addToBackStack(null); // Thêm vào back stack để có thể quay lại
        transaction.commit();
    }

    private void showConfirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        TextView textViewWarning = dialogView.findViewById(R.id.textViewWarning);
        TextView textViewMessage = dialogView.findViewById(R.id.textViewMessage);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);

        //textViewWarning.setText("Bạn sẽ mất tất cả tiến trình học");
        textViewMessage.setText("Bạn có chắc là muốn xóa hay không?");

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        buttonConfirm.setOnClickListener(v -> {
            // Xóa tất cả các gói học
            studyPackageRepository.deleteAllPackages();
            studyPackageRepository.deleteAllPackageQuestionCrossRef();
            // Cập nhật lại danh sách
            displayStudyPackages();
            // Chuyển hướng về CoursesFragment
            replaceFragment(new CoursesFragment());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    // Hàm để thêm gói học mới với ngày tháng
    public void addStudyPackage(String packageName, int questionCount) {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Tạo StudyPackageEntity mới với ngày hiện tại
        StudyPackageEntity newPackage = new StudyPackageEntity(packageName, questionCount, currentDate);

        // Thêm gói học vào database
        studyPackageRepository.insert(newPackage);

        // Cập nhật lại danh sách
        displayStudyPackages();
    }

    public void setExpectedPackageCount(int expectedPackageCount) {
        this.expectedPackageCount = expectedPackageCount;
    }
}