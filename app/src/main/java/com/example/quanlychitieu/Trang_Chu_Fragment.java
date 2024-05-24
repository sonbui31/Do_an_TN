package com.example.quanlychitieu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class Trang_Chu_Fragment extends Fragment {
    private View view;
    private ImageView imageView3, img_search;
    private TextView tv_name_profile, tv_chi, tv_thu, tv_tong_so_du;
    private String imageUrl;
    private Progress_Dialog dialog;

    private RecyclerView rcv_home;
    private RCV_Thu_Chi_Adapter rcv_thu_chi_adapter;
    private ArrayList<Thu_Chi> list;
    private String idUser;
    String id_danh_muc, id_danh_muc_new, ten_danh_muc_new;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.trang_chu_fragment, container, false);
        anhxa();
        set_profile();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idUser = user.getUid();

        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_home.setLayoutManager(linearLayoutManager);
        rcv_thu_chi_adapter = new RCV_Thu_Chi_Adapter(list);
        rcv_home.setAdapter(rcv_thu_chi_adapter);
        get_data();
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), Activity_Search.class));
            }
        });

        rcv_thu_chi_adapter.setOnItemClickListener(new RCV_Thu_Chi_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String idThuChi, String Id_danh_muc) {
                id_danh_muc_new = Id_danh_muc;
                id_danh_muc = Id_danh_muc;

                final Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_chi_tiet_thu_chi);
                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                if (window == null){
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);

                ImageView img_icon = dialog.findViewById(R.id.img_iconTC);
                EditText edt_st= dialog.findViewById(R.id.edt_so_tien);
                EditText edt_gc = dialog.findViewById(R.id.edt_ghi_chu);

                edt_st.addTextChangedListener(new TextWatcher() {
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
                            edt_st.removeTextChangedListener(this);

                            String cleanString = s.toString().replaceAll("[,.]", "");
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = NumberFormat.getNumberInstance().format(parsed);

                            current = formatted;
                            edt_st.setText(formatted);
                            edt_st.setSelection(formatted.length());

                            edt_st.addTextChangedListener(this);
                        }
                    }
                });

                AppCompatButton btn_sua = dialog.findViewById(R.id.btn_sua);
                AppCompatButton btn_huy = dialog.findViewById(R.id.btn_huy);

                RecyclerView rcv_danh_muc = dialog.findViewById(R.id.rcv_thu_chi);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Thu_Chi").child(idThuChi);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String ghi_chu = snapshot.child("ghi_chu").getValue(String.class);
                        String so_tien = snapshot.child("so_tien").getValue(String.class);
                        edt_gc.setText(ghi_chu);
                        edt_st.setText(so_tien);

                        String idDM = snapshot.child("id_danh_muc").getValue(String.class);
                        DatabaseReference referenceDM = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(idDM);
                        referenceDM.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String url = snapshot.child("icon_Danh_Muc").getValue(String.class);
                                // Sử dụng thư viện Glide để tải và hiển thị ảnh từ URL
                                Glide.with(view.getContext().getApplicationContext())
                                        .load(url)
                                        .circleCrop()
                                        .into(img_icon);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        String loai_thu_chi = snapshot.child("loai_thu_chi").getValue(String.class);

                        if (loai_thu_chi.equals("Chi phí")) {
                            ArrayList<Danh_Muc> list1;
                            RCV_Danh_Muc_Adapter rcv_danh_muc_adapter_chi;

                            list1 = new ArrayList<>();
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
                            rcv_danh_muc.setLayoutManager(gridLayoutManager);
                            rcv_danh_muc_adapter_chi = new RCV_Danh_Muc_Adapter(list1);
                            rcv_danh_muc.setAdapter(rcv_danh_muc_adapter_chi);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc");

                            reference.orderByChild("id_User").equalTo(idUser).addChildEventListener(new ChildEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                                    if (data != null && "Danh mục chi".equals(data.getLoai_Danh_Muc())){
                                        list1.add(data);
                                        rcv_danh_muc_adapter_chi.notifyDataSetChanged();
                                    }
                                }

                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                                    if (list1 == null || list1.isEmpty()){
                                        return;
                                    }

                                    for (int i = 0; i < list1.size(); i++){
                                        if (data.getId_Danh_Muc() == list1.get(i).getId_Danh_Muc()){
                                            list1.set(i, data);
                                            break;
                                        }
                                    }
                                    rcv_danh_muc_adapter_chi.notifyDataSetChanged();
                                }

                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                    Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                                    if (data == null || list1 == null || list1.isEmpty()){
                                        return;
                                    }
                                    for (int i = 0; i < list1.size(); i++){
                                        if (data.getId_Danh_Muc() == list1.get(i).getId_Danh_Muc()){
                                            list1.remove(list1.get(i));
                                            break;
                                        }
                                    }
                                    rcv_danh_muc_adapter_chi.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            rcv_danh_muc_adapter_chi.setOnItemClickListener(new RCV_Danh_Muc_Adapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String id, String Ten_Danh_Muc) {
                                    id_danh_muc_new = id;
                                    ten_danh_muc_new = Ten_Danh_Muc;

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String url = snapshot.child("icon_Danh_Muc").getValue(String.class);
                                            // Sử dụng thư viện Glide để tải và hiển thị ảnh từ URL
                                            Glide.with(view.getContext().getApplicationContext())
                                                    .load(url)
                                                    .circleCrop()
                                                    .into(img_icon);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });

                        } else if (loai_thu_chi.equals("Thu nhập")) {

                            ArrayList<Danh_Muc> list1;
                            RCV_Danh_Muc_Adapter rcv_danh_muc_adapter_thu;

                            list1 = new ArrayList<>();
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
                            rcv_danh_muc.setLayoutManager(gridLayoutManager);
                            rcv_danh_muc_adapter_thu = new RCV_Danh_Muc_Adapter(list1);
                            rcv_danh_muc.setAdapter(rcv_danh_muc_adapter_thu);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc");

                            reference.orderByChild("id_User").equalTo(idUser).addChildEventListener(new ChildEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                                    if (data != null && "Danh mục thu".equals(data.getLoai_Danh_Muc())){
                                        list1.add(data);
                                        rcv_danh_muc_adapter_thu.notifyDataSetChanged();
                                    }
                                }

                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                                    if (list1 == null || list1.isEmpty()){
                                        return;
                                    }

                                    for (int i = 0; i < list1.size(); i++){
                                        if (data.getId_Danh_Muc() == list1.get(i).getId_Danh_Muc()){
                                            list1.set(i, data);
                                            break;
                                        }
                                    }
                                    rcv_danh_muc_adapter_thu.notifyDataSetChanged();
                                }

                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                    Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                                    if (data == null || list1 == null || list1.isEmpty()){
                                        return;
                                    }
                                    for (int i = 0; i < list1.size(); i++){
                                        if (data.getId_Danh_Muc() == list1.get(i).getId_Danh_Muc()){
                                            list1.remove(list1.get(i));
                                            break;
                                        }
                                    }
                                    rcv_danh_muc_adapter_thu.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            rcv_danh_muc_adapter_thu.setOnItemClickListener(new RCV_Danh_Muc_Adapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String id, String Ten_Danh_Muc) {
                                     id_danh_muc_new = id;
                                     ten_danh_muc_new = Ten_Danh_Muc;

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String url = snapshot.child("icon_Danh_Muc").getValue(String.class);
                                            // Sử dụng thư viện Glide để tải và hiển thị ảnh từ URL
                                            Glide.with(view.getContext().getApplicationContext())
                                                    .load(url)
                                                    .circleCrop()
                                                    .into(img_icon);


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                        }





                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                btn_huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_sua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String gc = edt_gc.getText().toString().trim();
                        String st = edt_st.getText().toString().replace(".", "").replace(",", "");

                        if (st.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Lỗi!");
                            builder.setMessage("Bỏ trống số tiền.");

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
                        } else if (gc.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Lỗi!");
                            builder.setMessage("Bỏ trống ghí chú.");

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

                            if (!id_danh_muc_new.equals(id_danh_muc)){
                                Toast.makeText(view.getContext(), "Danh mục mới", Toast.LENGTH_SHORT).show();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Thu_Chi").child(idThuChi);
                                reference.child("so_tien").setValue(st);
                                reference.child("id_danh_muc").setValue(id_danh_muc_new);
                                reference.child("name_danh_muc").setValue(ten_danh_muc_new);
                                reference.child("ghi_chu").setValue(gc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(view.getContext(), "Danh mục cũ", Toast.LENGTH_SHORT).show();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Thu_Chi").child(idThuChi);
                                reference.child("so_tien").setValue(st);
                                reference.child("ghi_chu").setValue(gc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                    }
                                });

                            }





                        }

                    }
                });

                dialog.show();
            }
        });

        return view;
    }

    private void get_data(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Thu_Chi");
        reference.orderByChild("id_User").equalTo(idUser).addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Thu_Chi data = snapshot.getValue(Thu_Chi.class);
                if (data != null ){
                    list.add(data);
                    rcv_thu_chi_adapter.notifyDataSetChanged();
                    sortDataByMonthAndDate();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Thu_Chi data = snapshot.getValue(Thu_Chi.class);
                if (list == null || list.isEmpty()){
                    return;
                }

                for (int i = 0; i < list.size(); i++){
                    if (data.getId_thu_chi() == list.get(i).getId_thu_chi()){
                        list.set(i, data);
                        break;
                    }
                }
                sortDataByMonthAndDate();

                rcv_thu_chi_adapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Thu_Chi data = snapshot.getValue(Thu_Chi.class);
                if (data == null || list == null || list.isEmpty()){
                    return;
                }
                for (int i = 0; i < list.size(); i++){
                    if (data.getId_thu_chi() == list.get(i).getId_thu_chi()){
                        list.remove(list.get(i));
                        break;
                    }
                }
                sortDataByMonthAndDate();
                rcv_thu_chi_adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sortDataByMonthAndDate() {
        Collections.sort(list, new Comparator<Thu_Chi>() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(Thu_Chi o1, Thu_Chi o2) {
                try {
                    // Parse ngày thành Date để so sánh
                    Date date1 = dateFormat.parse(o1.getNgay());
                    Date date2 = dateFormat.parse(o2.getNgay());

                    // So sánh theo thứ tự từ lớn đến bé (ngày mới đến cũ)
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    private void anhxa(){
        imageView3 = view.findViewById(R.id.imageView3);
        tv_name_profile = view.findViewById(R.id.tv_name_profile);
        rcv_home = view.findViewById(R.id.rcv_home);
        dialog = new Progress_Dialog(view.getContext());
        tv_chi = view.findViewById(R.id.tv_chi);
        tv_thu = view.findViewById(R.id.tv_thu);
        tv_tong_so_du = view.findViewById(R.id.tv_tong_so_du);
        img_search = view.findViewById(R.id.img_search);
    }

    private void set_profile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());

        dialog.ShowDilag("Đang tải...");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                imageUrl = snapshot.child("imageUrl").getValue(String.class);
                Glide.with(view.getContext())
                        .load(imageUrl)
                        .circleCrop()
                        .into(imageView3);
                tv_name_profile.setText(user.getName());
                dialog.HideDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.HideDialog();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Thu_Chi");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long tongThu = 0;
                long tongChi = 0;

                // Lấy tháng và năm hiện tại
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH bắt đầu từ 0
                int currentYear = calendar.get(Calendar.YEAR);

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String idUserTC = childSnapshot.child("id_User").getValue(String.class);
                    String loai = childSnapshot.child("loai_thu_chi").getValue(String.class);
                    String giaTien = childSnapshot.child("so_tien").getValue(String.class);
                    String ngay = childSnapshot.child("ngay").getValue(String.class);

                    // Tách tháng và năm từ chuỗi ngày
                    String[] parts = ngay.split("/");
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);

                    long gt = Long.parseLong(giaTien);

                    if (idUserTC.equals(idUser) && month == currentMonth && year == currentYear) {
                        if (loai.equals("Thu nhập")) {
                            tongThu += gt;
                        } else if (loai.equals("Chi phí")) {
                            tongChi += gt;
                        }
                    }
                }
                long tongDu = tongThu - tongChi;

                String tc = CurrencyFormatter.formatVND(tongChi);
                String tt = CurrencyFormatter.formatVND(tongThu);
                String td = CurrencyFormatter.formatVND(tongDu);

                // Sử dụng tổng thu và tổng chi ở đây
                tv_chi.setText(tc);
                tv_thu.setText(tt);
                tv_tong_so_du.setText(td);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

}
