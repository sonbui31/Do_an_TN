package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Person_Fragment extends Fragment {
    private View view;
    private ConstraintLayout log_out, my_account, danh_muc;
    private ImageView img_profile;
    private TextView tv_name_profile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.person_fragment, container, false);

        anhxa();
        click();
        set_profile();

        return view;
    }
    private void anhxa(){
        log_out = view.findViewById(R.id.log_out);
        my_account = view.findViewById(R.id.my_account);
        danh_muc = view.findViewById(R.id.danh_muc);
        img_profile = view.findViewById(R.id.img_profile);
        tv_name_profile = view.findViewById(R.id.tv_name_profile);

    }

    private void click(){
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Dang_Nhap_Activity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });

        danh_muc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, new Danh_Muc_Fragment()).commit();
                startActivity(new Intent(view.getContext(), Activity_Danh_Muc.class));
            }
        });
        my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new Account_Fragment()).commit();
            }
        });
    }

    private void set_profile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User users = snapshot.getValue(User.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                // Sử dụng thư viện Glide để tải và hiển thị ảnh từ URL
                Glide.with(view.getContext().getApplicationContext())
                        .load(imageUrl)
                        .circleCrop()
                        .into(img_profile);

                if (users != null){
                    tv_name_profile.setText(users.getName());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
