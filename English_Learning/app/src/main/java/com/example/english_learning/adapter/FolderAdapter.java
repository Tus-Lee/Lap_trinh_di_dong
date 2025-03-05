package com.example.english_learning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.room.FolderEntity;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<FolderEntity> folders = new ArrayList<>();
    private OnFolderClickListener listener;
    private OnFolderMoreClickListener moreListener;
    private boolean isEditMode = false;
    private List<FolderEntity> selectedFolders = new ArrayList<>();
    private Context context;

    public FolderAdapter(OnFolderClickListener listener, Context context, OnFolderMoreClickListener moreListener) {
        this.listener = listener;
        this.context = context;
        this.moreListener = moreListener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        FolderEntity currentFolder = folders.get(position);
        holder.tvFolderName.setText(currentFolder.getName());

        holder.rbSelect.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        // Cập nhật trạng thái checked của RadioButton dựa trên selectedFolders
        holder.rbSelect.setChecked(selectedFolders.contains(currentFolder));

        // Loại bỏ setOnClickListener cũ
        holder.rbSelect.setOnClickListener(null);

        // Thêm setOnClickListener mới
        holder.rbSelect.setOnClickListener(v -> {
            // Kiểm tra xem folder đã được chọn hay chưa
            boolean isSelected = selectedFolders.contains(currentFolder);

            // Thay đổi trạng thái checked của RadioButton
            holder.rbSelect.setChecked(!isSelected);

            // Cập nhật danh sách selectedFolders
            if (!isSelected) {
                selectedFolders.add(currentFolder);
            } else {
                selectedFolders.remove(currentFolder);
            }

            // Gọi phương thức để cập nhật số lượng thư mục đã chọn
            if (listener != null) {
                listener.onFolderSelectedCountChanged(selectedFolders.size());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (!isEditMode && listener != null) {
                listener.onFolderClick(currentFolder);
            }
        });

        // Thêm sự kiện click cho ivMore
        holder.ivMore.setOnClickListener(v -> {
            if (moreListener != null) {
                moreListener.onFolderMoreClick(currentFolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void setFolders(List<FolderEntity> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    public FolderEntity getFolderAt(int position) {
        return folders.get(position);
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        if (!isEditMode) {
            selectedFolders.clear();
        }
        notifyDataSetChanged();
    }

    public List<FolderEntity> getSelectedFolders() {
        return selectedFolders;
    }

    public void clearSelectedFolders() {
        selectedFolders.clear();
        notifyDataSetChanged();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFolderName;
        private RadioButton rbSelect;
        private ImageView ivMore; // Thêm ImageView

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
            rbSelect = itemView.findViewById(R.id.rbSelect);
            ivMore = itemView.findViewById(R.id.ivMore); // Ánh xạ ImageView
        }
    }

    public interface OnFolderClickListener {
        void onFolderClick(FolderEntity folder);

        void onFolderSelectedCountChanged(int count);
    }

    public interface OnFolderMoreClickListener {
        void onFolderMoreClick(FolderEntity folder);
    }
}