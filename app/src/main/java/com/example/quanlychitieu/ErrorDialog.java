package com.example.quanlychitieu;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ErrorDialog extends DialogFragment {

    private String title;
    private String message;

    // Khởi tạo DialogFragment với title và message được truyền vào
    public ErrorDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_error_dialog, null);

        TextView titleTextView = view.findViewById(R.id.dialog_title);
        TextView messageTextView = view.findViewById(R.id.dialog_message);
        Button positiveButton = view.findViewById(R.id.positive_button);

        titleTextView.setText(title);
        messageTextView.setText(message);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);

        // Hiển thị Dialog giữa màn hình
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);
        }

        return dialog;
    }
}
