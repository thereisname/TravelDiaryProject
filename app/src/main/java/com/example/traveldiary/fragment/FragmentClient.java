package com.example.traveldiary.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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

    private String TAG = "로그";

    private MyPageValue mp;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        TextView fragment_hashtag = view.findViewById(R.id.fragment_hashtag);
        TextView uploadDate = view.findViewById(R.id.uploadDate);
        TextView con = view.findViewById(R.id.con);

        db.collection("data").whereNotEqualTo("userToken", getUserToken()).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                fragment_title.setText(mp.getTitle());
                fragment_hashtag.setText(mp.getHashTag());
                con.setText(Html.fromHtml(mp.getCon(), Html.FROM_HTML_MODE_LEGACY));
                uploadDate.setText(getString(R.string.uploadBoard_uploadDate, mp.getUploadDate()));
            }
        });

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Image/pMp11v28f4dXE22tSta0").listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(command -> {
                    Log.d("로그1", String.valueOf(command));
                });
            }
        }).addOnFailureListener(command -> Log.d("error", "불러오기 실패."));

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

    private void imageDown(String userToken, String str) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        ArrayList<Integer> arrayStartIndex = new ArrayList<Integer>();
//        ArrayList<Integer> arrayEndIndex = new ArrayList<Integer>();
//        for(int i = 0; i< str.length(); i++){
//            if(str.charAt(i) == '<' && str.charAt(i+1)=='i'&& str.charAt(i+2) == 'm'){
//                arrayStartIndex.add(i+10);
//            }
//            if(str.charAt(i) =='>'&& str.charAt(i-1) == '"' && str.charAt(i-2) == '0'){
//                arrayEndIndex.add(i-20);
//            }
//        }
//
//        int count = arrayStartIndex.size();
//
//        while(count >= 0){


    }
}