package com.example.english_learning.ui;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.FolderAdapter;
import com.example.english_learning.room.FolderEntity;
import com.example.english_learning.room.SentenceEntity;
import com.example.english_learning.viewmodel.FolderViewModel;
import com.example.english_learning.viewmodel.SentenceViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FolderFragment extends Fragment implements AddFolderDialogFragment.AddFolderDialogListener, FolderAdapter.OnFolderClickListener, FolderAdapter.OnFolderMoreClickListener, FolderMoreDialogFragment.OnSavePlaylistListener, FolderMoreDialogFragment.EditFolderDialogListener {

    private ImageView ivAddFolder;
    private RecyclerView rvFolders;
    private FolderAdapter folderAdapter;
    private FolderViewModel folderViewModel;
    private SentenceViewModel sentenceViewModel;
    private TextView tvEdit;
    private TextView tvDelete;
    private boolean isEditMode = false; // Biến để theo dõi trạng thái chỉnh sửa
    private ImageView imageBack;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private FolderEntity currentFolder; // Biến thành viên để lưu trữ FolderEntity

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FolderFragment", "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        ivAddFolder = view.findViewById(R.id.ivAddFolder);
        rvFolders = view.findViewById(R.id.rvFolders);
        tvEdit = view.findViewById(R.id.tvEdit);
        tvDelete = view.findViewById(R.id.tvDelete);
        imageBack = view.findViewById(R.id.imageBack);

        // Ẩn TextView xóa ban đầu
        tvDelete.setVisibility(View.GONE);

        // Khởi tạo FolderViewModel
        folderViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((Application) requireContext().getApplicationContext())).get(FolderViewModel.class);
        sentenceViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((Application) requireContext().getApplicationContext())).get(SentenceViewModel.class);

        // Khởi tạo FolderAdapter
        folderAdapter = new FolderAdapter(this, requireContext(), this);

        // Thiết lập RecyclerView
        rvFolders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFolders.setAdapter(folderAdapter);

        // Thêm sự kiện click cho ImageView
        ivAddFolder.setOnClickListener(v -> {
            showAddFolderDialog();
        });

        // Thêm sự kiện click cho TextView "Sửa"/"Xong"
        tvEdit.setOnClickListener(v -> {
            isEditMode = !isEditMode; // Đảo ngược trạng thái
            folderAdapter.setEditMode(isEditMode); // Bật/tắt chế độ chỉnh sửa trong Adapter
            tvEdit.setText(isEditMode ? "Xong" : "Sửa"); // Thay đổi text của tvEdit
            if (!isEditMode) {
                tvDelete.setVisibility(View.GONE); // Ẩn tvDelete khi chuyển sang trạng thái "Sửa"
            } else {
                if (folderAdapter.getSelectedFolders().size() > 0) {
                    tvDelete.setVisibility(View.VISIBLE);
                }
            }
        });

        // Thêm sự kiện click cho TextView xóa
        tvDelete.setOnClickListener(v -> {
            List<FolderEntity> selectedFolders = folderAdapter.getSelectedFolders();
            if (selectedFolders.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn thư mục để xóa", Toast.LENGTH_SHORT).show();
            } else {
                for (FolderEntity folder : selectedFolders) {
                    folderViewModel.delete(folder);
                }
                folderAdapter.clearSelectedFolders(); // Xóa danh sách đã chọn
                tvDelete.setVisibility(View.GONE); // Ẩn nút xóa sau khi xóa
            }
        });

        // Thêm sự kiện click cho imageBack
        imageBack.setOnClickListener(v -> {
            // Quay lại fragment trước đó (HomeFragment)
            getParentFragmentManager().popBackStack();
        });

        // Quan sát danh sách thư mục từ FolderViewModel
        folderViewModel.getAllFolders().observe(getViewLifecycleOwner(), new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(List<FolderEntity> folders) {
                Log.d("FolderFragment", "folderViewModel.getAllFolders().observe onChanged() called");
                folderAdapter.setFolders(folders);
            }
        });

        return view;
    }

    private void showAddFolderDialog() {
        Log.d("FolderFragment", "showAddFolderDialog() called");
        AddFolderDialogFragment dialog = new AddFolderDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "AddFolderDialogFragment");
    }

    private void showFolderMoreDialog(FolderEntity folder) {
        Log.d("FolderFragment", "showFolderMoreDialog called for folder: " + folder.getName());
        FolderMoreDialogFragment dialog = FolderMoreDialogFragment.newInstance(folder);
        dialog.setEditFolderDialogListener(this);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "FolderMoreDialogFragment"); // Sử dụng getParentFragmentManager()
    }

    @Override
    public void onFinishAddFolderDialog(String folderName) {
        Log.d("FolderFragment", "onFinishAddFolderDialog() called with folderName: " + folderName);
        FolderEntity folderEntity = new FolderEntity(folderName);
        folderViewModel.insert(folderEntity);
    }

    @Override
    public void onFolderClick(FolderEntity folder) {
        Log.d("FolderFragment", "onFolderClick() called for folder: " + folder.getName());
        MyPlaylistFragment myPlaylistFragment = new MyPlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("folderId", folder.getId());
        myPlaylistFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, myPlaylistFragment);
        transaction.addToBackStack("FolderFragment");
        transaction.commit();
    }

    @Override
    public void onFolderSelectedCountChanged(int count) {
        Log.d("FolderFragment", "onFolderSelectedCountChanged() called with count: " + count);
        // Xử lý khi số lượng thư mục được chọn thay đổi
        if (isEditMode) {
            tvDelete.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onFolderMoreClick(FolderEntity folder) {
        // Xử lý khi click vào icon 3 chấm
        Log.d("FolderFragment", "onFolderMoreClick called for folder: " + folder.getName()); // Thêm log này
        showFolderMoreDialog(folder);
    }

    @Override
    public void onSavePlaylist(FolderEntity folder) {
        Log.d("FolderFragment", "onSavePlaylist called for folder: " + folder.getName()); // Thêm log này
        currentFolder = folder; // Lưu trữ folder vào biến thành viên
        // Kiểm tra quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Nếu Android 10 trở lên, không cần WRITE_EXTERNAL_STORAGE
            Log.d("FolderFragment", "Android 10 or higher, no need WRITE_EXTERNAL_STORAGE permission");
            savePlaylist(folder);
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("FolderFragment", "WRITE_EXTERNAL_STORAGE permission not granted, requesting...");
                // Nếu chưa có quyền, yêu cầu quyền
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                Log.d("FolderFragment", "WRITE_EXTERNAL_STORAGE permission granted, saving playlist...");
                // Nếu đã có quyền, tiếp tục lưu playlist
                savePlaylist(folder);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("FolderFragment", "onRequestPermissionsResult() called");
        Log.d("FolderFragment", "onRequestPermissionsResult() called with requestCode: " + requestCode);
        if (grantResults.length > 0) {
            Log.d("FolderFragment", "grantResults[0]: " + grantResults[0]);
        }
        if (permissions.length > 0) {
            Log.d("FolderFragment", "permissions[0]: " + permissions[0]);
        }
        if (currentFolder != null) {
            Log.d("FolderFragment", "currentFolder.getName(): " + currentFolder.getName());
        } else {
            Log.d("FolderFragment", "currentFolder is null");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FolderFragment", "WRITE_EXTERNAL_STORAGE permission granted by user.");
                // Quyền đã được cấp, tiếp tục lưu playlist
                savePlaylist(currentFolder); // Sử dụng currentFolder thay vì folder
            } else {
                Log.d("FolderFragment", "WRITE_EXTERNAL_STORAGE permission denied by user.");
                // Quyền bị từ chối, hiển thị thông báo
                Toast.makeText(requireContext(), "Không thể lưu playlist vì thiếu quyền truy cập bộ nhớ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePlaylist(FolderEntity folder) {
        Log.d("FolderFragment", "savePlaylist() called");
        Log.d("FolderFragment", "savePlaylist() called for folder: " + folder.getName());
        // Lấy danh sách câu theo folderId
        sentenceViewModel.getSentencesByFolderId(folder.getId()).observe(getViewLifecycleOwner(), new Observer<List<SentenceEntity>>() {
            @Override
            public void onChanged(List<SentenceEntity> sentenceEntities) {
                Log.d("FolderFragment", "sentenceViewModel.getSentencesByFolderId().observe onChanged() called");
                Log.d("FolderFragment", "Sentences received: " + sentenceEntities.size()); // Thêm log này
                if (sentenceEntities.isEmpty()) {
                    Log.d("FolderFragment", "No sentences found for this folder.");
                    // Nếu không có câu nào, hiển thị thông báo
                    Toast.makeText(requireContext(), "Hiện tại không có dữ liệu để lưu", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("FolderFragment", "Sentences found, creating and downloading JSON file...");
                    // Nếu có câu, tạo và tải file JSON
                    createAndDownloadJsonFile(folder.getName(), sentenceEntities);
                }
            }
        });
    }

    private void createAndDownloadJsonFile(String folderName, List<SentenceEntity> sentenceEntities) {
        Log.d("FolderFragment", "createAndDownloadJsonFile() called");
        // Tạo đối tượng Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Log.d("FolderFragment", "Converting to JSON...");
        // Chuyển danh sách câu thành chuỗi JSON
        String json = gson.toJson(sentenceEntities);
        Log.d("FolderFragment", "JSON conversion complete.");

        Log.d("FolderFragment", "Creating file name...");
        // Tạo tên file
        String fileName = folderName + ".json";
        Log.d("FolderFragment", "File name created: " + fileName);

        Log.d("FolderFragment", "Getting downloads directory...");
        // Lấy thư mục Downloads
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.d("FolderFragment", "Downloads directory: " + downloadsDir.getAbsolutePath());

        Log.d("FolderFragment", "Creating file object...");
        // Tạo file trong thư mục Downloads
        File file = new File(downloadsDir, fileName);
        Log.d("FolderFragment", "File object created: " + file.getAbsolutePath());

        try {
            Log.d("FolderFragment", "Writing to file...");
            // Ghi chuỗi JSON vào file
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
            Log.d("FolderFragment", "File writing complete.");

            // Hiển thị thông báo thành công
            Toast.makeText(requireContext(), "Đã lưu playlist vào " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d("FolderFragment", "File saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("FolderFragment", "Error saving file", e);
            // Hiển thị thông báo lỗi
            Toast.makeText(requireContext(), "Lỗi khi lưu playlist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishEditFolderDialog(String folderName, int folderId) {
        Log.d("FolderFragment", "onFinishEditFolderDialog() called with folderName: " + folderName + ", folderId: " + folderId);
        // Cập nhật tên thư mục trong cơ sở dữ liệu
        FolderEntity folder = new FolderEntity(folderName);
        folder.setId(folderId);
        folderViewModel.update(folder);
    }
}