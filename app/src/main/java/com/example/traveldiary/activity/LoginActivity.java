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
import com.google.firebase.auth.FirebaseAuth;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();

            else
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Intent intent = new Intent(getApplicationContext(), MainViewActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                    }
                });
        });
        register.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
