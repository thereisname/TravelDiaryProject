package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.example.traveldiary.fragment.FragmentClient;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainViewActivity extends AppCompatActivity {




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        String userToken = getIntent().getStringExtra("userToken");

        ImageView myPage = findViewById(R.id.myPage);

        FragmentClient fragmentClient = new FragmentClient(userToken);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragmentClient).commit();






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