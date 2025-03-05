package com.example.english_learning.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.ProgressAdapter;
import com.example.english_learning.model.Question;
import com.example.english_learning.repository.QuestionRepository;
import com.example.english_learning.repository.StudyPackageRepository;
import com.example.english_learning.room.PackageWithQuestions;
import com.example.english_learning.room.QuestionEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private CardView cardProgress;
    private TextView tvProgressTitle;
    private TextView tvProgress;
    private ProgressBar progressBar;
    private CardView btnNavigate;
    private CardView cardMyPlaylist;
    private RecyclerView recyclerViewProgress;
    private ProgressAdapter progressAdapter;
    private ProgressBar loadingProgressBar;
    private LinearLayout topicProgressLayout;
    private ImageView ivExpandCollapse;

    private QuestionRepository questionRepository;
    private StudyPackageRepository studyPackageRepository;

    private boolean isDataLoaded = false;
    private boolean isExpanded = false; // Biến để theo dõi trạng thái mở rộng/thu gọn

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        questionRepository = new QuestionRepository(getActivity().getApplication());
        studyPackageRepository = new StudyPackageRepository(getActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cardProgress = view.findViewById(R.id.cardProgress);
        tvProgressTitle = view.findViewById(R.id.tvProgressTitle);
        tvProgress = view.findViewById(R.id.tvProgress);
        progressBar = view.findViewById(R.id.progressBar);
        btnNavigate = view.findViewById(R.id.btnNavigate);
        cardMyPlaylist = view.findViewById(R.id.cardMyPlaylist);
        recyclerViewProgress = view.findViewById(R.id.recyclerTopicProgress);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        topicProgressLayout = view.findViewById(R.id.topicProgressLayout);
        ivExpandCollapse = view.findViewById(R.id.ivExpandCollapse);

        recyclerViewProgress.setLayoutManager(new LinearLayoutManager(getContext()));

        // Hiển thị loading khi bắt đầu
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Tải dữ liệu từ JSON và sau khi tải xong, gọi checkUserProgress()
        loadQuestionsFromJsonIfNeeded(this::checkUserProgress);

        cardMyPlaylist.setOnClickListener(v -> replaceFragment(new FolderFragment()));

        // Thêm listener cho ivExpandCollapse
        ivExpandCollapse.setOnClickListener(v -> toggleRecyclerViewVisibility());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Nếu dữ liệu đã được tải, gọi checkUserProgress()
        if (isDataLoaded) {
            checkUserProgress();
        }
    }

    private void toggleRecyclerViewVisibility() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            // Hiển thị tất cả các topic
            ivExpandCollapse.setImageResource(R.drawable.ic_arrow_up);
            recyclerViewProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            recyclerViewProgress.requestLayout();
        } else {
            // Chỉ hiển thị 3 topic đầu tiên
            ivExpandCollapse.setImageResource(R.drawable.ic_arrow_down);
            recyclerViewProgress.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.topic_item_height) * 3;
            recyclerViewProgress.requestLayout();
        }
    }

    private void checkUserProgress() {
        studyPackageRepository.getPackagesWithQuestions(new StudyPackageRepository.OnDataLoadedCallback() {
            @Override
            public void onDataLoaded(List<PackageWithQuestions> packages) {
                int totalQuestionsInAllPackages = 0;
                int totalQuestionsInCompletedPackages = 0;
                List<QuestionEntity> allQuestionsInPackages = new ArrayList<>();
                Map<String, Integer> topicTotalQuestions = new HashMap<>();
                Map<String, Integer> topicLearnedQuestions = new HashMap<>();
                List<String> allTopics = new ArrayList<>();
                List<String> filteredTopics = new ArrayList<>();

                // Tính tổng số câu hỏi trong tất cả các gói học và số câu hỏi trong các gói học đã hoàn thành
                for (PackageWithQuestions packageWithQuestions : packages) {
                    totalQuestionsInAllPackages += packageWithQuestions.studyPackage.getQuestionCount();
                    if (packageWithQuestions.studyPackage.isCompleted()) {
                        totalQuestionsInCompletedPackages += packageWithQuestions.studyPackage.getQuestionCount();
                    }
                    allQuestionsInPackages.addAll(packageWithQuestions.questions);
                }
                // Lấy danh sách tất cả các chủ đề từ tất cả các câu hỏi
                questionRepository.getAllQuestions(new QuestionRepository.OnDataLoadedCallback() {
                    @Override
                    public void onDataLoaded(List<QuestionEntity> allQuestions) {
                        for (QuestionEntity question : allQuestions) {
                            List<String> topics = question.getTopics();
                            for (String topic : topics) {
                                if (!allTopics.contains(topic)) {
                                    allTopics.add(topic);
                                }
                            }
                        }
                        // Tính toán tiến độ cho từng chủ đề
                        for (String topic : allTopics) {
                            final int[] topicTotal = {0};
                            final int[] topicLearned = {0};

                            // Lấy tổng số câu hỏi cho chủ đề hiện tại
                            questionRepository.getCountByTopic(topic, count -> {
                                topicTotal[0] = count;
                                topicTotalQuestions.put(topic, topicTotal[0]);

                                // Lấy số câu hỏi đã học cho chủ đề hiện tại
                                questionRepository.getCompletedCountByTopic(topic, learnedCount -> {
                                    topicLearned[0] = learnedCount;
                                    topicLearnedQuestions.put(topic, topicLearned[0]);
                                    if (topicLearned[0] > 0) {
                                        filteredTopics.add(topic);
                                    }
                                    // Cập nhật adapter khi đã có đủ thông tin cho chủ đề này
                                    if (topic.equals(allTopics.get(allTopics.size() - 1))) {
                                        // Sắp xếp các topic theo thứ tự phần trăm hoàn thành giảm dần
                                        List<Map.Entry<String, Double>> sortedTopics = new ArrayList<>();
                                        for (String filteredTopic : filteredTopics) {
                                            int total = topicTotalQuestions.get(filteredTopic);
                                            int learned = topicLearnedQuestions.get(filteredTopic);
                                            double progress = total > 0 ? (double) learned / total : 0;
                                            sortedTopics.add(new HashMap.SimpleEntry<>(filteredTopic, progress));
                                        }
                                        Collections.sort(sortedTopics, (o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));

                                        // Lấy ra 3 topic đầu tiên
                                        List<String> top3Topics = new ArrayList<>();
                                        for (int i = 0; i < Math.min(3, sortedTopics.size()); i++) {
                                            top3Topics.add(sortedTopics.get(i).getKey());
                                        }

                                        // Tạo adapter với danh sách đã sắp xếp
                                        Map<String, Integer> sortedTopicTotalQuestions = new HashMap<>();
                                        Map<String, Integer> sortedTopicLearnedQuestions = new HashMap<>();
                                        for (String filteredTopic : filteredTopics) {
                                            sortedTopicTotalQuestions.put(filteredTopic, topicTotalQuestions.get(filteredTopic));
                                            sortedTopicLearnedQuestions.put(filteredTopic, topicLearnedQuestions.get(filteredTopic));
                                        }
                                        progressAdapter = new ProgressAdapter(getContext(), sortedTopicTotalQuestions, sortedTopicLearnedQuestions);
                                        recyclerViewProgress.setAdapter(progressAdapter);
                                        // Ẩn loading khi đã có dữ liệu
                                        loadingProgressBar.setVisibility(View.GONE);
                                        isDataLoaded = true;
                                        // Gọi toggleRecyclerViewVisibility() để hiển thị 3 topic đầu tiên
                                        toggleRecyclerViewVisibility();
                                    }
                                });
                            });
                        }
                        // Nếu không có chủ đề nào, ẩn loading
                        if (allTopics.isEmpty()) {
                            loadingProgressBar.setVisibility(View.GONE);
                            isDataLoaded = true;
                        }
                    }
                });

                if (packages.isEmpty()) {
                    // Nếu không có gói học nào, hiển thị thông báo và ẩn thanh tiến độ
                    tvProgress.setText("Chưa có khóa học");
                    progressBar.setVisibility(View.GONE);
                    btnNavigate.setOnClickListener(v -> replaceFragment(new CoursesFragment()));
                    // Ẩn loading khi không có gói học
                    loadingProgressBar.setVisibility(View.GONE);
                    isDataLoaded = true;
                } else {
                    // Hiển thị tiến độ
                    tvProgress.setText(totalQuestionsInCompletedPackages + "/" + totalQuestionsInAllPackages + " câu đã học");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(totalQuestionsInAllPackages);
                    progressBar.setProgress(totalQuestionsInCompletedPackages);
                    btnNavigate.setOnClickListener(v -> replaceFragment(new StudyPackageFragment()));
                }
            }
        });
    }

    private void loadQuestionsFromJsonIfNeeded(Runnable callback) {
        new Thread(() -> {
            if (questionRepository.getCount() == 0) {
                try {
                    String json = loadJSONFromAsset(getContext(), "questions.json");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Question>>() {
                    }.getType();
                    List<Question> questions = gson.fromJson(json, listType);

                    List<QuestionEntity> questionEntities = new ArrayList<>();
                    for (Question question : questions) {
                        String topicsJson = gson.toJson(question.getTopics());
                        QuestionEntity questionEntity = new QuestionEntity(question.getText(), topicsJson, question.getIndex(), false);
                        questionEntities.add(questionEntity);
                    }
                    questionRepository.insertAll(questionEntities);
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error loading questions from JSON", e);
                }
            }
            // Gọi callback sau khi đã tải xong dữ liệu từ JSON (hoặc nếu đã có dữ liệu trong Room)
            if (callback != null) {
                getActivity().runOnUiThread(callback);
            }
        }).start();
    }

    public String loadJSONFromAsset(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}