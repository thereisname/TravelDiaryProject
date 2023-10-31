package com.example.traveldiary.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.traveldiary.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContentUploadAdapter {
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private Context context;
    private ArrayList<Integer> strImgStartIndex = new ArrayList<>();
    private ArrayList<Integer> strImgEndIndex = new ArrayList<>();

    public ContentUploadAdapter() {
    }

    public ContentUploadAdapter(Context context) {
        this.context = context;
    }

    public String changeText(String str) {
        // 이미지 링크 추출하는 loop
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '<' && str.charAt(i + 1) == 'i') {
                strImgStartIndex.add(i + 10);
            }
            if (str.charAt(i) == '>' && str.charAt(i - 2) == '0' && str.charAt(i - 3) == '2') {
                strImgEndIndex.add(i - 21);
            }
        }
        //문자에서 Uri를 추출
        for (int i = 0; i < strImgStartIndex.size(); i++) {
            Uri img = Uri.parse(str.substring(strImgStartIndex.get(i), strImgEndIndex.get(i)));
            uriArrayList.add(img);
        }
        // 이미지 링크 자리에 image 번호로 대체
        int count = strImgStartIndex.size();
        while (count > 0) {
            str = str.replace(str.substring(strImgStartIndex.get(count - 1), strImgEndIndex.get(count - 1)), "image" + count);
            count--;
        }
        return str;
    }

    public void uploadImage(String docID, Uri mainImage) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // 다수의 이미지를 넣기 위해 for문 사용
        for (int index = 0; index < uriArrayList.size(); index++) {
            // content 이미지들 DB에 올리기.
            StorageReference imgRef = storageRef.child("Image").child(docID).child("contentImage0" + index + "." + getFileExtension(uriArrayList.get(index)));
            imgRef.putFile(uriArrayList.get(index)).addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl()
                            .addOnSuccessListener(uri1 -> Toast.makeText(context, R.string.upload_image_successful, Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(context, R.string.upload_image_fail, Toast.LENGTH_SHORT).show());
        }
        // mainImage DB에 올리기.
        StorageReference mImgRef = storageRef.child("Image").child(docID).child("MainImage" + "." + getOnlyExtension(String.valueOf(mainImage)));
        mImgRef.putFile(mainImage).addOnSuccessListener(taskSnapshot -> mImgRef.getDownloadUrl()
                        .addOnSuccessListener(uri1 -> Toast.makeText(context, R.string.upload_image_successful, Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(context, R.string.upload_image_fail, Toast.LENGTH_SHORT).show());
    }

    /**
     * 수정된 이미지 저장 방식 알고리즘
     * <p>이미지 다운로드. 로컬(외부 저장소)에 이미지 저장. -> 이미지 저장 한 로컬 경로를 localImg에 순서대로 저장. -> 수정 클릭 시 -> arrayUri과 localImg와 비교(아래 코드) -> 이미지 업로드 및 로컬 이미지 삭제</p>
     * <pre> {@code ArrayList<String> localImg = new ArrayList<>();  //localImg 로컬에 저장한 이미지 경로들을 저장하는 변수
     *
     * int j = 0;
     * for(int i = 0; i < arrayUri.size(); i++) {
     *      if(uriArrayList.get(i)가 http로 시작(firebase에서 가져왔는가)하는 가?) {
     *          uriArrayList.get(i) = localImg.get(j);
     *          j++;
     *      }
     * }}
     * </pre>
     */
    //richEditer에서 수정하기 버튼을 클릭시 실행할 함수, version을 올려서 리턴함
    public Integer uploadEditImage(String docID, int imageCount, int version) {
        int mVersion = version + 1;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // 다수의 이미지를 넣기 위해 for문 사용
        for (int index = 0; index < uriArrayList.size(); index++) {
            // content 이미지들 DB에 올리기.
            StorageReference imgRef = storageRef.child("Image").child(docID).child("contentImage" + mVersion + index + "." + getFileExtension(uriArrayList.get(index)));
            imgRef.putFile(uriArrayList.get(index)).addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl()
                            .addOnSuccessListener(uri1 -> Toast.makeText(context, R.string.upload_image_successful, Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(context, R.string.upload_image_fail, Toast.LENGTH_SHORT).show());
        }
        // 기존 이미지 삭제
        for (int i = 0; i < imageCount; i++) {
            StorageReference desertRef = storageRef.child("Image/" + docID + "/").child("contentImage" + version + i + ".jpg");
            desertRef.delete();
        }
        return mVersion;
    }

    // 이미지 형태 가져오는 함수
    private String getFileExtension(Uri uri) {
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private String getOnlyExtension(String fullUri) {
        int fileIndex = fullUri.toString().lastIndexOf(".");
        return fullUri.substring(fileIndex + 1, fullUri.length());
    }
}