package com.example.traveldiary.fragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.traveldiary.R;

public class FragmentClient extends Fragment {
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;

        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);

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