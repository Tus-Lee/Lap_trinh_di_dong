package com.example.english_learning.ui;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_learning.R;
import com.example.english_learning.adapter.SentenceAdapter;
import com.example.english_learning.room.SentenceEntity;
import com.example.english_learning.viewmodel.SentenceViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MyPlaylistFragment extends Fragment implements AddSentenceDialogFragment.AddSentenceDialogListener {

    private FloatingActionButton fabAddPlaylist;
    private TextView tvTitle;
    private RecyclerView rvSentences;
    private SentenceViewModel sentenceViewModel;
    private SentenceAdapter sentenceAdapter;
    private int folderId;
    private ImageView imageBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_playlist, container, false);

        fabAddPlaylist = view.findViewById(R.id.fabAddPlaylist);
        tvTitle = view.findViewById(R.id.tvTitle);
        rvSentences = view.findViewById(R.id.rvSentences);
        imageBack = view.findViewById(R.id.imageBack);

        // Lấy folderId từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            folderId = bundle.getInt("folderId", -1); // -1 là giá trị mặc định nếu không tìm thấy folderId
            if (folderId == -1) {
                // Xử lý lỗi: không tìm thấy folderId
                tvTitle.setText("Lỗi: Không tìm thấy thư mục");
            }
        }

        // Khởi tạo SentenceViewModel
        sentenceViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((Application) requireContext().getApplicationContext())).get(SentenceViewModel.class);

        // Khởi tạo SentenceAdapter
        sentenceAdapter = new SentenceAdapter();

        // Thiết lập RecyclerView
        rvSentences.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSentences.setAdapter(sentenceAdapter);

        // Thêm sự kiện click cho FloatingActionButton
        fabAddPlaylist.setOnClickListener(v -> {
            // Hiển thị AddSentenceDialogFragment
            showAddSentenceDialog();
        });

        // Thêm sự kiện click cho imageBack
        imageBack.setOnClickListener(v -> {
            // Quay lại fragment trước đó (FolderFragment)
            getParentFragmentManager().popBackStack();
        });

        // Quan sát danh sách câu tiếng Anh từ SentenceViewModel theo folderId
        sentenceViewModel.getSentencesByFolderId(folderId).observe(getViewLifecycleOwner(), new Observer<List<SentenceEntity>>() {
            @Override
            public void onChanged(List<SentenceEntity> sentences) {
                // Cập nhật danh sách câu tiếng Anh trong SentenceAdapter
                sentenceAdapter.setSentences(sentences);
            }
        });

        return view;
    }

    private void showAddSentenceDialog() {
        AddSentenceDialogFragment dialog = new AddSentenceDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "AddSentenceDialogFragment");
    }

    @Override
    public void onFinishAddSentenceDialog(String sentence) {
        // Lưu câu vào database với folderId
        SentenceEntity sentenceEntity = new SentenceEntity(sentence, folderId, "english_learning_app");
        sentenceViewModel.insert(sentenceEntity);
    }
}