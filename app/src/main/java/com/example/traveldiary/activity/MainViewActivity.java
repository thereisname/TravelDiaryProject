package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.R;
import com.example.traveldiary.fragment.FragmentClient;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;


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