package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.R;
import com.example.traveldiary.adapter.MainValueAdapter;
import com.example.traveldiary.fragment.FragmentClient;

import com.example.traveldiary.value.MyPageValue;

import java.util.ArrayList;

public class MainViewActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        RecyclerView mRecyclerView = findViewById(R.id.rv_maineRcyclerView);
        MainValueAdapter mainValueAdapter = new MainValueAdapter();

        mRecyclerView.setAdapter(mainValueAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        ArrayList<MyPageValue> item = new ArrayList<>();

        mainValueAdapter.setAdapterList(item);

        FragmentClient fragmentClient = new FragmentClient();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragmentClient).commit();

        ImageView myPage = findViewById(R.id.myPage);
        myPage.setOnClickListener(v -> {
            startActivity(new Intent(this, MypageActivity.class));
            finish();
        });

        ImageView upload = findViewById(R.id.upload);
        upload.setOnClickListener(v -> {
            startActivity(new Intent(this, UploadCalendarActivity.class));
            finish();
        });
    }
}