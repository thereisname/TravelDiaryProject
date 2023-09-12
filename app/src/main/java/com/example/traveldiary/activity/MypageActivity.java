package com.example.traveldiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveldiary.R;
import com.example.traveldiary.fragment.FragmentBoard;
import com.example.traveldiary.fragment.FragmentBookmark;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MypageActivity extends AppCompatActivity {
    FragmentBoard fragmentBoard;
    FragmentBookmark fragmentBookmark;
    public static DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        String userToken = getIntent().getStringExtra("userToken");
        TextView logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(getApplicationContext(), "Logout successful.", Toast.LENGTH_SHORT).show();
            System.exit(0);
        });

        fragmentBoard = new FragmentBoard();
        fragmentBookmark = new FragmentBookmark();

        TextView nickName = findViewById(R.id.nickName);
        mDatabase = FirebaseDatabase.getInstance().getReference("UI");
        mDatabase.child("users").child(userToken).child("info").child("userNickName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickName.setText(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentBoard).commit();
        Bundle bundle = new Bundle();
        bundle.putString("userToken", userToken);
        fragmentBoard.setArguments(bundle);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("게시물"));
        tabs.addTab(tabs.newTab().setText("북마크"));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("position", String.valueOf(position));
                Fragment selected = null;
                if (position == 0) {
                    selected = fragmentBoard;
                    fragmentBoard.setArguments(bundle);
                    fragmentBookmark.onDestroy();
                    fragmentBookmark.onDetach();
                } else {
                    selected = fragmentBookmark;
                    fragmentBookmark.setArguments(bundle);
                    fragmentBoard.onDestroy();
                    fragmentBoard.onDetach();
                }
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
            Intent intent = new Intent(this, UploadCalendarActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
        });
    }
}