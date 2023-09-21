package com.example.traveldiary.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.example.traveldiary.SHA256;
import com.example.traveldiary.value.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
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
        inputEmail = findViewById(R.id.inputEmail);
        inputPw = findViewById(R.id.inputPw);
        inputRePw = findViewById(R.id.inputRePw);
        confirm_button.setOnClickListener(v -> {
            if (inputNickName.length() > 0 && inputEmail.length() > 0 && inputPw.length() > 0 && inputRePw.length() > 0) {
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

                            mDatabase.child("users").child(firebaseUser.getUid()).child("info").setValue(account);
                            Toast.makeText(getApplicationContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

