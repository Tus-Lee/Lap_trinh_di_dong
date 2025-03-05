package com.example.english_learning.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.english_learning.R;

public class CreateCourseDialog extends DialogFragment {

    private int selectedCount;
    private OnConfirmListener listener;

    public interface OnConfirmListener {
        void onConfirm(int dailyCount);
    }

    public CreateCourseDialog(int selectedCount, OnConfirmListener listener) {
        this.selectedCount = selectedCount;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_course, null);

        TextView txtSelectedCount = view.findViewById(R.id.txtSelectedCount);
        Spinner spinnerDailyCount = view.findViewById(R.id.spinnerDailyCount);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        txtSelectedCount.setText("Số câu đã chọn: " + selectedCount);

        // Danh sách số câu học mỗi ngày
        Integer[] dailyCounts = {1, 2, 3, 4, 5};

        // Tạo ArrayAdapter với màu chữ luôn rõ ràng
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), R.layout.spinner_item, dailyCounts) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.BLACK); // Màu chữ dropdown khi chưa mở
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.BLACK); // Màu chữ trong danh sách
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDailyCount.setAdapter(adapter);

        btnConfirm.setOnClickListener(v -> {
            int dailyCount = (int) spinnerDailyCount.getSelectedItem();
            listener.onConfirm(dailyCount);
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}
