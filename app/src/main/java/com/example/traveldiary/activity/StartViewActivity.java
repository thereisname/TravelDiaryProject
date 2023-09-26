package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class StartViewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private PermissionSupport permission;

    @Override
    protected void onStart() {
        super.onStart();

        permissionCheck();  // 권한 체크

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) // 자동 로그인.
            startActivity(new Intent(this, MainViewActivity.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_view);
        getWindow().setWindowAnimations(0);
        mAuth = FirebaseAuth.getInstance();

        Button startlogin_btn = findViewById(R.id.startlogin_btn);
        startlogin_btn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.start_btn).setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // 권한 체크
    private void permissionCheck() {
        // sdk 23버전 이하 버전에서는 permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= 23) {
            // 클래스 객체 생성
            permission = new PermissionSupport(this, this);
            // 권한 체크한 후에 리턴이 false일 경우 권한 요청을 해준다.
            if (!permission.checkPermission()) permission.requestPermission();
        }
    }

    // Request Permission에 대한 결과 값을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 리턴이 false일 경우 다시 권한 요청
        if (!permission.permissionResult(requestCode, permissions, grantResults))
            permission.requestPermission();
    }
}
