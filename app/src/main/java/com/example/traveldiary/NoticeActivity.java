package com.example.traveldiary;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.adapter.NoticeAdapter;
import com.example.traveldiary.value.NoticeValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NoticeActivity extends AppCompatActivity implements OnNoticeClickListener{
    private RecyclerView recyclerView;
    private NoticeAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoticeAdapter(this, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadData();
    }

    public void loadData() {
        db.collection("notice").orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                NoticeValue mp = queryDocumentSnapshot.toObject(NoticeValue.class);
                adapter.addItem(mp);
            }
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onItemSelected(View view, int position, ArrayList<NoticeValue> items) {

    }
}