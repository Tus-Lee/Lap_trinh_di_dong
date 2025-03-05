package com.example.english_learning.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.english_learning.R;
import com.example.english_learning.room.FolderEntity;
import com.example.english_learning.viewmodel.FolderViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FolderMoreDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_FOLDER = "folder";
    private FolderEntity folder;
    private FolderViewModel folderViewModel;
    private EditFolderDialogListener editFolderDialogListener;

    public interface OnSavePlaylistListener {
        void onSavePlaylist(FolderEntity folder);
    }

    public interface EditFolderDialogListener {
        void onFinishEditFolderDialog(String folderName, int folderId);
    }

    public static FolderMoreDialogFragment newInstance(FolderEntity folder) {
        FolderMoreDialogFragment fragment = new FolderMoreDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOLDER, folder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            folder = (FolderEntity) getArguments().getSerializable(ARG_FOLDER);
        }
        folderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_folder_more, container, false);

        TextView tvEdit = view.findViewById(R.id.tvEdit);
        TextView tvSavePlaylist = view.findViewById(R.id.tvSavePlaylist);
        TextView tvDelete = view.findViewById(R.id.tvDelete);

        tvEdit.setOnClickListener(v -> {
            showEditFolderDialog();
            dismiss(); // Đóng dialog sau khi click
        });

        tvSavePlaylist.setOnClickListener(v -> {
            // Xử lý lưu playlist
            Log.d("FolderMoreDialog", "Save Playlist clicked"); // Thêm log này
            Fragment targetFragment = getTargetFragment();
            Log.d("FolderMoreDialog", "Target Fragment: " + targetFragment); // Thêm log này
            if (targetFragment instanceof OnSavePlaylistListener) {
                ((OnSavePlaylistListener) targetFragment).onSavePlaylist(folder);
            }
            dismiss(); // Đóng dialog sau khi click
        });

        tvDelete.setOnClickListener(v -> {
            // Xử lý xóa ở đây
            folderViewModel.delete(folder);
            Toast.makeText(getContext(), "Đã xóa thư mục", Toast.LENGTH_SHORT).show();
            dismiss(); // Đóng dialog sau khi click
        });

        return view;
    }

    private void showEditFolderDialog() {
        EditFolderDialogFragment dialog = EditFolderDialogFragment.newInstance(folder);
        dialog.show(getParentFragmentManager(), "EditFolderDialogFragment");
    }

    public void setEditFolderDialogListener(EditFolderDialogListener listener) {
        this.editFolderDialogListener = listener;
    }
}