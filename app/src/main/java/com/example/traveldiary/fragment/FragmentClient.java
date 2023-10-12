package com.example.traveldiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FragmentClient extends Fragment {
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private MyPageValue mp;
    private LinearLayout listView;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        TextView fragment_hashtag = view.findViewById(R.id.fragment_hashtag);

        listView = view.findViewById(R.id.listView);
        db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                        ContentDownloadAdapter contentDownloadAdapter = new ContentDownloadAdapter(getActivity(), listView, mp);
                        contentDownloadAdapter.checkText();
                        fragment_title.setText(mp.getTitle());
                        fragment_hashtag.setText(mp.getHashTag());
                    }
                });
        return view;
    }
}