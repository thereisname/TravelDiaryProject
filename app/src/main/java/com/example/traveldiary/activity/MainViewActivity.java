package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.adapter.MainValueAdapter;
import com.example.traveldiary.fragment.FragmentClient;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class MainViewActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView recyclerView;
    private MainValueAdapter adapter;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("로그", "화면나옴");
        setContentView(R.layout.activity_main_view);

        recyclerView = findViewById(R.id.rv_maineRcyclerView);

        adapter = new MainValueAdapter( getApplicationContext(),this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        db = FirebaseFirestore.getInstance();

        loadDate();


        //---------------------------------------------------------------------------------------
        FragmentClient fragmentClient = new FragmentClient();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragmentClient).commit();

        ImageView myPage = findViewById(R.id.myPage);
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

    public void loadDate() {
        db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                MyPageValue mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                adapter.addItem(mp);
            }
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onItemSelected(View view, int position, ArrayList<MyPageValue> items) {
        MyPageValue item = items.get(position);

        Log.d("로그1", item.getTitle());

        FragmentClient fragmentClient = FragmentClient.newInstance(item);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragmentClient).commit();
    }
}