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
import com.example.english_learning.model.TopicProgress;

import java.util.List;

public class TopicProgressAdapter extends RecyclerView.Adapter<TopicProgressAdapter.TopicViewHolder> {
    private List<TopicProgress> topicList;
    private Context context;

    public TopicProgressAdapter(Context context, List<TopicProgress> topicList) {
        this.context = context;
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic_progress, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicProgress topic = topicList.get(position);
        holder.tvTopicName.setText(topic.getTopicName());

        // Tính % tiến độ
        int progress = 0;
        if (topic.getTotalCount() != 0) {
            progress = (topic.getCompletedCount() * 100) / topic.getTotalCount();
        }
        holder.progressCircle.setProgress(progress);
        holder.tvProgressPercent.setText(progress + "%");
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, tvProgressPercent;
        ProgressBar progressCircle;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            progressCircle = itemView.findViewById(R.id.progressCircle);
            tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
        }
    }
}