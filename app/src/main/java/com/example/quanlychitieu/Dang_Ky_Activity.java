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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Dang_Ky_Activity extends AppCompatActivity {
    private EditText edt_name, edt_email, edt_pass;
    private AppCompatButton btn_dang_ky;
    private TextView tv_dangnhap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
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
        edt_name = findViewById(R.id.edt_name);
        btn_dang_ky = findViewById(R.id.btn_dang_ky);
        tv_dangnhap = findViewById(R.id.tv_dangnhap);
    }
    private void click(){
        btn_dang_ky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dang_ky();
            }
        });
        tv_dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dang_Ky_Activity.this, Dang_Nhap_Activity.class));
            }
        });
    }

    private void dang_ky(){
        String name = edt_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String pass = edt_pass.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email không được để trống!", Toast.LENGTH_SHORT).show();
        } else if (!email.contains("@gmail.com")) {
            Toast.makeText(this, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete()){
                        FirebaseUser user = auth.getCurrentUser();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
                        if (user != null){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", name);
                            hashMap.put("email", email);
                            hashMap.put("idUser", user.getUid());
                            hashMap.put("imageUrl", "default");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(Dang_Ky_Activity.this, Dang_Nhap_Activity.class));
                                        finish();
                                    }
                                }
                            });
                        }
                    } else {
                        // Đăng ký thất bại
                        ErrorDialog dialog = new ErrorDialog("Thông báo!", "Đăng ký tài khoản thất bại!");
                        dialog.show(getSupportFragmentManager(), "Thông báo!");
                    }
                }
            });
        }

    }
}