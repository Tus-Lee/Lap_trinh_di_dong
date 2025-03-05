package com.example.english_learning.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.english_learning.R;
import com.example.english_learning.room.FolderEntity;
import com.example.english_learning.viewmodel.FolderViewModel;

public class EditFolderDialogFragment extends DialogFragment {
    private static final String ARG_FOLDER = "folder";
    private FolderEntity folder;
    private FolderViewModel folderViewModel;
    private EditText etFolderName;

    public static EditFolderDialogFragment newInstance(FolderEntity folder) {
        EditFolderDialogFragment fragment = new EditFolderDialogFragment();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_folder, null);
        etFolderName = view.findViewById(R.id.etFolderName);
        etFolderName.setText(folder.getName());

        builder.setView(view)
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newName = etFolderName.getText().toString();
                        if (!newName.isEmpty()) {
                            folder.setName(newName);
                            folderViewModel.update(folder);
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditFolderDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}