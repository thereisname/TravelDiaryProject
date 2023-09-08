package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.example.traveldiary.SHA256;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    public static FirebaseFirestore db;   // 게시물 저장 DB
    public static FirebaseStorage storage;    // 이미지 저장 DB
    private FirebaseAuth mAuth;

    private PermissionSupport permission;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 권한 체크 함수 불러오기
        permissionCheck();

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        EditText emailText = findViewById(R.id.login_email);
        EditText pwText = findViewById(R.id.login_pw);
        Button login = findViewById(R.id.loginBtn);
        Button register = findViewById(R.id.registerBtn);

        login.setOnClickListener(v -> {
            String email = emailText.getText().toString().trim();
            String password;
            SHA256 sha256 = new SHA256();
            try {
                password = sha256.encrypt(sha256.encrypt(pwText.getText().toString().trim()));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            if (email.equals("") || password.equals(""))
                Toast.makeText(this, "Please enter email, password.", Toast.LENGTH_SHORT).show();
            else
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Intent intent = new Intent(getApplicationContext(), MainViewActivity.class);
                        intent.putExtra("userToken",mAuth.getCurrentUser().getUid());
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
        });
        register.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));

        Log.d("로그", mAuth.getCurrentUser().getEmail().toString());
    }

    // 권한 체크
    private void permissionCheck(){
        // sdk 23버전 이하 버전에서는 permission이 필요하지 않음
        if(Build.VERSION.SDK_INT >= 23){

            // 클래스 객체 생성
            permission =  new PermissionSupport(this, this);

            // 권한 체크한 후에 리턴이 false일 경우 권한 요청을 해준다.
            if(!permission.checkPermission()){
                permission.requestPermission();
            }
        }
    }


    // Request Permission에 대한 결과 값을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 리턴이 false일 경우 다시 권한 요청
        if (!permission.permissionResult(requestCode, permissions, grantResults)){
            permission.requestPermission();
        }
    }
}
