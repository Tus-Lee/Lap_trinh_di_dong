package com.example.english_learning.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.FilterAdapter;
import com.example.english_learning.adapter.QuestionAdapter;
import com.example.english_learning.repository.QuestionRepository;
import com.example.english_learning.repository.StudyPackageRepository;
import com.example.english_learning.room.PackageWithQuestions;
import com.example.english_learning.room.QuestionEntity;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoursesFragment extends Fragment implements QuestionRepository.OnDataLoadedCallback, FilterAdapter.OnTopicClickListener {
    private SearchView searchView;
    private ImageView imageFilter;
    private RecyclerView recyclerViewTopics;
    private TextView textViewSelectAll;
    private BottomSheetDialog filterDialog;
    private FilterAdapter filterAdapter;
    private Button btnCreateCourse;
    private RecyclerView recyclerViewQuestions;
    private QuestionRepository questionRepository;
    private StudyPackageRepository studyPackageRepository;
    private QuestionAdapter questionAdapter;
    private List<QuestionEntity> allQuestions;
    private List<String> topics;
    private Set<String> selectedTopics = new HashSet<>();
    private boolean isSelectAll = false;
    private boolean isFiltered = false;
    private List<QuestionEntity> currentQuestions = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private ProgressBar progressBarLoading;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Khởi tạo QuestionRepository
        questionRepository = new QuestionRepository(getActivity().getApplication());
        // Khởi tạo StudyPackageRepository
        studyPackageRepository = new StudyPackageRepository(getActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        searchView = view.findViewById(R.id.searchView);
        imageFilter = view.findViewById(R.id.imageFilter);
        recyclerViewTopics = view.findViewById(R.id.recyclerViewTopics);
        textViewSelectAll = view.findViewById(R.id.textViewSelectAll);
        btnCreateCourse = view.findViewById(R.id.btnCreateCourse);
        recyclerViewQuestions = view.findViewById(R.id.recyclerViewQuestions);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);

        // Khởi tạo QuestionAdapter với OnItemClickListener
        questionAdapter = new QuestionAdapter(new ArrayList<>(), new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QuestionEntity question) {
                // Xử lý logic khi click vào câu hỏi
                // Ví dụ: Hiển thị thông tin chi tiết của câu hỏi
                Log.d("QuestionAdapter", "Câu hỏi được click: " + question.getText());
            }
        });

        // Đặt QuestionAdapter cho recyclerViewQuestions
        recyclerViewQuestions.setAdapter(questionAdapter);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        // Hiển thị ProgressBar và ẩn RecyclerView
        progressBarLoading.setVisibility(View.VISIBLE);
        recyclerViewQuestions.setVisibility(View.GONE);

        // Hiển thị câu hỏi từ Room
        displayQuestionsFromRoom();

        // Thiết lập SearchView
        setupSearchView();
        // Thiết lập Filter
        setupFilter();
        // Thiết lập Select All/Deselect All
        setupSelectAll();
        // Thiết lập nút tạo khóa học
        setupCreateCourseButton();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reset trạng thái isSelectAll khi fragment được hiển thị lại
        isSelectAll = false;
        textViewSelectAll.setText("Select All");
    }

    private void setupCreateCourseButton() {
        // Kiểm tra xem đã có gói học nào chưa
        studyPackageRepository.getPackagesWithQuestions(new StudyPackageRepository.OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(List<PackageWithQuestions> packages) {
                if (packages.isEmpty()) {
                    // Nếu chưa có gói học nào, hiển thị "Tạo khóa học"
                    btnCreateCourse.setText("Tạo khóa học");
                    btnCreateCourse.setOnClickListener(v -> createNewStudyPackage());
                } else {
                    // Nếu đã có gói học, hiển thị "Xem khóa học"
                    btnCreateCourse.setText("Xem khóa học");
                    btnCreateCourse.setOnClickListener(v -> navigateToStudyPackageFragment());
                }
            }
        });
    }

    private void createNewStudyPackage() {
        // Lấy số câu đã chọn
        List<QuestionEntity> selectedQuestions = new ArrayList<>(questionAdapter.getSelectedQuestions());
        int selectedCount = selectedQuestions.size();
        if (selectedCount > 0) {
            // Hiển thị dialog
            CreateCourseDialog dialog = new CreateCourseDialog(selectedCount, new CreateCourseDialog.OnConfirmListener() {
                @Override
                public void onConfirm(int dailyCount) {
                    // Xử lý khi người dùng nhấn Confirm trong dialog
                    Log.d("CoursesFragment", "Số câu học mỗi ngày: " + dailyCount);
                    // Lưu gói học vào Room
                    //String packageName = "Gói học "; // Tạo tên gói học
                    studyPackageRepository.insertStudyPackages(selectedQuestions, dailyCount, new StudyPackageRepository.OnPackagesInsertedCallback() {
                        @Override
                        public void onPackagesInserted(int numPackages) {
                            // Sau khi gói học được tạo xong, chuyển sang StudyPackageFragment
                            navigateToStudyPackageFragment(numPackages);
                        }
                    });
                }
            });
            dialog.show(getChildFragmentManager(), "CreateCourseDialog");
        } else {
            // Hiển thị thông báo
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 câu hỏi", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToStudyPackageFragment() {
        navigateToStudyPackageFragment(0);
    }

    private void navigateToStudyPackageFragment(int numPackages) {
        StudyPackageFragment studyPackageFragment = new StudyPackageFragment();
        studyPackageFragment.setExpectedPackageCount(numPackages);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, studyPackageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayQuestionsFromRoom() {
        questionRepository.getAllQuestions(this);
    }

    @Override
    public void onDataLoaded(List<QuestionEntity> questionEntities) {
        allQuestions = questionEntities;
        currentQuestions = new ArrayList<>(allQuestions);
        questionAdapter.setQuestions(currentQuestions);
        // Cập nhật lại danh sách topics sau khi dữ liệu được load
        setupFilter();
        // Ẩn ProgressBar và hiển thị RecyclerView
        progressBarLoading.setVisibility(View.GONE);
        recyclerViewQuestions.setVisibility(View.VISIBLE);
    }

    private void setupSearchView() {
        searchView.setQueryHint("Tìm kiếm câu hỏi");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn nút Enter
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng thay đổi văn bản
                performSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            // Nếu query rỗng, hiển thị lại tất cả câu hỏi
            currentQuestions = new ArrayList<>(allQuestions);
            questionAdapter.setFilteredQuestions(currentQuestions);
        } else {
            // Nếu query không rỗng, thực hiện tìm kiếm
            questionRepository.getQuestionsByText(query, new QuestionRepository.OnDataLoadedCallback() {
                @Override
                public void onDataLoaded(List<QuestionEntity> questionEntities) {
                    currentQuestions = questionEntities;
                    getActivity().runOnUiThread(() -> {
                        questionAdapter.setFilteredQuestions(currentQuestions);
                    });
                }
            });
        }
    }

    private void setupFilter() {
        // Lấy danh sách topics duy nhất từ Room
        topics = getUniqueTopicsFromRoom();

        // Xử lý sự kiện click vào imageFilter
        imageFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        // Nếu đang ở trạng thái đã lọc, bỏ lọc và hiển thị lại tất cả câu hỏi
        if (isFiltered) {
            performFilter(); // Gọi performFilter() để bỏ lọc
            return; // Thoát khỏi hàm để không hiển thị dialog
        }
        // Tạo dialog
        filterDialog = new BottomSheetDialog(getContext());
        filterDialog.setContentView(R.layout.dialog_filter_topics);

        // Ánh xạ các view trong dialog
        TextView textViewCancel = filterDialog.findViewById(R.id.textViewCancel);
        TextView textViewTitle = filterDialog.findViewById(R.id.textViewTitle);
        TextView textViewConfirm = filterDialog.findViewById(R.id.textViewConfirm); // Ánh xạ TextView Confirm
        RecyclerView recyclerViewFilterTopics = filterDialog.findViewById(R.id.recyclerViewFilterTopics); // Ánh xạ RecyclerView

        // Thay đổi text của textViewTitle
        textViewTitle.setText("Chọn 1 chủ đề bạn quan tâm");

        // Thiết lập FilterAdapter cho recyclerViewFilterTopics
        filterAdapter = new FilterAdapter(getContext(), topics, this);
        recyclerViewFilterTopics.setAdapter(filterAdapter);
        // Sử dụng FlexboxLayoutManager
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerViewFilterTopics.setLayoutManager(layoutManager);

        // Xử lý sự kiện click vào textViewCancel
        textViewCancel.setOnClickListener(v -> filterDialog.dismiss());

        // Xử lý sự kiện click vào textViewConfirm
        textViewConfirm.setOnClickListener(v -> {
            performFilter();
            filterDialog.dismiss();
        });

        // Hiển thị dialog
        filterDialog.show();
    }

    @Override
    public void onTopicClick(List<String> selectedTopics) {
        // Cập nhật danh sách selectedTopics với topic mới được chọn
        this.selectedTopics.clear();
        this.selectedTopics.addAll(selectedTopics);
    }

    private void performFilter() {
        if (selectedTopics.isEmpty() || isFiltered) {
            // Nếu không có topic nào được chọn HOẶC đang ở trạng thái đã lọc, hiển thị lại tất cả câu hỏi
            currentQuestions = new ArrayList<>(allQuestions);
            questionAdapter.setFilteredQuestions(currentQuestions);
            // Thay đổi hình ảnh thành ic_filter (chưa lọc)
            imageFilter.setImageResource(R.drawable.ic_filter);
            isFiltered = false;
            selectedTopics.clear();
        } else {
            // Nếu có topic được chọn, thực hiện lọc
            // Lấy topic đầu tiên trong danh sách selectedTopics
            String selectedTopic = selectedTopics.iterator().next();
            questionRepository.getQuestionsByTopics(selectedTopic, new QuestionRepository.OnDataLoadedCallback() {
                @Override
                public void onDataLoaded(List<QuestionEntity> questionEntities) {
                    currentQuestions = questionEntities;
                    getActivity().runOnUiThread(() -> {
                        questionAdapter.setFilteredQuestions(currentQuestions);
                        // Thay đổi hình ảnh thành ic_filter_selected (đã lọc)
                        imageFilter.setImageResource(R.drawable.ic_filter_active);
                        isFiltered = true;
                    });
                }
            });
        }
    }

    private List<String> getUniqueTopicsFromRoom() {
        Set<String> uniqueTopics = new HashSet<>();
        if (allQuestions != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            for (QuestionEntity questionEntity : allQuestions) {
                try {
                    // Lấy chuỗi JSON từ trường 'topicsJson' của QuestionEntity
                    String topicsJson = questionEntity.getTopicsJson();
                    // Chuyển đổi chuỗi JSON thành List<String>
                    List<String> topics = gson.fromJson(topicsJson, listType);
                    // Thêm các topic vào Set để đảm bảo tính duy nhất
                    uniqueTopics.addAll(topics);
                } catch (Exception e) {
                    Log.e("CoursesFragment", "Error getting unique topics", e);
                }
            }
        }
        return new ArrayList<>(uniqueTopics);
    }

    private void setupSelectAll() {
        // Đảm bảo text luôn là "Select All" khi mới vào fragment
        textViewSelectAll.setText("Select All");
        textViewSelectAll.setOnClickListener(v -> {
            if (textViewSelectAll.getText().equals("Deselect All")) {
                questionAdapter.deselectAll();
                textViewSelectAll.setText("Select All");
            } else {
                // Sử dụng currentQuestions thay vì allQuestions
                questionAdapter.selectAll(currentQuestions);
                textViewSelectAll.setText("Deselect All");
            }
        });
    }
}