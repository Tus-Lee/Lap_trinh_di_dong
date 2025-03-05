package com.example.english_learning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.room.PackageWithQuestions;
import com.example.english_learning.room.QuestionEntity;
import com.example.english_learning.room.StudyPackageEntity;

import java.util.List;

public class StudyPackageAdapter extends RecyclerView.Adapter<StudyPackageAdapter.StudyPackageViewHolder> {
    private List<PackageWithQuestions> packagesWithQuestions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(StudyPackageEntity studyPackage, List<QuestionEntity> questions);
    }

    public StudyPackageAdapter(List<PackageWithQuestions> packagesWithQuestions, OnItemClickListener listener) {
        this.packagesWithQuestions = packagesWithQuestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudyPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_package, parent, false);
        return new StudyPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyPackageViewHolder holder, int position) {
        PackageWithQuestions packageWithQuestions = packagesWithQuestions.get(position);
        StudyPackageEntity studyPackage = packageWithQuestions.studyPackage;
        holder.textViewPackageName.setText(studyPackage.getName());
        holder.textViewQuestionCount.setText(" Số câu: " + studyPackage.getQuestionCount());
        holder.textViewDate.setText(studyPackage.getDate());

        // Cập nhật trạng thái hoàn thành
        if (studyPackage.isCompleted()) {
            holder.textViewCompleteStatus.setVisibility(View.VISIBLE);
            holder.textViewCompleteStatus.setText("Đã hoàn thành");
        } else {
            holder.textViewCompleteStatus.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(studyPackage, packageWithQuestions.questions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packagesWithQuestions.size();
    }

    public static class StudyPackageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPackageName;
        TextView textViewQuestionCount;
        TextView textViewDate;
        TextView textViewCompleteStatus;

        public StudyPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPackageName = itemView.findViewById(R.id.textViewPackageName);
            textViewQuestionCount = itemView.findViewById(R.id.textViewQuestionCount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewCompleteStatus = itemView.findViewById(R.id.textViewCompleteStatus);
        }
    }
}