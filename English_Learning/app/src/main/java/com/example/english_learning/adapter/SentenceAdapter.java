package com.example.english_learning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.room.SentenceEntity;

import java.util.ArrayList;
import java.util.List;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.SentenceViewHolder> {

    private List<SentenceEntity> sentences = new ArrayList<>();

    @NonNull
    @Override
    public SentenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sentence, parent, false);
        return new SentenceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceViewHolder holder, int position) {
        SentenceEntity currentSentence = sentences.get(position);
        holder.tvSentence.setText(currentSentence.getText());
    }

    @Override
    public int getItemCount() {
        return sentences.size();
    }

    public void setSentences(List<SentenceEntity> sentences) {
        this.sentences = sentences;
        notifyDataSetChanged();
    }

    static class SentenceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSentence;

        public SentenceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSentence = itemView.findViewById(R.id.tvSentence);
        }
    }
}