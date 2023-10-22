package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.example.traveldiary.R;
import com.example.traveldiary.adapter.MainValueAdapter;
import com.example.traveldiary.fragment.FragmentClient;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.os.Handler;
import android.widget.Toast;

public class MainViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MainValueAdapter adapter;
    private FirebaseFirestore db;
    private int currentPosition = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds

    SlidingUpPanelLayout main_frame;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("로그", "화면나옴");
        setContentView(R.layout.activity_main_view);

        recyclerView = findViewById(R.id.rv_maineRcyclerView);
        adapter = new MainValueAdapter( this);
        recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // 스크롤 상태 감지
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        currentPosition = layoutManager.findFirstVisibleItemPosition();
                        // 현재 아이템 데이터를 가져와서 Fragment로 전달
                        MyPageValue currentItem = adapter.getItem(currentPosition);
                        onItemSelected(currentItem);
                    }
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        db = FirebaseFirestore.getInstance();

        loadDate();

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

    // PagerSnapHelper로 스와이프된 경우 호출되는 메서드
    public void onItemSelected(MyPageValue item) {
        // 현재 아이템 위치에 해당하는 아이템 데이터를 Fragment로 전달
        FragmentClient fragmentClient = new FragmentClient();
        Bundle bundle = new Bundle();
        bundle.putParcelable("myPageValue", item);
        fragmentClient.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_view, fragmentClient)
                .commit();
    }

    // 뒤로가기 두번하면 종료되는 코드
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_INTERVAL);
        }
    }

}