package com.example.traveldiary.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.traveldiary.R;
import com.example.traveldiary.fragment.FragmentBoard;
import com.example.traveldiary.fragment.FragmentBookmark;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MypageActivity extends AppCompatActivity {
    FragmentBoard fragmentBoard;
    FragmentBookmark fragmentBookmark;
    @SuppressLint("StaticFieldLeak")
    public static TextView postCount;
    @SuppressLint("StaticFieldLeak")
    public static TextView bookmarkCount;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        TextView nickName = findViewById(R.id.nickName);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("UI");
        mDatabase.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("info").child("userNickName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickName.setText(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        postCount = findViewById(R.id.postCount);
        bookmarkCount = findViewById(R.id.bookmarkCount);
        loadDataCount();    //bookmark 게시물 개수 불러오기.
        fragmentManager = getSupportFragmentManager();

        TextView logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, StartViewActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), R.string.logout, Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            finish();
        });

        fragmentBoard = new FragmentBoard();
        fragmentManager.beginTransaction().replace(R.id.container, fragmentBoard).commit();

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(R.string.mypage_boarder));
        tabs.addTab(tabs.newTab().setText(R.string.mypage_bookmark));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // position = 0: fragmentBoard 의미함.
                    if (fragmentBoard == null) {
                        fragmentBoard = new FragmentBoard();
                        fragmentManager.beginTransaction().add(R.id.container, fragmentBoard).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .show(fragmentBoard)
                                .hide(fragmentBookmark)
                                .commit();
                    }
                } else {
                    if (fragmentBookmark == null) {
                        fragmentBookmark = new FragmentBookmark();
                        fragmentManager.beginTransaction().add(R.id.container, fragmentBookmark).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .show(fragmentBookmark)
                                .hide(fragmentBoard)
                                .commit();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainViewActivity.class));

        });

        ImageView upload = findViewById(R.id.upload);
        upload.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, UploadCalendarActivity.class));
        });
    }

    //bookmark 게시물 개수를 불러오는 코드
    private void loadDataCount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").whereArrayContains("bookmark", Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().
                addOnSuccessListener(queryDocumentSnapshots -> bookmarkCount.setText(String.valueOf(queryDocumentSnapshots.size())));
    }

    //MypageActivity가 종료되었을때 fragment instance를 종료시킴
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fragmentBoard != null) {
            fragmentBoard.onDestroy();
        }
        if (fragmentBookmark != null) {
            fragmentBookmark.onDestroy();
        }
        postCount = null;
        bookmarkCount = null;
    }
}