package com.example.english_learning.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private Context context;
    private List<String> topics;
    private OnTopicClickListener listener;
    private String selectedTopic = null; // Thay đổi Set thành String để chỉ chọn 1 topic

    public interface OnTopicClickListener {
        void onTopicClick(List<String> selectedTopics);
    }

    public FilterAdapter(Context context, List<String> topics, OnTopicClickListener listener) {
        this.context = context;
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String topic = topics.get(position);
        holder.textFilter.setText(topic);

        // Kiểm tra xem topic có được chọn hay không
        if (topic.equals(selectedTopic)) {
            holder.textFilter.setBackgroundResource(R.drawable.selected_topic_background);
            holder.textFilter.setTextColor(Color.WHITE);
        } else {
            holder.textFilter.setBackgroundResource(R.drawable.background_filter_item);
            holder.textFilter.setTextColor(Color.BLACK);
        }

        holder.textFilter.setOnClickListener(v -> {
            // Cập nhật selectedTopic
            if (selectedTopic != null && selectedTopic.equals(topic)) {
                selectedTopic = null; // Bỏ chọn nếu nhấn lại vào topic đã chọn
            } else {
                selectedTopic = topic; // Chọn topic mới
            }

            // Cập nhật lại background và màu chữ cho tất cả các item
            notifyDataSetChanged();

            // Thông báo cho listener về topic đã chọn
            List<String> selectedTopics = new ArrayList<>();
            if (selectedTopic != null) {
                selectedTopics.add(selectedTopic);
            }
            listener.onTopicClick(selectedTopics);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {
        TextView textFilter;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            textFilter = itemView.findViewById(R.id.textFilter);
        }
    }

    // Phương thức để lấy danh sách các chủ đề đã chọn
    public List<String> getSelectedTopics() {
        List<String> selectedTopics = new ArrayList<>();
        if (selectedTopic != null) {
            selectedTopics.add(selectedTopic);
        }
        return selectedTopics;
    }
}