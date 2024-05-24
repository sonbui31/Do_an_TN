package com.example.quanlychitieu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.widget.TextView;

public class Progress_Dialog {
    Context context;
    Dialog dialog;
    Handler handler;
    public Progress_Dialog(Context context) {
        this.context = context;
    }
    public void ShowDilag(String title){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textview_progress_dialog = dialog.findViewById(R.id.textview_progress_dialog);
        textview_progress_dialog.setText(title);
        dialog.create();
        dialog.show();

//        // Đặt thời gian tự động ẩn Dialog
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                HideDialog();
//            }
//        }, durationInSeconds * 1000L);  // Chuyển đổi giây thành mili giây

    }
    public void HideDialog(){
        dialog.dismiss();
    }
}
