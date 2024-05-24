package com.example.quanlychitieu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Bao_Cao_Fragment extends Fragment {

    private View view;
    private DatabaseReference mDatabase;
    private PieChart pieChart;
    private Spinner monthSpinner;
    private FirebaseUser currentUser;
    private ListView legendListView;
    private TextView tvConLai, tvNganSach, tvChiPhi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bao_cao_fragment, container, false);
        anhXa();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Thu_Chi");

            layDanhSachThang();
            thietLapSuKienSpinner();
        }

        return view;
    }

    private void anhXa() {
        pieChart = view.findViewById(R.id.bieu_do);
        monthSpinner = view.findViewById(R.id.sp);
        legendListView = view.findViewById(R.id.legendListView);
        tvChiPhi = view.findViewById(R.id.tv_chi_phi);
        tvNganSach = view.findViewById(R.id.tv_ngan_sach);
        tvConLai = view.findViewById(R.id.tv_con_lai);
    }

    private void layDanhSachThang() {
        List<String> monthList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthList.add(String.format("%02d", i));
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Tự động chọn tháng hiện tại
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonth);
    }

    private void thietLapSuKienSpinner() {
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = monthSpinner.getSelectedItem().toString();
                layDuLieuBieuDoChoThang(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có mục nào được chọn
            }
        });
    }

    private void layDuLieuBieuDoChoThang(final String month) {
        String idUser = currentUser.getUid();
        mDatabase.orderByChild("id_User").equalTo(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalThu = 0;
                float totalChi = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userID = snapshot.child("id_User").getValue(String.class);

                    if (userID != null && userID.equals(currentUser.getUid())) {
                        String date = snapshot.child("ngay").getValue(String.class);
                        String snapshotMonth = layThangTuNgay(date);
                        String loaiThuChi = snapshot.child("loai_thu_chi").getValue(String.class);
                        String soTienStr = snapshot.child("so_tien").getValue(String.class);

                        if (snapshotMonth.equals(month) && soTienStr != null) {
                            try {
                                float amount = Float.parseFloat(soTienStr);

                                if ("Thu nhập".equals(loaiThuChi)) {
                                    totalThu += amount;
                                } else if ("Chi phí".equals(loaiThuChi)) {
                                    totalChi += amount;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                Map<String, Float> totalMap = new HashMap<>();
                totalMap.put("Tổng Thu", totalThu);
                totalMap.put("Tổng Chi", totalChi);

                taoBieuDoTron(totalMap);

                float conLai = totalThu - totalChi;
                tvChiPhi.setText(CurrencyFormatter.formatVND(totalChi));
                tvNganSach.setText(CurrencyFormatter.formatVND(totalThu));
                tvConLai.setText(CurrencyFormatter.formatVND(conLai));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    private String layThangTuNgay(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            return String.format("%02d", cal.get(Calendar.MONTH) + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void taoBieuDoTron(Map<String, Float> categoryAmountMap) {
        List<PieEntry> entries = new ArrayList<>();
        List<String> legendEntries = new ArrayList<>();
        float totalAmount = 0;

        for (Map.Entry<String, Float> entry : categoryAmountMap.entrySet()) {
            totalAmount += Math.abs(entry.getValue());
        }

        for (Map.Entry<String, Float> entry : categoryAmountMap.entrySet()) {
            String categoryName = entry.getKey();
            float amount = Math.abs(entry.getValue());
            float percentage = (amount / totalAmount) * 100;
            entries.add(new PieEntry(percentage, categoryName));
            legendEntries.add(categoryName + ": " + CurrencyFormatter.formatVND(amount) + " (" + String.format("%.2f", percentage) + "%)");
        }

        PieDataSet dataSet = new PieDataSet(entries, "Giao dịch hàng tháng");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        ArrayAdapter<String> legendAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, legendEntries);
        legendListView.setAdapter(legendAdapter);
    }
}
