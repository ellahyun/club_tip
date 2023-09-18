package com.example.club_tip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    TabLayout tip_tabLayout1;
    ViewPager2 tip_vp1;
    ViewpagerAdapter viewpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // (피드, 다이어리, 팁쳐)의 탭레이아웃, 프레그먼트 어뎁터 연결
        tip_tabLayout1 = findViewById(R.id.tip_tabLayout1);
        tip_vp1 = findViewById(R.id.tip_vp1);

        viewpagerAdapter = new ViewpagerAdapter(this);
        tip_vp1.setAdapter(viewpagerAdapter);



        new TabLayoutMediator(tip_tabLayout1, tip_vp1, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("NO TIME피드");
                    break;

                case 1:
                    tab.setText("YES! 다이어리");
                    break;

                case 2:
                    tab.setText("나만의 팁쳐");
                    break;
            }
        }).attach();
    }
}