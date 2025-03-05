package com.example.english_learning.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.room.QuestionEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<QuestionEntity> questions;
    private List<QuestionEntity> filteredQuestions;
    private OnItemClickListener listener;
    private Set<QuestionEntity> selectedQuestions = new HashSet<>();

    public QuestionAdapter(List<QuestionEntity> questions, OnItemClickListener listener) {
        this.questions = questions;
        this.filteredQuestions = new ArrayList<>(questions);
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(QuestionEntity question);
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionEntity question = filteredQuestions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return filteredQuestions.size();
    }

    public void setQuestions(List<QuestionEntity> questions) {
        this.questions = questions;
        this.filteredQuestions = new ArrayList<>(questions);
        notifyDataSetChanged();
    }

    public void setFilteredQuestions(List<QuestionEntity> filteredQuestions) {
        this.filteredQuestions = filteredQuestions;
        notifyDataSetChanged();
    }

    public Set<QuestionEntity> getSelectedQuestions() {
        return selectedQuestions;
    }

    public void selectAll(List<QuestionEntity> questions) {
        selectedQuestions.clear();
        selectedQuestions.addAll(questions);
        notifyDataSetChanged();
    }

    public void deselectAll() {
        selectedQuestions.clear();
        notifyDataSetChanged();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewQuestion;
        private CardView cardView;
        private ImageView imageViewSuccess;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.questionText);
            cardView = itemView.findViewById(R.id.cardView);
            imageViewSuccess = itemView.findViewById(R.id.imageViewSuccess);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    QuestionEntity question = filteredQuestions.get(position);
                    if (selectedQuestions.contains(question)) {
                        selectedQuestions.remove(question);
                    } else {
                        selectedQuestions.add(question);
                    }
                    notifyItemChanged(position);
                    listener.onItemClick(question);
                }
            });
        }

        public void bind(QuestionEntity question) {
            textViewQuestion.setText((getAdapterPosition() + 1) + ". " + question.getText());

            if (question.isSuccess()) {
                imageViewSuccess.setVisibility(View.VISIBLE);
            } else {
                imageViewSuccess.setVisibility(View.GONE);
            }

            // Đảm bảo màu thay đổi phù hợp cho cả chế độ sáng và tối
            if (selectedQuestions.contains(question)) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.card_selected));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.card_default));
            }
        }

    }
}