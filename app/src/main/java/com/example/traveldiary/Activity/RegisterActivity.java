package com.example.traveldiary.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.example.traveldiary.SHA256;
import com.example.traveldiary.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
    // 1. DB 읽거나 쓰기 위해서 DatabaseReference 인스턴스 필요.
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private EditText inputNickName, inputPw, inputRePw, inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("UI");

        Button confirm_button = findViewById(R.id.confirm_button);
        inputNickName = findViewById(R.id.inputNickName);
        inputPw = findViewById(R.id.inputPw);
        inputRePw = findViewById(R.id.inputRePw);
        inputEmail = findViewById(R.id.inputEmail);

        confirm_button.setOnClickListener(v -> {
            SHA256 sha256 = new SHA256();

            String strNickName = inputNickName.getText().toString();
            String strPw;
            String strRePw;
            try {
                strPw = sha256.encrypt(sha256.encrypt(inputPw.getText().toString()));
                strRePw = sha256.encrypt(sha256.encrypt(inputRePw.getText().toString()));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            String strEmail = inputEmail.getText().toString();

            if (strPw.equals(strRePw)) {
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPw).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // 가입 성공
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        UserAccount account = new UserAccount();
                        account.setIdToken(firebaseUser.getUid());
                        account.setUserEmail(firebaseUser.getEmail());
                        account.setUserNickName(strNickName);

                        mDatabase.child("users").child(firebaseUser.getUid()).setValue(account);
                        Toast.makeText(this, "회원가입에 성공하였습니다.", Toast.LENGTH_LONG).show();
                        System.exit(0);
                    } else
                        Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                });
            }

        });
    }
}
