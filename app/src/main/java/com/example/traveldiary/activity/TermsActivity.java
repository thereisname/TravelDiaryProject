package com.example.traveldiary.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.traveldiary.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        TextView termsCon2 = findViewById(R.id.terms_con2);
        TextView termsCon = findViewById(R.id.terms_con);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("terms").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                termsCon.setText(Html.fromHtml(queryDocumentSnapshot.get("terms").toString(), Html.FROM_HTML_MODE_LEGACY));
                termsCon2.setText(Html.fromHtml(queryDocumentSnapshot.get("Personal information Processing Policy").toString(), Html.FROM_HTML_MODE_LEGACY));
            }
        });
    }
}