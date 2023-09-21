package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.example.traveldiary.fragment.FragmentClient;
import com.example.traveldiary.fragment.FragmentImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainViewActivity extends AppCompatActivity {
    private String userToken;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userToken = currentUser.getUid();
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "알 수 없는 오류가 발생 했습니다. 로그인을 다시 해주세요.", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        ImageView myPage = findViewById(R.id.myPage);
        FragmentImage fragmentImage = new FragmentImage();
        FragmentClient fragmentClient = new FragmentClient();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, fragmentImage).commit();
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