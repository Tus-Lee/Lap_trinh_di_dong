package com.example.english_learning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {
    private List<String> topics;
    private Map<String, Integer> totalQuestions;
    private Map<String, Integer> learnedQuestions;
    private Context context;

    public ProgressAdapter(Context context, Map<String, Integer> totalQuestions, Map<String, Integer> learnedQuestions) {
        this.context = context;
        this.topics = new ArrayList<>(totalQuestions.keySet());
        this.totalQuestions = totalQuestions;
        this.learnedQuestions = learnedQuestions;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng context để inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        // Kiểm tra xem topics có rỗng không
        if (topics.isEmpty()) {
            return; // Không làm gì nếu không có dữ liệu
        }
        // Kiểm tra xem position có hợp lệ không
        if (position < 0 || position >= topics.size()) {
            return; // Không làm gì nếu position không hợp lệ
        }
        String topic = topics.get(position);
        // Kiểm tra xem totalQuestions có chứa topic không
        if (!totalQuestions.containsKey(topic)) {
            return; // Không làm gì nếu không có dữ liệu cho topic này
        }
        int total = totalQuestions.get(topic);
        int learned = learnedQuestions.getOrDefault(topic, 0);
        int progressPercentage = (total > 0) ? (int) ((double) learned / total * 100) : 0;

        holder.tvTopic.setText(topic);
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(progressPercentage);
        holder.tvProgress.setText(learned + "/" + total);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopic, tvProgress;
        ProgressBar progressBar;

        ProgressViewHolder(View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tvTopicName);
            tvProgress = itemView.findViewById(R.id.tvProgressPercent);
            progressBar = itemView.findViewById(R.id.progressCircle);
        }
    }

    public void updateData(Map<String, Integer> totalQuestions, Map<String, Integer> learnedQuestions) {
        this.totalQuestions = totalQuestions;
        this.learnedQuestions = learnedQuestions;
        this.topics = new ArrayList<>(totalQuestions.keySet()); // Cập nhật danh sách topics
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }
}