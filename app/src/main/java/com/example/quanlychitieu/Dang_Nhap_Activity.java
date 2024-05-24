package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Dang_Nhap_Activity extends AppCompatActivity {

    private EditText edt_email, edt_pass;
    private AppCompatButton btn_quen_mat_khau, btn_dang_nhap;
    private TextView tv_dangky;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhxa();
        click();
    }

    private void anhxa(){
        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        btn_quen_mat_khau = findViewById(R.id.btn_quen_mat_khau);
        btn_dang_nhap = findViewById(R.id.btn_dang_nhap);
        tv_dangky = findViewById(R.id.tv_dangky);
    }

    private void click(){
        btn_dang_nhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dang_nhap();
            }
        });
        tv_dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dang_Nhap_Activity.this, Dang_Ky_Activity.class));

            }
        });

        btn_quen_mat_khau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuenMatKhau();
            }


        });
    }
    private void showQuenMatKhau() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quen_mat_khau, null);
        builder.setView(dialogView);

        // Lấy ra các button trong dialog
        EditText edt_email = dialogView.findViewById(R.id.edt_email);
        AppCompatButton cancelButton = dialogView.findViewById(R.id.btn_huy);
        AppCompatButton sendButton = dialogView.findViewById(R.id.btn_send);

        // Xử lý sự kiện khi click vào nút "Hủy"
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng dialog
                alertDialog.dismiss();
            }
        });

        // Xử lý sự kiện khi click vào nút "Gửi"
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = edt_email.getText().toString();
                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(Dang_Nhap_Activity.this, "Email không được để trống!", Toast.LENGTH_SHORT).show();
                } else if (!emailAddress.contains("@gmail.com")) {
                    Toast.makeText(Dang_Nhap_Activity.this, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show();
                } else {

                    auth.sendPasswordResetEmail(emailAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            alertDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ErrorDialog dialog = new ErrorDialog("Thông báo!", "Gửi link đặt lại mật khẩu thất bại! Có thể Email " + emailAddress + " chưa được đăng ký.");
                            dialog.show(getSupportFragmentManager(), "Thông báo!");
                        }
                    });


                }
            }
        });

        // Tạo và hiển thị AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void dang_nhap(){
        String email = edt_email.getText().toString().trim();
        String pass = edt_pass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email không được để trống!", Toast.LENGTH_SHORT).show();
        } else if (!email.contains("@gmail.com")) {
            Toast.makeText(this, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(Dang_Nhap_Activity.this, Home_Activity.class);
                    startActivity(intent);
                    finishAffinity();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Đăng nhập thất bại
                    ErrorDialog dialog = new ErrorDialog("Thông báo!", "Đăng nhập thất bại!");
                    dialog.show(getSupportFragmentManager(), "Thông báo!");
                }
            });
        }
    }
}