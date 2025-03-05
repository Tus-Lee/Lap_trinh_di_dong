package com.example.english_learning.ui;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.english_learning.R;
import com.example.english_learning.viewmodel.FolderViewModel;

public class AddFolderDialogFragment extends DialogFragment {

    private FolderViewModel folderViewModel;
    private EditText etFolderName;
    private String folderName;

    public interface AddFolderDialogListener {
        void onFinishAddFolderDialog(String folderName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo FolderViewModel
        folderViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((Application) requireContext().getApplicationContext())).get(FolderViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_folder, null);

        etFolderName = view.findViewById(R.id.etFolderName);

        // Quan sát kết quả kiểm tra tên thư mục
        folderViewModel.getIsFolderNameExistsResult().observe(this, exists -> {
            if (exists) {
                // Tên thư mục đã tồn tại, hiển thị thông báo lỗi
                Toast.makeText(requireContext(), "Đã có thư mục cùng tên tồn tại. Xin vui lòng chọn tên khác", Toast.LENGTH_SHORT).show();
            } else {
                // Tên thư mục chưa tồn tại, tạo thư mục mới
                AddFolderDialogListener listener = (AddFolderDialogListener) getTargetFragment();
                if (listener != null) {
                    listener.onFinishAddFolderDialog(folderName);
                }
                dismiss();
            }
        });

        builder.setView(view)
                .setPositiveButton("Xác nhận", (dialog, id) -> {
                    folderName = etFolderName.getText().toString().trim();
                    if (TextUtils.isEmpty(folderName)) {
                        Toast.makeText(requireContext(), "Vui lòng nhập tên thư mục", Toast.LENGTH_SHORT).show();
                    } else {
                        // Gọi checkFolderName() để bắt đầu kiểm tra
                        folderViewModel.checkFolderName(folderName);
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> {
                    AddFolderDialogFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}