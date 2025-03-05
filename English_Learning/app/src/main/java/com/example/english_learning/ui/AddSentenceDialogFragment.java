package com.example.english_learning.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.english_learning.R;

public class AddSentenceDialogFragment extends DialogFragment {

    public interface AddSentenceDialogListener {
        void onFinishAddSentenceDialog(String sentence);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_sentence, null);

        EditText etSentence = view.findViewById(R.id.etSentence);

        builder.setView(view)
                .setPositiveButton("Xác nhận", (dialog, id) -> {
                    AddSentenceDialogListener listener = (AddSentenceDialogListener) getTargetFragment();
                    if (listener != null) {
                        String sentence = etSentence.getText().toString();
                        listener.onFinishAddSentenceDialog(sentence);
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> {
                    AddSentenceDialogFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}