package com.example.traveldiary.fragment;

import static java.lang.Thread.sleep;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FragmentClient extends Fragment {
    private int isAttBookmark; // 0: 북마크 비활성화 상태, 1: 북마크 활성화 상태
    private MyPageValue mp;
    private LinearLayout listView;
    ImageView imageView;
    ArrayList<Integer> arrayStartIndex = new ArrayList<Integer>();
    ArrayList<Integer> arrayEndIndex = new ArrayList<Integer>();
    ArrayList<View> arrayimage = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAttBookmark = 0;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        ImageButton bookmark = view.findViewById(R.id.bookMarkBtn);
        TextView fragment_title = view.findViewById(R.id.fragment_title);
        TextView fragment_hashtag = view.findViewById(R.id.fragment_hashtag);

        listView = view.findViewById(R.id.listView);
        db.collection("data").whereNotEqualTo("userToken", FirebaseAuth.getInstance().getUid()).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                checkText(mp);
                fragment_title.setText(mp.getTitle());
                fragment_hashtag.setText(mp.getHashTag());
            }
        });
        return view;
    }

    // 문자에서 이미지  시작과 끝을 가져오기
    private void checkText(MyPageValue mp) {
        String str = mp.getCon();
        for (int index = 0; index < str.length(); index++) {
            if (str.charAt(index) == '<' && str.charAt(index + 1) == 'i' && str.charAt(index + 2) == 'm') {
                arrayStartIndex.add(index);
            }
            if (str.charAt(index) == '>' && str.charAt(index - 1) == '"' && str.charAt(index - 2) == '0') {
                arrayEndIndex.add(index);
            }
        }
        // 이미지 가져오기
        if (arrayStartIndex.size() == 0) {
            createTextView(mp.getCon());
        } else {
            if (arrayStartIndex.get(0) != 0) {
                String str0 = mp.getCon().substring(0, arrayStartIndex.get(0));
                createTextView(str0);
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                        createTextView(str1);
                    } else {
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str1);
                    }
                }
            } else {
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str2 = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                        createTextView(str2);
                    } else {
                        String str3 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str3);
                    }
                }
            }
        }

        try {
            sleep(1000);
            Imagedown(mp.getBoardID());
        } catch (Exception e) {

        }
    }

    // Image 다운로드 함수
    private void Imagedown(String boardID) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Image/" + boardID).listAll().addOnSuccessListener(listResult -> {
            for (int i = 0; i < listResult.getItems().size(); i++) {
                StorageReference item = listResult.getItems().get(i);
                int finalI = i;
                item.getDownloadUrl().addOnSuccessListener(command -> Glide.with(getContext()).load(command).into(((ImageView) arrayimage.get(finalI))));
            }
        });
    }

    int imageId = 10000;

    private void createImageView() {
        imageView = new ImageView(getContext());
        imageView.setId(imageId);
        Glide.with(getContext()).load(R.drawable.baseline_image_24).into(imageView);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(param);
        arrayimage.add(imageView);
        listView.addView(imageView);
        imageId++;
    }

    private void createTextView(String str) {
        TextView textViewNm = new TextView(getActivity());
        textViewNm.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString());
        textViewNm.setTextSize(15);
        textViewNm.setTextColor(Color.rgb(0, 0, 0));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewNm.setLayoutParams(param);
        listView.addView(textViewNm);
    }
}