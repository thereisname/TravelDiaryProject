package com.example.traveldiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.traveldiary.R;
import com.example.traveldiary.adapter.MainValueAdapter;
import com.example.traveldiary.fragment.FragmentClient;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MainValueAdapter adapter;
    private FirebaseFirestore db;
    private int currentPosition = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        recyclerView = findViewById(R.id.rv_maineRcyclerView);

        adapter = new MainValueAdapter(this);

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

        loadData();

        FragmentClient fragmentClient = new FragmentClient();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragmentClient).commit();

        EditText search = findViewById(R.id.search);
        search.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                searchLoadData(Arrays.asList(search.getText().toString()));
            }
            return false;
        });

        //필터 화면 설정.
        ConstraintLayout filterView = findViewById(R.id.filterView);
        search.setOnClickListener(v -> {
            if (filterView.getVisibility() == View.GONE) {
                filterView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                filterView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        //필터 적용 하여 output code.
        AppCompatButton filterSearchBtn = findViewById(R.id.filterSearchBtn);
        filterSearchBtn.setOnClickListener(v -> {
            searchLoadData(hashTagCustom());
            filterView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.home).setOnClickListener(v -> {
            loadData();
        });

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

    private List<String> hashTagCustom() {
        List<String> arrayHashTag = new ArrayList<>();
        if (((Chip) findViewById(R.id.chip1)).isChecked()) arrayHashTag.add("#혼자 여행 ");
        if (((Chip) findViewById(R.id.chip2)).isChecked()) arrayHashTag.add("#커플 여행 ");
        if (((Chip) findViewById(R.id.chip3)).isChecked()) arrayHashTag.add("#친구와 여행 ");
        if (((Chip) findViewById(R.id.chip4)).isChecked()) arrayHashTag.add("#가족 여행 ");

        if (((Chip) findViewById(R.id.chip10)).isChecked()) arrayHashTag.add("#계획적인 ");
        if (((Chip) findViewById(R.id.chip11)).isChecked()) arrayHashTag.add("#자유로운 ");
        if (((Chip) findViewById(R.id.chip12)).isChecked()) arrayHashTag.add("#휴가 ");
        if (((Chip) findViewById(R.id.chip13)).isChecked()) arrayHashTag.add("#추억 ");
        if (((Chip) findViewById(R.id.chip14)).isChecked()) arrayHashTag.add("#힐링 ");
        if (((Chip) findViewById(R.id.chip15)).isChecked()) arrayHashTag.add("#엑티비티 ");
        if (((Chip) findViewById(R.id.chip16)).isChecked()) arrayHashTag.add("#맛집투어 ");
        if (((Chip) findViewById(R.id.chip17)).isChecked()) arrayHashTag.add("#낭만 ");
        if (((Chip) findViewById(R.id.chip18)).isChecked()) arrayHashTag.add("#감성 ");

        return arrayHashTag;
    }

    public void loadData() {
        db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            adapter = new MainValueAdapter(this);
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                MyPageValue mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                adapter.addItem(mp);
            }
            recyclerView.setAdapter(adapter);
        });
    }

    public void searchLoadData(List<String> searchList) {
        db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).whereArrayContainsAny("hashTag", searchList).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() != 0) {
                        adapter = new MainValueAdapter(this);
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            MyPageValue mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                            adapter.addItem(mp);
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "검색 결과가 존재하기 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(command -> Toast.makeText(this, "검색 결과가 존재하기 않습니다.", Toast.LENGTH_SHORT).show());
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