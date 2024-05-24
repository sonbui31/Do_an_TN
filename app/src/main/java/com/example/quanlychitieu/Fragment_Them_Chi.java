package com.example.quanlychitieu;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

public class Fragment_Them_Chi extends Fragment {
    private View view;
    private RCV_Danh_Muc_Adapter rcvDanhMucAdapter;
    private RecyclerView rcv_them_chi;
    private ArrayList<Danh_Muc> list;
    private ImageView img_icon;
    private AppCompatButton btn_them;
    private String icon_Danh_Muc, idUser, id_danh_muc, name_danh_muc;
    private EditText edt_so_tien, edt_ghi_chu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_them_chi, container, false);
        anhxa();
        them();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idUser = user.getUid();

        btn_them.setVisibility(View.VISIBLE);

        list = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
        rcv_them_chi.setLayoutManager(gridLayoutManager);
        rcvDanhMucAdapter = new RCV_Danh_Muc_Adapter(list);
        rcv_them_chi.setAdapter(rcvDanhMucAdapter);
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



        return view;
    }
    private void anhxa(){
        rcv_them_chi = view.findViewById(R.id.rcv_them_chi);
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
                if (data != null && "Danh mục chi".equals(data.getLoai_Danh_Muc())){
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Lỗi!");
                    builder.setMessage("Chưa chọn loại danh mục.");

                    // Thêm nút "Đóng" vào thông báo
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    // Hiển thị thông báo
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (tien.isEmpty() || Double.parseDouble(tien) <= 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Lỗi!");
                    builder.setMessage("Chưa nhập giá tiền.");

                    // Thêm nút "Đóng" vào thông báo
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    // Hiển thị thông báo
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (ghi_chu.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Lỗi!");
                    builder.setMessage("Ghi chú bỏ trống.");

                    // Thêm nút "Đóng" vào thông báo
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    // Hiển thị thông báo
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
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
                    thu_chi.setLoai_thu_chi("Chi phí");
                    thu_chi.setName_danh_muc(name_danh_muc);
                    thu_chi.setNgay(currentDate);
                    Ref.child(id).setValue(thu_chi).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(view.getContext(), Home_Activity.class));
                        }
                    });
                }



            }
        });

    }

}
