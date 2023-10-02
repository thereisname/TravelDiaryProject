package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.R;
import com.example.traveldiary.adapter.MainValueAdapter;
import com.example.traveldiary.fragment.FragmentClient;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class MainViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MainValueAdapter mainValueAdapter;
    private ArrayList<MyPageValue> itemList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);



        String userToken = getIntent().getStringExtra("userToken");

        ImageView myPage = findViewById(R.id.myPage);

        FragmentClient fragmentClient = new FragmentClient(userToken);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragmentClient).commit();


        myPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, MypageActivity.class);
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