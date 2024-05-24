package com.example.quanlychitieu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Search extends AppCompatActivity {
    private EditText edt_Search;
    private RecyclerView rcv_search;
    private RCV_Thu_Chi_Adapter rcvSearchAdapter;
    private ArrayList<Thu_Chi> list;
    private ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        anhxa();

        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(Activity_Search.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_search.setLayoutManager(linearLayoutManager);
        rcvSearchAdapter = new RCV_Thu_Chi_Adapter(list);
        rcv_search.setAdapter(rcvSearchAdapter);

        // Lấy toàn bộ dữ liệu từ Firebase và hiển thị lên RecyclerView
        getTenants();

        rcvSearchAdapter.setOnItemClickListener(new RCV_Thu_Chi_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String Id_danh_muc) {
            }
        });

        edt_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                // Gọi phương thức tìm kiếm với giá trị searchText
                performSearch(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    // Hiển thị toàn bộ dữ liệu
                    getTenants();
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Search.this, Home_Activity.class));

            }
        });


    }
    private void anhxa(){
        edt_Search = findViewById(R.id.edt_Search);
        rcv_search = findViewById(R.id.rcv_search);
        img_back = findViewById(R.id.img_back);
    }
    private void getTenants(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Thu_Chi");
        reference.orderByChild("id_User").equalTo(idUser).addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Thu_Chi thu_chi = snapshot.getValue(Thu_Chi.class);
                if (thu_chi != null){
                    list.add(thu_chi);
                    rcvSearchAdapter.notifyDataSetChanged();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Thu_Chi thu_chi = snapshot.getValue(Thu_Chi.class);
                if (list == null || list.isEmpty()){
                    return;
                }

                for (int i = 0; i < list.size(); i++){
                    if (thu_chi.getId_thu_chi() == list.get(i).getId_thu_chi()){
                        list.set(i, thu_chi);
                        break;
                    }
                }
                rcvSearchAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Thu_Chi thu_chi = snapshot.getValue(Thu_Chi.class);
                if (thu_chi == null || list == null || list.isEmpty()){
                    return;
                }
                for (int i = 0; i < list.size(); i++){
                    if (thu_chi.getId_thu_chi() == list.get(i).getId_thu_chi()){
                        list.remove(list.get(i));
                        break;
                    }
                }
                rcvSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void performSearch(String searchText){
        // Thực hiện tìm kiếm dựa trên searchText
        if (searchText.isEmpty() || searchText == null) {
            // Nếu searchText trống, hiển thị toàn bộ dữ liệu
            // Ví dụ: hiển thị danh sách đầy đủ
            getTenants();

        } else {
            // Nếu có searchText, thực hiện tìm kiếm và hiển thị kết quả
            // Ví dụ: lọc danh sách dựa trên searchText và hiển thị kết quả
            filterAndDisplayData(searchText);
        }
    }
    private void filterAndDisplayData(String searchText) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        // Lọc dữ liệu dựa trên searchText và hiển thị kết quả
        // Ví dụ: lọc danh sách dựa trên searchText và cập nhật RecyclerView hoặc ListView
        String tu_Khoa = searchText.toLowerCase();
        if (tu_Khoa == null || tu_Khoa.isEmpty()){
            getTenants();
        } else {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Thu_Chi");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Thu_Chi> list1 = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Thu_Chi thu_chi = ds.getValue(Thu_Chi.class);
                        String ten = thu_chi.getNgay().toLowerCase();
                        int viTri = ten.indexOf(tu_Khoa);
                        if (viTri != -1) {
                            // Tìm thấy chuỗi con trong chuỗi lớn
                            ten.substring(0, viTri + tu_Khoa.length());
                            list1.add(thu_chi);
                        }

//                                if (tenants.getTen_Nguoi_Thue().equals(tu_Khoa)){
//                                    list1.add(tenants);
//                                }

                    }
                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(Activity_Search.this);
                    linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                    rcv_search.setLayoutManager(linearLayoutManager1);
                    rcvSearchAdapter = new RCV_Thu_Chi_Adapter(list1);
                    rcv_search.setAdapter(rcvSearchAdapter);

                    rcvSearchAdapter.setOnItemClickListener(new RCV_Thu_Chi_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String id, String Id_danh_muc) {
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}