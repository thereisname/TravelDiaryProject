package com.example.traveldiary.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.traveldiary.R;
import com.example.traveldiary.activity.LoginActivity;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FragmentClient extends Fragment {
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private String userToken;

    public FragmentClient(String userToken) {
        setUserToken(userToken);
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        LoginActivity.db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        TextView fragment_hashtag = view.findViewById(R.id.fragment_hashtag);
        TextView uploadDate = view.findViewById(R.id.uploadDate);
        TextView con = view.findViewById(R.id.con);

        LoginActivity.db.collection("data").whereNotEqualTo("userToken", getUserToken()).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                MyPageValue mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                fragment_title.setText(mp.getTitle());
                fragment_hashtag.setText(mp.getHashTag());
                con.setText(Html.fromHtml(mp.getCon(), Html.FROM_HTML_MODE_LEGACY));
                uploadDate.setText(getString(R.string.uploadBoard_uploadDate, mp.getUploadDate()));
            }
            ;
        });

        bookmark.setOnClickListener(v -> {
            if (isAttBookmark == 1) {
                bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
                isAttBookmark = 0;
            } else {
                bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                isAttBookmark = 1;
            }
        });
        return view;
    }
}