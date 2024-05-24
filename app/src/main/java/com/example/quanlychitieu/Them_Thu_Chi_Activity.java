package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Them_Thu_Chi_Activity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tvHuy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_thu_chi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhxa();
        click();

        // Tạo adapter cho ViewPager
        ViewPagerTabLayoutAdapter adapter = new ViewPagerTabLayoutAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        // Thêm các fragment tùy chỉnh vào adapter sau khi dialog được hiển thị
        adapter.addFragment(new Fragment_Them_Chi(), "Chi phí");
        adapter.addFragment(new Fragment_Them_Thu(), "Thu nhập");
        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager
        tabLayout.setupWithViewPager(viewPager);



    }
    private void anhxa(){
        tabLayout = findViewById(R.id.tabLayout_them_thu_chi);
        viewPager = findViewById(R.id.viewPager_them_thu_chi);
        tvHuy = findViewById(R.id.tv_huy);
    }

    private void click(){
        tvHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(Them_Thu_Chi_Activity.this, Home_Activity.class));
            }
        });
    }
}