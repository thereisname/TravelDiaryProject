package com.example.traveldiary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class ContentDownloadAdapter {
    private ArrayList<Integer> arrayStartIndex = new ArrayList<>();
    private ArrayList<Integer> arrayEndIndex = new ArrayList<>();
    private ArrayList<View> arrayImage = new ArrayList();
    private LinearLayout listView;
    private Context context;
    private MyPageValue mp;
    ArrayList<Uri> imageUri;


    public ContentDownloadAdapter(Context context, LinearLayout listView, MyPageValue mp) {
        this.context = context;
        this.listView = listView;
        this.mp = mp;
    }

    // 문자에서 이미지 시작과 끝을 가져오기
    public int checkText() {
        String con = mp.getCon();

        for (int index = 0; index < con.length(); index++) {
            if (con.charAt(index) == '<' && con.charAt(index + 1) == 'i' && con.charAt(index + 2) == 'm') {
                arrayStartIndex.add(index);
            }
            if (con.charAt(index) == '>' && con.charAt(index - 1) == '"' && con.charAt(index - 2) == '0') {
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
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1);
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
                        String str2 = mp.getCon().substring(arrayEndIndex.get(i) + 1);
                        createTextView(str2);
                    } else {
                        String str3 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str3);
                    }
                }
            }
        }
        ImageDown(mp.getBoardID());

        return arrayStartIndex.size();
    }

    // Image 다운로드 함수
    private void ImageDown(String boardID) {
        imageUri = new ArrayList<>();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Image/" + boardID).listAll().addOnSuccessListener(listResult -> {
            for (int i = 1; i < listResult.getItems().size(); i++) {
                StorageReference item = listResult.getItems().get(i);
                int finalI = i - 1;
                try {
                    item.getDownloadUrl().addOnSuccessListener(command -> {
                        imageUri.add(command);
                        Glide.with(context).load(command).into(((ImageView) arrayImage.get(finalI)));
                    });
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    int imageId = 10000;

    private void createImageView() {
        ImageView imageView = new ImageView(context);
        imageView.setId(imageId);
        Glide.with(context).load(R.drawable.baseline_image_24).into(imageView);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(param);
        arrayImage.add(imageView);
        listView.addView(imageView);
        imageId++;
    }

    private void createTextView(String str) {
        TextView textViewNm = new TextView(context);
        textViewNm.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
        textViewNm.setTextSize(15);
        textViewNm.setTextColor(Color.rgb(0, 0, 0));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewNm.setLayoutParams(param);
        listView.addView(textViewNm);
    }

    public String checkTextEdit() {
        ArrayList<Integer> strImgStartIndex = new ArrayList<>();
        ArrayList<Integer> strImgEndIndex = new ArrayList<>();

        // 이미지 링크 추출하는 loop
        for (int i = 0; i < mp.getCon().length(); i++) {
            if (mp.getCon().charAt(i) == '<' && mp.getCon().charAt(i + 1) == 'i') {
                strImgStartIndex.add(i + 10);
            }
            if (mp.getCon().charAt(i) == '>' && mp.getCon().charAt(i - 2) == '0' && mp.getCon().charAt(i - 3) == '2') {
                strImgEndIndex.add(i - 21);
            }
        }
        // 이미지 링크 자리에 image 번호로 대체
        int count = strImgStartIndex.size();
        while (count > 0) {
            mp.setCon(mp.getCon().replace(mp.getCon().substring(strImgStartIndex.get(count - 1), strImgEndIndex.get(count - 1)), imageUri.get(count - 1).toString()));
            count--;
        }
        return mp.getCon();
    }

    public ArrayList<String> downLoadImage() {
        ArrayList<String> localArray = new ArrayList<>();
        // 폴더 생성
        String folderName = "TraveFolder";
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName);
        dir.mkdir();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://traveldiary-356ee.appspot.com");

        for (int i = 0; i < arrayImage.size(); i++) {
            // 파일에 저장할 이름 결정
            String imageName = "contentImage" + mp.getVersion() + i + ".jpg";
            File localFile = new File(dir, imageName); // 파일 경로 및 이름 지정
            localArray.add(String.valueOf(localFile));
            //다운로드할 파일을 가르키는 참조 만들기
            StorageReference pathReference = storageReference.child("Image").child(mp.getBoardID()).child(imageName);
            pathReference.getFile(localFile);
        }
        return localArray;
    }
}
