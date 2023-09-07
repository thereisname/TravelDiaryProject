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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class FragmentClient extends Fragment {
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private  TextView con;

    public FragmentClient(String userToken) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        LoginActivity.db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        con = view.findViewById(R.id.con);

        DocumentReference docRef = LoginActivity.db.collection("data").document("one");

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.CACHE;

        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Document found in the offline cache
                DocumentSnapshot document = task.getResult();
                con.setText(Html.fromHtml((String) document.get("con"), Html.FROM_HTML_MODE_LEGACY));
            }
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