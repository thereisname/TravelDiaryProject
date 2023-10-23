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

import com.example.traveldiary.R;
import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class FragmentClient extends Fragment {
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private ImageButton bookmark;
    private static MyPageValue mp;
    private LinearLayout listView;
    FirebaseFirestore db;

    public FragmentClient() {
    }

    public static FragmentClient newInstance(MyPageValue myPageValue) {
        FragmentClient fragment = new FragmentClient();
        Bundle args = new Bundle();
        args.putParcelable("myPageValue", myPageValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        TextView fragment_hashtag = view.findViewById(R.id.fragment_hashtag);
        listView = view.findViewById(R.id.listView);

        if (getArguments() != null) {
            mp = getArguments().getParcelable("myPageValue");
            fragment_title.setText(mp.getTitle());
            fragment_hashtag.setText(mp.getHashTag());
            ContentDownloadAdapter contentDownloadAdapter = new ContentDownloadAdapter(getActivity(), listView, mp);
            contentDownloadAdapter.checkText();
            if (mp.getBookmark().contains(FirebaseAuth.getInstance().getUid())) {
                bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                isAttBookmark = 1;
            }
        } else {
            db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).limit(1).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                                    ContentDownloadAdapter contentDownloadAdapter = new ContentDownloadAdapter(getActivity(), listView, mp);
                                    contentDownloadAdapter.checkText();
                                    fragment_title.setText(mp.getTitle());
                                    fragment_hashtag.setText(mp.getHashTag());
                                    if (mp.getBookmark().contains(FirebaseAuth.getInstance().getUid())) {
                                        bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                                        isAttBookmark = 1;
                                    }
                                }
                            }
                    );
        }

        bookmark.setOnClickListener(v -> {
            List<String> bookmarkArray = mp.getBookmark();
            if (isAttBookmark == 0) {
                bookmarkArray.add(FirebaseAuth.getInstance().getUid());
                db.collection("data").document(mp.getBoardID()).update("bookmark", bookmarkArray).addOnSuccessListener(command -> {
                    isAttBookmark = 1;
                    bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                });
            } else {
                bookmarkArray.remove(FirebaseAuth.getInstance().getUid());
                db.collection("data").document(mp.getBoardID()).update("bookmark", bookmarkArray).addOnSuccessListener(command -> {
                    isAttBookmark = 0;
                    bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
                });
            }
        });
        return view;
    }
}