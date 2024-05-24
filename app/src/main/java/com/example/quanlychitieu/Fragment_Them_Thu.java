package com.example.quanlychitieu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Them_Thu extends Fragment {
    private View view;
    private RCV_Danh_Muc_Adapter rcvDanhMucAdapter;
    private RecyclerView rcv_them_thu;
    private ArrayList<Danh_Muc> list;
    private ImageView img_icon;
    private AppCompatButton btn_them;
    private String icon_Danh_Muc, idUser, id_danh_muc, name_danh_muc;
    private EditText edt_so_tien, edt_ghi_chu;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Đính kèm layout fragment_them_chi.xml vào Fragment
        view = inflater.inflate(R.layout.fragment_them_thu, container, false);
        anhxa();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idUser = user.getUid();

        list = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
        rcv_them_thu.setLayoutManager(gridLayoutManager);
        rcvDanhMucAdapter = new RCV_Danh_Muc_Adapter(list);
        rcv_them_thu.setAdapter(rcvDanhMucAdapter);
        get_data();

        rcvDanhMucAdapter.setOnItemClickListener(new RCV_Danh_Muc_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String Ten_Danh_Muc) {
                id_danh_muc = id;

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Danh_Muc danh_muc = snapshot.getValue(Danh_Muc.class);
                        icon_Danh_Muc = snapshot.child("icon_Danh_Muc").getValue(String.class);
                        name_danh_muc = snapshot.child("ten_Danh_Muc").getValue(String.class);

                        // Sử dụng thư viện Glide để tải và hiển thị ảnh từ URL
                        Glide.with(view.getContext().getApplicationContext())
                                .load(icon_Danh_Muc)
                                .circleCrop()
                                .into(img_icon);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        edt_so_tien.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edt_so_tien.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getNumberInstance().format(parsed);

                    current = formatted;
                    edt_so_tien.setText(formatted);
                    edt_so_tien.setSelection(formatted.length());

                    edt_so_tien.addTextChangedListener(this);
                }
            }
        });

        them();
        return view;
    }
    private void anhxa(){
        rcv_them_thu= view.findViewById(R.id.rcv_them_thu);
        img_icon = view.findViewById(R.id.img_icon);
        edt_ghi_chu = view.findViewById(R.id.edt_ghi_chu);
        edt_so_tien = view.findViewById(R.id.edt_so_tien);
        btn_them = view.findViewById(R.id.btn_them);

    }

    private void get_data(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc");
        reference.orderByChild("id_User").equalTo(idUser).addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                if (data != null && "Danh mục thu".equals(data.getLoai_Danh_Muc())){
                    list.add(data);
                    rcvDanhMucAdapter.notifyDataSetChanged();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                if (list == null || list.isEmpty()){
                    return;
                }

                for (int i = 0; i < list.size(); i++){
                    if (data.getId_Danh_Muc() == list.get(i).getId_Danh_Muc()){
                        list.set(i, data);
                        break;
                    }
                }
                rcvDanhMucAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                if (data == null || list == null || list.isEmpty()){
                    return;
                }
                for (int i = 0; i < list.size(); i++){
                    if (data.getId_Danh_Muc() == list.get(i).getId_Danh_Muc()){
                        list.remove(list.get(i));
                        break;
                    }
                }
                rcvDanhMucAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void them(){

        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tien = edt_so_tien.getText().toString().replace(".", "").replace(",", "");
                String ghi_chu = edt_ghi_chu.getText().toString();

                if (id_danh_muc == null) {
                    // Người dùng chưa chọn danh mục
                    Toast.makeText(getContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tien.isEmpty() || Double.parseDouble(tien) <= 0) {
                    // Người dùng chưa nhập số tiền hoặc số tiền không hợp lệ
                    Toast.makeText(getContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ghi_chu.isEmpty()) {
                    // Người dùng chưa nhập ghi chú
                    Toast.makeText(getContext(), "Vui lòng nhập ghi chú", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy ngày hiện tại
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = dateFormat.format(new Date());

                DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("Thu_Chi");
                String id = Ref.push().getKey();

                Thu_Chi thu_chi = new Thu_Chi();

                thu_chi.setId_thu_chi(id);
                thu_chi.setId_User(idUser);
                thu_chi.setId_danh_muc(id_danh_muc);
                thu_chi.setSo_tien(tien);
                thu_chi.setGhi_chu(ghi_chu);
                thu_chi.setLoai_thu_chi("Thu nhập");
                thu_chi.setNgay(currentDate);
                thu_chi.setName_danh_muc(name_danh_muc);
                Ref.child(id).setValue(thu_chi).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(view.getContext(), Home_Activity.class));
                    }
                });

            }
        });

    }


}


