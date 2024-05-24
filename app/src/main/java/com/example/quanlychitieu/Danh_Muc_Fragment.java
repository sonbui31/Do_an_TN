package com.example.quanlychitieu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Danh_Muc_Fragment extends Fragment {
    private View view;
    private ImageView img_back;

    private FloatingActionButton FABtn_add;

    private ArrayList<Danh_Muc> list, list_thu;
    private RecyclerView rcv_danh_muc_chi, rcv_danh_muc_thu;
    private RCV_Danh_Muc_Adapter rcvDanhMucAdapter, rcvDanhMucAdapter_thu;
    private Uri selectedIconUri;
    private Progress_Dialog progress_dialog;
    private String idUser;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.danh_muc_fragment, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idUser = user.getUid();

        anhxa();
        click();

        list = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
        rcv_danh_muc_chi.setLayoutManager(gridLayoutManager);
        rcvDanhMucAdapter = new RCV_Danh_Muc_Adapter(list);
        rcv_danh_muc_chi.setAdapter(rcvDanhMucAdapter);
        get_data();

        list_thu = new ArrayList<>();
        GridLayoutManager gridLayoutManager_thu = new GridLayoutManager(view.getContext(), 3);
        rcv_danh_muc_thu.setLayoutManager(gridLayoutManager_thu);
        rcvDanhMucAdapter_thu = new RCV_Danh_Muc_Adapter(list_thu);
        rcv_danh_muc_thu.setAdapter(rcvDanhMucAdapter_thu);
        get_data_thu();

        rcvDanhMucAdapter_thu.setOnItemClickListener(new RCV_Danh_Muc_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String Ten_Danh_Muc) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_chi_tiet_danh_muc);
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

                ImageView img_icon_danh_muc = dialog.findViewById(R.id.img_icon_danh_muc);
                EditText edt_ten_danh_muc = dialog.findViewById(R.id.edt_ten_danh_muc);
                AppCompatButton btn_huy = dialog.findViewById(R.id.btn_huy);
                AppCompatButton btn_sửa = dialog.findViewById(R.id.btn_sửa);

                AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.auto_complete_tv);

                RecyclerView rcv_them_danh_muc = dialog.findViewById(R.id.rcv_them_danh_muc);
                ArrayList<Icon_Model> list_them;
                RCV_Icon_Adapter rcv_icon_adapter_them;

                list_them = new ArrayList<>();
                data_icon(list_them);

                // Sử dụng GridLayoutManager thay vì LinearLayoutManager
                GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 5);
                rcv_them_danh_muc.setLayoutManager(gridLayoutManager);

                rcv_icon_adapter_them = new RCV_Icon_Adapter(list_them);
                rcv_them_danh_muc.setAdapter(rcv_icon_adapter_them);

                // Thêm sự kiện click cho Adapter để hiển thị icon khi một item được chọn
                rcv_icon_adapter_them.setOnItemClickListener(new RCV_Icon_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Icon_Model icon) {
                        img_icon_danh_muc.setImageResource(icon.getIcon());
                        selectedIconUri = Uri.parse("android.resource://" + view.getContext().getPackageName() + "/" + icon.getIcon());


                    }
                });


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                        String icon_Danh_Muc = snapshot.child("icon_Danh_Muc").getValue(String.class);
                        String loai_Danh_Muc = snapshot.child("loai_Danh_Muc").getValue(String.class);
                        String ten_Danh_Muc = snapshot.child("ten_Danh_Muc").getValue(String.class);

                        Glide.with(view.getContext())
                                .load(icon_Danh_Muc)
                                .circleCrop()
                                .into(img_icon_danh_muc);
                        edt_ten_danh_muc.setText(ten_Danh_Muc);
                        autoCompleteTextView.setText(loai_Danh_Muc);

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

                btn_sửa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progress_dialog.ShowDilag("Đang sửa danh mục.");

                        String ten_new = edt_ten_danh_muc.getText().toString().trim();
                        if (ten_new.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Lỗi!");
                            builder.setMessage("Bỏ trống tên danh mục.");

                            // Thêm nút "Đóng" vào thông báo
                            builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progress_dialog.HideDialog();
                                    dialog.dismiss();

                                }
                            });

                            // Hiển thị thông báo
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference imgRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            imgRef.putFile(selectedIconUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    String imgUrl = uri.toString();
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                                                    reference.child("icon_Danh_Muc").setValue(imgUrl);
                                                    reference.child("ten_Danh_Muc").setValue(ten_new).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            dialog.dismiss();
                                                            progress_dialog.HideDialog();
                                                        }
                                                    });


                                                }
                                            });
                                        }
                                    });


                        }

                    }
                });

                dialog.show();

            }
        });
        rcvDanhMucAdapter.setOnItemClickListener(new RCV_Danh_Muc_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String Ten_Danh_Muc) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_chi_tiet_danh_muc);
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

                ImageView img_icon_danh_muc = dialog.findViewById(R.id.img_icon_danh_muc);
                EditText edt_ten_danh_muc = dialog.findViewById(R.id.edt_ten_danh_muc);
                AppCompatButton btn_huy = dialog.findViewById(R.id.btn_huy);
                AppCompatButton btn_sửa = dialog.findViewById(R.id.btn_sửa);

                AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.auto_complete_tv);

                RecyclerView rcv_them_danh_muc = dialog.findViewById(R.id.rcv_them_danh_muc);
                ArrayList<Icon_Model> list_them;
                RCV_Icon_Adapter rcv_icon_adapter_them;

                list_them = new ArrayList<>();
                data_icon(list_them);

                // Sử dụng GridLayoutManager thay vì LinearLayoutManager
                GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 5);
                rcv_them_danh_muc.setLayoutManager(gridLayoutManager);

                rcv_icon_adapter_them = new RCV_Icon_Adapter(list_them);
                rcv_them_danh_muc.setAdapter(rcv_icon_adapter_them);

                // Thêm sự kiện click cho Adapter để hiển thị icon khi một item được chọn
                rcv_icon_adapter_them.setOnItemClickListener(new RCV_Icon_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Icon_Model icon) {
                        img_icon_danh_muc.setImageResource(icon.getIcon());
                        selectedIconUri = Uri.parse("android.resource://" + view.getContext().getPackageName() + "/" + icon.getIcon());


                    }
                });


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                        String icon_Danh_Muc = snapshot.child("icon_Danh_Muc").getValue(String.class);
                        String loai_Danh_Muc = snapshot.child("loai_Danh_Muc").getValue(String.class);
                        String ten_Danh_Muc = snapshot.child("ten_Danh_Muc").getValue(String.class);

                        Glide.with(view.getContext())
                                .load(icon_Danh_Muc)
                                .circleCrop()
                                .into(img_icon_danh_muc);
                        edt_ten_danh_muc.setText(ten_Danh_Muc);
                        autoCompleteTextView.setText(loai_Danh_Muc);

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

                btn_sửa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progress_dialog.ShowDilag("Đang sửa danh mục.");

                        String ten_new = edt_ten_danh_muc.getText().toString().trim();
                        if (ten_new.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Lỗi!");
                            builder.setMessage("Bỏ trống tên danh mục.");

                            // Thêm nút "Đóng" vào thông báo
                            builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progress_dialog.HideDialog();
                                    dialog.dismiss();

                                }
                            });

                            // Hiển thị thông báo
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference imgRef = storageRef.child("images/" + UUID.randomUUID().toString());

                            imgRef.putFile(selectedIconUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {


                                                    String imgUrl = uri.toString();
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
                                                    reference.child("icon_Danh_Muc").setValue(imgUrl);
                                                    reference.child("ten_Danh_Muc").setValue(ten_new).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            dialog.dismiss();
                                                            progress_dialog.HideDialog();
                                                        }
                                                    });


                                                }
                                            });
                                        }
                                    });


                        }

                    }
                });

                dialog.show();

            }
        });


        return view;
    }
    private void anhxa(){
        img_back = view.findViewById(R.id.img_back);
        rcv_danh_muc_chi = view.findViewById(R.id.rcv_danh_muc_chi);
        rcv_danh_muc_thu = view.findViewById(R.id.rcv_danh_muc_thu);
        FABtn_add = view.findViewById(R.id.FABtn_add);
        progress_dialog = new Progress_Dialog(view.getContext());

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

    private void get_data_thu(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc");
        reference.orderByChild("id_User").equalTo(idUser).addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                if (data != null && "Danh mục thu".equals(data.getLoai_Danh_Muc())){
                    list_thu.add(data);
                    rcvDanhMucAdapter_thu.notifyDataSetChanged();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                if (list_thu == null || list_thu.isEmpty()){
                    return;
                }

                for (int i = 0; i < list_thu.size(); i++){
                    if (data.getId_Danh_Muc() == list_thu.get(i).getId_Danh_Muc()){
                        list_thu.set(i, data);
                        break;
                    }
                }
                rcvDanhMucAdapter_thu.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Danh_Muc data = snapshot.getValue(Danh_Muc.class);
                if (data == null || list_thu == null || list_thu.isEmpty()){
                    return;
                }
                for (int i = 0; i < list_thu.size(); i++){
                    if (data.getId_Danh_Muc() == list_thu.get(i).getId_Danh_Muc()){
                        list_thu.remove(list_thu.get(i));
                        break;
                    }
                }
                rcvDanhMucAdapter_thu.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void click(){

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new Person_Fragment()).commit();
            }
        });
        FABtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogThem_Danh_Muc();
            }
        });
    }



    private void openDialogThem_Danh_Muc(){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_them_danh_muc);
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

        ImageView img_icon_danh_muc = dialog.findViewById(R.id.img_icon_danh_muc);
        EditText edt_ten_danh_muc = dialog.findViewById(R.id.edt_ten_danh_muc);
        AppCompatButton btn_huy = dialog.findViewById(R.id.btn_huy);
        AppCompatButton btn_them = dialog.findViewById(R.id.btn_them);

        String[] item = {"Danh mục thu", "Danh mục chi"};
        AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.auto_complete_tv);
        ArrayAdapter<String> adapterItem;
        adapterItem = new ArrayAdapter<String>(getActivity(), R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItem);

        RecyclerView rcv_them_danh_muc = dialog.findViewById(R.id.rcv_them_danh_muc);
        ArrayList<Icon_Model> list_them;
        RCV_Icon_Adapter rcv_icon_adapter_them;

        list_them = new ArrayList<>();
        data_icon(list_them);

        // Sử dụng GridLayoutManager thay vì LinearLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 5);
        rcv_them_danh_muc.setLayoutManager(gridLayoutManager);

        rcv_icon_adapter_them = new RCV_Icon_Adapter(list_them);
        rcv_them_danh_muc.setAdapter(rcv_icon_adapter_them);

        // Thêm sự kiện click cho Adapter để hiển thị icon khi một item được chọn
        rcv_icon_adapter_them.setOnItemClickListener(new RCV_Icon_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Icon_Model icon) {
                img_icon_danh_muc.setImageResource(icon.getIcon());
                selectedIconUri = Uri.parse("android.resource://" + view.getContext().getPackageName() + "/" + icon.getIcon());


            }
        });



        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress_dialog.ShowDilag("Đang thêm danh mục.");
                String ten_danh_muc = edt_ten_danh_muc.getText().toString();
                String loai_Danh_Muc = autoCompleteTextView.getText().toString();

                if (ten_danh_muc.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Lỗi!");
                    builder.setMessage("Bỏ trống tên danh mục.");

                    // Thêm nút "Đóng" vào thông báo
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress_dialog.HideDialog();
                            dialog.dismiss();

                        }
                    });

                    // Hiển thị thông báo
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (loai_Danh_Muc.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Lỗi!");
                    builder.setMessage("Chưa chọn loại danh mục.");

                    // Thêm nút "Đóng" vào thông báo
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress_dialog.HideDialog();
                            dialog.dismiss();

                        }
                    });

                    // Hiển thị thông báo
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else if (selectedIconUri == null) {
                    // Xử lý khi chưa chọn icon
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Lỗi!");
                    builder.setMessage("Chưa chọn biểu tượng danh mục.");
                    builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progress_dialog.HideDialog();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else {

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imgRef = storageRef.child("images/" + UUID.randomUUID().toString());

                    imgRef.putFile(selectedIconUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {


                                            String imgUrl = uri.toString();
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc");
                                            String id_Danh_Muc = reference.push().getKey();
                                            Map<String, Object> danh_Muc = new HashMap<>();
                                            danh_Muc.put("icon_Danh_Muc", imgUrl);
                                            danh_Muc.put("ten_Danh_Muc", ten_danh_muc);
                                            danh_Muc.put("id_Danh_Muc", id_Danh_Muc);
                                            danh_Muc.put("id_User", idUser);
                                            danh_Muc.put("loai_Danh_Muc", loai_Danh_Muc);


                                            reference.child(id_Danh_Muc).setValue(danh_Muc)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                dialog.dismiss();
                                                                progress_dialog.HideDialog();
                                                            }
                                                        }
                                                    });

                                        }
                                    });
                                }
                            });


                }

            }
        });

        dialog.show();
    }

    private void data_icon(ArrayList<Icon_Model> list_them){
        // Tạo một mảng hoặc danh sách chứa các đối tượng Icon_Model
        ArrayList<Icon_Model> icons = new ArrayList<>();

        // Thêm các đối tượng Icon_Model vào mảng hoặc danh sách
        icons.add(new Icon_Model(R.drawable.liquor));
        icons.add(new Icon_Model(R.drawable.beer));
        icons.add(new Icon_Model(R.drawable.fish));
        icons.add(new Icon_Model(R.drawable.watermelon));
        icons.add(new Icon_Model(R.drawable.rice));
        icons.add(new Icon_Model(R.drawable.food));
        icons.add(new Icon_Model(R.drawable.fries));
        icons.add(new Icon_Model(R.drawable.milk));
        icons.add(new Icon_Model(R.drawable.strawberry));
        icons.add(new Icon_Model(R.drawable.popsicle));
        icons.add(new Icon_Model(R.drawable.cherries));
        icons.add(new Icon_Model(R.drawable.chicken_leg));
        icons.add(new Icon_Model(R.drawable.strawberry_cake));
        icons.add(new Icon_Model(R.drawable.pizza));
        icons.add(new Icon_Model(R.drawable.turkey_leg));
        icons.add(new Icon_Model(R.drawable.candy));
        icons.add(new Icon_Model(R.drawable.hot_dog));
        icons.add(new Icon_Model(R.drawable.pet_food));
        icons.add(new Icon_Model(R.drawable.bread));
        icons.add(new Icon_Model(R.drawable.roller_skater));
        icons.add(new Icon_Model(R.drawable.pawn));
        icons.add(new Icon_Model(R.drawable.gun));
        icons.add(new Icon_Model(R.drawable.game_boy));
        icons.add(new Icon_Model(R.drawable.watching_a_movie));
        icons.add(new Icon_Model(R.drawable.radio));
        icons.add(new Icon_Model(R.drawable.shuttlecock));
        icons.add(new Icon_Model(R.drawable.ping_pong));
        icons.add(new Icon_Model(R.drawable.billiard));
        icons.add(new Icon_Model(R.drawable.console));
        icons.add(new Icon_Model(R.drawable.agreement));
        icons.add(new Icon_Model(R.drawable.air_conditioner));
        icons.add(new Icon_Model(R.drawable.archery));
        icons.add(new Icon_Model(R.drawable.attach_file));
        icons.add(new Icon_Model(R.drawable.badge));
        icons.add(new Icon_Model(R.drawable.bag));
        icons.add(new Icon_Model(R.drawable.baseball));
        icons.add(new Icon_Model(R.drawable.bath_tub));
        icons.add(new Icon_Model(R.drawable.binoculars));
        icons.add(new Icon_Model(R.drawable.birthday_cake));
        icons.add(new Icon_Model(R.drawable.book));
        icons.add(new Icon_Model(R.drawable.books));
        icons.add(new Icon_Model(R.drawable.boots));
        icons.add(new Icon_Model(R.drawable.bouquet));
        icons.add(new Icon_Model(R.drawable.bowtie));
        icons.add(new Icon_Model(R.drawable.butterfly));
        icons.add(new Icon_Model(R.drawable.bycicle));
        icons.add(new Icon_Model(R.drawable.cabinet));
        icons.add(new Icon_Model(R.drawable.camera));
        icons.add(new Icon_Model(R.drawable.candle));
        icons.add(new Icon_Model(R.drawable.candy_cane));
        icons.add(new Icon_Model(R.drawable.car));
        icons.add(new Icon_Model(R.drawable.carrot));
        icons.add(new Icon_Model(R.drawable.chair));
        icons.add(new Icon_Model(R.drawable.children));
        icons.add(new Icon_Model(R.drawable.christmas_sock));
        icons.add(new Icon_Model(R.drawable.cigarettes));
        icons.add(new Icon_Model(R.drawable.cloth));
        icons.add(new Icon_Model(R.drawable.cloud_server));
        icons.add(new Icon_Model(R.drawable.coat));
        icons.add(new Icon_Model(R.drawable.cocktail));
        icons.add(new Icon_Model(R.drawable.coffee));
        icons.add(new Icon_Model(R.drawable.comb));
        icons.add(new Icon_Model(R.drawable.conversation));
        icons.add(new Icon_Model(R.drawable.cupcake));
        icons.add(new Icon_Model(R.drawable.denim_shorts));
        icons.add(new Icon_Model(R.drawable.donation));
        icons.add(new Icon_Model(R.drawable.double_bed));
        icons.add(new Icon_Model(R.drawable.dress));
        icons.add(new Icon_Model(R.drawable.earning));
        icons.add(new Icon_Model(R.drawable.exam));
        icons.add(new Icon_Model(R.drawable.excavator));
        icons.add(new Icon_Model(R.drawable.exchange_rate));
        icons.add(new Icon_Model(R.drawable.fencing));
        icons.add(new Icon_Model(R.drawable.fire_cracker));
        icons.add(new Icon_Model(R.drawable.fire_crackers));
        icons.add(new Icon_Model(R.drawable.first_aid_box));
        icons.add(new Icon_Model(R.drawable.flash_light));
        icons.add(new Icon_Model(R.drawable.flask));
        icons.add(new Icon_Model(R.drawable.floppy_disk));
        icons.add(new Icon_Model(R.drawable.flower_basket));
        icons.add(new Icon_Model(R.drawable.food_tray));
        icons.add(new Icon_Model(R.drawable.football));
        icons.add(new Icon_Model(R.drawable.gas_mask));
        icons.add(new Icon_Model(R.drawable.gas_pump));
        icons.add(new Icon_Model(R.drawable.gloves));
        icons.add(new Icon_Model(R.drawable.golf_stick));
        icons.add(new Icon_Model(R.drawable.graduation_hat));
        icons.add(new Icon_Model(R.drawable.grape));
        icons.add(new Icon_Model(R.drawable.guitar));
        icons.add(new Icon_Model(R.drawable.gym_clothes));
        icons.add(new Icon_Model(R.drawable.gym_nastics));
        icons.add(new Icon_Model(R.drawable.hair_treatment));
        icons.add(new Icon_Model(R.drawable.hat));
        icons.add(new Icon_Model(R.drawable.helicopter));
        icons.add(new Icon_Model(R.drawable.high_heel));
        icons.add(new Icon_Model(R.drawable.international_call));
        icons.add(new Icon_Model(R.drawable.iot));
        icons.add(new Icon_Model(R.drawable.island));
        icons.add(new Icon_Model(R.drawable.keyboard));
        icons.add(new Icon_Model(R.drawable.label));
        icons.add(new Icon_Model(R.drawable.law));
        icons.add(new Icon_Model(R.drawable.lip_stick));
        icons.add(new Icon_Model(R.drawable.lottery));
        icons.add(new Icon_Model(R.drawable.luggage));
        icons.add(new Icon_Model(R.drawable.luxury));
        icons.add(new Icon_Model(R.drawable.machine));
        icons.add(new Icon_Model(R.drawable.male_clothes));
        icons.add(new Icon_Model(R.drawable.meditation));
        icons.add(new Icon_Model(R.drawable.microphone));
        icons.add(new Icon_Model(R.drawable.microscope));
        icons.add(new Icon_Model(R.drawable.money));
        icons.add(new Icon_Model(R.drawable.more));
        icons.add(new Icon_Model(R.drawable.motorbike));
        icons.add(new Icon_Model(R.drawable.motorcycle));
        icons.add(new Icon_Model(R.drawable.mouse));
        icons.add(new Icon_Model(R.drawable.neck_lace));
        icons.add(new Icon_Model(R.drawable.office_material));
        icons.add(new Icon_Model(R.drawable.paint_roller));
        icons.add(new Icon_Model(R.drawable.palette));
        icons.add(new Icon_Model(R.drawable.part_time));
        icons.add(new Icon_Model(R.drawable.pc));
        icons.add(new Icon_Model(R.drawable.perfume));
        icons.add(new Icon_Model(R.drawable.pets));
        icons.add(new Icon_Model(R.drawable.picture));
        icons.add(new Icon_Model(R.drawable.plane));
        icons.add(new Icon_Model(R.drawable.plant));
        icons.add(new Icon_Model(R.drawable.population));
        icons.add(new Icon_Model(R.drawable.printer));
        icons.add(new Icon_Model(R.drawable.prize));
        icons.add(new Icon_Model(R.drawable.pumpkin));
        icons.add(new Icon_Model(R.drawable.repair_tools));
        icons.add(new Icon_Model(R.drawable.responsive));
        icons.add(new Icon_Model(R.drawable.restaurant));
        icons.add(new Icon_Model(R.drawable.robot));
        icons.add(new Icon_Model(R.drawable.router));
        icons.add(new Icon_Model(R.drawable.rugby_ball));
        icons.add(new Icon_Model(R.drawable.ruler));
        icons.add(new Icon_Model(R.drawable.sailboat));
        icons.add(new Icon_Model(R.drawable.salary));
        icons.add(new Icon_Model(R.drawable.santa_hat));
        icons.add(new Icon_Model(R.drawable.satellite_dish));
        icons.add(new Icon_Model(R.drawable.school));
        icons.add(new Icon_Model(R.drawable.school_bag));
        icons.add(new Icon_Model(R.drawable.school_bus));
        icons.add(new Icon_Model(R.drawable.search));
        icons.add(new Icon_Model(R.drawable.seater_sofa));
        icons.add(new Icon_Model(R.drawable.settings));
        icons.add(new Icon_Model(R.drawable.ship));
        icons.add(new Icon_Model(R.drawable.skiing));
        icons.add(new Icon_Model(R.drawable.slide_show));
        icons.add(new Icon_Model(R.drawable.smart_tv));
        icons.add(new Icon_Model(R.drawable.smartphone));
        icons.add(new Icon_Model(R.drawable.snow_man));
        icons.add(new Icon_Model(R.drawable.spade));
        icons.add(new Icon_Model(R.drawable.suitcase));
        icons.add(new Icon_Model(R.drawable.surfing));
        icons.add(new Icon_Model(R.drawable.swim_wear));
        icons.add(new Icon_Model(R.drawable.swimming));
        icons.add(new Icon_Model(R.drawable.table_lamp));
        icons.add(new Icon_Model(R.drawable.table_tennis));
        icons.add(new Icon_Model(R.drawable.teddy_bear));
        icons.add(new Icon_Model(R.drawable.telephone));
        icons.add(new Icon_Model(R.drawable.telescope));
        icons.add(new Icon_Model(R.drawable.tennis));
        icons.add(new Icon_Model(R.drawable.trading));

        icons.add(new Icon_Model(R.drawable.train));
        icons.add(new Icon_Model(R.drawable.transport));
        icons.add(new Icon_Model(R.drawable.trolley));
        icons.add(new Icon_Model(R.drawable.trophy));
        icons.add(new Icon_Model(R.drawable.umbrella));
        icons.add(new Icon_Model(R.drawable.usb_flash_drive));
        icons.add(new Icon_Model(R.drawable.wage));
        icons.add(new Icon_Model(R.drawable.watch));
        icons.add(new Icon_Model(R.drawable.world));
        icons.add(new Icon_Model(R.drawable.writing));

        // Thêm tất cả các đối tượng Icon_Model vào danh sách list
        list_them.addAll(icons);
    }



}
