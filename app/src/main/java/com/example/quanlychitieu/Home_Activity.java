package com.example.quanlychitieu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class Home_Activity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private boolean doubleBackToExitPressedOnce = false;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        anhxa();
        click();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new Trang_Chu_Fragment());
        transaction.commit();
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_home){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new Trang_Chu_Fragment())
                            .commit();
                    bottomNavigationView.getMenu().findItem(R.id.item_home).setChecked(true);
                    return true;
                } else if (item.getItemId() == R.id.item_bar){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new Bieu_Do_Fragment())
                            .commit();
                    bottomNavigationView.getMenu().findItem(R.id.item_stacked).setChecked(true);
                    return true;
                }else if (item.getItemId() == R.id.item_stacked){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new Bao_Cao_Fragment())
                            .commit();
                    bottomNavigationView.getMenu().findItem(R.id.item_stacked).setChecked(true);
                    return true;
                } else if (item.getItemId() == R.id.item_person){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new Person_Fragment())
                            .commit();
                    bottomNavigationView.getMenu().findItem(R.id.item_person).setChecked(true);
                    return true;
                }
                return false;
            }
        });




    }

    private void anhxa(){
        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        fab = findViewById(R.id.btn_fab);

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ấn thoát lần nữa để thoát", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private void click(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(Home_Activity.this, Them_Thu_Chi_Activity.class));
            }
        });

    }

}