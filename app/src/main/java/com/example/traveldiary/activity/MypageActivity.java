package com.example.traveldiary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.traveldiary.R;
import com.example.traveldiary.fragment.FragmentBoard;
import com.example.traveldiary.fragment.FragmentBookmark;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;

public class MypageActivity extends AppCompatActivity {
    FragmentBoard fragmentBoard;
    FragmentBookmark fragmentBookmark;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        String userToken = getIntent().getStringExtra("userToken");


        fragmentBoard = new FragmentBoard();
        fragmentBookmark = new FragmentBookmark();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentBoard).commit();
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("게시물"));
        tabs.addTab(tabs.newTab().setText("북마크"));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("position", String.valueOf(position));
                Fragment selected = null;
                if (position == 0)
                    selected = fragmentBoard;
                else
                    selected = fragmentBookmark;

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainViewActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
        });

        ImageView upload = findViewById(R.id.upload);
        upload.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
        });
    }
}