package com.example.traveldiary.fragment;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<Uri> imagess = new ArrayList<Uri>();
    ArrayList<Integer> arrayStartIndex = new ArrayList<Integer>();
    ArrayList<Integer> arrayEndIndex = new ArrayList<Integer>();
    int num = 0;

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
                fragment_title.setText(mp.getTitle());
                fragment_hashtag.setText(mp.getHashTag());
                checkText(mp);
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
        Imagedown(mp.getBoardID(), arrayStartIndex, arrayEndIndex);
    }

    // Image 다운로드 함수
    private void Imagedown(String boardID, ArrayList<Integer> arrayStartIndex, ArrayList<Integer> arrayEndIndex) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Image/" + boardID).listAll().addOnSuccessListener(listResult -> {

            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(command -> {
                    imagess.add(command);
                    Log.d("로그1", String.valueOf(command) + "uri가져오기");
                    Log.d("로그1", String.valueOf(imagess.size()) + "첫 번째 imagess 사이지 확인");
                    Log.d("로그1", imagess.get(0).toString() + "imagess 인덱스 첫번째 가져오기");

                    imagess.add(command);

                    if (arrayStartIndex.get(0) != 0) {
                        String str = mp.getCon().substring(0, arrayStartIndex.get(0));
                        createTextView(str);
                    } else {
                        for (int i = 0; i < arrayStartIndex.size(); i++) {
                            createImageView(imagess.get(i));
                            if (i == arrayStartIndex.size() - 1) {
                                String str = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                                createTextView(str);
                            } else {
                                String str = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                                createTextView(str);
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(command -> Toast.makeText(getActivity().getApplicationContext(), "이미지 로드를 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void createImageView(Uri img) {
        ImageView imageView = new ImageView(getContext());
        Glide.with(getContext()).load(img).into(imageView);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(param);

        listView.addView(imageView);
    }

    int count = 0;

    private void createTextView(String str) {
        TextView textViewNm = new TextView(getActivity());
        textViewNm.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString());
        textViewNm.setTextSize(15);
        textViewNm.setTextColor(Color.rgb(0, 0, 0));
        textViewNm.setBackgroundColor(Color.rgb(184, 236, 184));
        textViewNm.setId(count);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewNm.setLayoutParams(param);
        listView.addView(textViewNm);
        count++;
    }
}