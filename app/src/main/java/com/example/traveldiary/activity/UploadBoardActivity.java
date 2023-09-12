package com.example.traveldiary.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.traveldiary.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class UploadBoardActivity extends AppCompatActivity {

    private ArrayList<Uri> uriArrayList = new ArrayList<Uri>();

    private String TAG = "로그";
    private RichEditor mEditor;
    private TextView mPreview;
    private String userToken;

    private ImageView preImage;
    private Uri filePath;

    @SuppressLint("MissingInflatedId")
    private Map<String, Object> info;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_board);

        userToken = getIntent().getStringExtra("userToken");

        LoginActivity.db = FirebaseFirestore.getInstance();
        LoginActivity.storage = FirebaseStorage.getInstance();    // 이미지 경로를 저장하기 위한 DB에 접근하기 위환 인스턴스 선언

        // Create a storage reference from our app


        mEditor = (RichEditor) findViewById(R.id.editor);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);

        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");

        mPreview = (TextView) findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(text -> mPreview.setText(text));

        findViewById(R.id.action_undo).setOnClickListener(v -> mEditor.undo());
        findViewById(R.id.action_redo).setOnClickListener(v -> mEditor.redo());

        findViewById(R.id.action_bold).setOnClickListener(v -> mEditor.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> mEditor.setItalic());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> mEditor.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> mEditor.setUnderline());

//        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;
//
//            @Override
//            public void onClick(View v) {
//                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
//                isChanged = !isChanged;
//            }
//        });

        findViewById(R.id.action_indent).setOnClickListener(v -> mEditor.setIndent());

        findViewById(R.id.action_outdent).setOnClickListener(v -> mEditor.setOutdent());


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result != null) {
                        Glide
                                .with(getApplicationContext()).load(result.getData().getData())
                                .apply(RequestOptions.overrideOf(250, 300))
                                .centerCrop()
                                .override(320, 240);
                        // 이미지 크기를 4:3으로 저장
                        mEditor.insertImage(String.valueOf(result.getData().getData()), " ", 320);

                        filePath = result.getData().getData();

                        Log.d("로그", "이미지 가져오기 성공");

                        uriArrayList.add(filePath);
                        Log.d("로그", "리스트에 uri 넣기 성공" + String.valueOf(uriArrayList.size()));

                    }
                }
        );

        findViewById(R.id.action_insert_image).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            activityResultLauncher.launch(intent);
        });

        ImageView myPage = findViewById(R.id.myPage);
        myPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, MypageActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
        });
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainViewActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
        });

        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mEditor.getHtml();
                ArrayList<Integer> strImgStartIndex = new ArrayList<Integer>();
                ArrayList<Integer> strImgEndIndex = new ArrayList<Integer>();
                //이미지 링크 추출하는 loop
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == '<' && str.charAt(i + 1) == 'i') {
                        strImgStartIndex.add(i+10);
                        Log.d(TAG, "이미지 시작 index" + str.charAt(i+10));

                    }
                    if (str.charAt(i) == '>' && str.charAt(i - 2) == '0' && str.charAt(i - 3) == '2') {
                        Log.d(TAG, "인택스" + str.charAt(i-21));
                        strImgEndIndex.add(i-21);
                    }
                }
                // 이미지 링크 자리에 image 번호로 대체
                int count = strImgStartIndex.size();
                while (count > 0) {
                    str = str.replace(str.substring(strImgStartIndex.get(count - 1), strImgEndIndex.get(count - 1)), "image" + count);
                    count--;
                }
                // 정상 처리하는지 확인
                Toast.makeText(getApplicationContext(), "버튼 눌림확인", Toast.LENGTH_SHORT).show();
                save(str);
            }
        });
    }


    // DB에 올리는 함수
    public void save(String str) {
        Log.d(TAG, "save가 실행됨");
        EditText title = findViewById(R.id.title);
        LocalDateTime now = LocalDateTime.now();
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        Map<String, Object> item = new HashMap<>();
        item.put("uploadDate", formatNow);
        item.put("title", title.getText().toString());
        item.put("con", str);
        LoginActivity.db.collection("data").document("one").set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (filePath != null) {
                    uploadImage(filePath);
                }
            }
        });
        item.put("date", info.get("date"));
        item.put("mainImg", info.get("mainImg"));
        item.put("hashTag", info.get("hashTag"));
        item.put("userToken", userToken);

        LoginActivity.db.collection("data").add(item).addOnSuccessListener(documentReference -> {
            String getID = documentReference.getId();
            documentReference.update("boardID", getID);

            Intent intent = new Intent(this, MainViewActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(UploadBoardActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show());
    }

    // 이미지 올리는 곳
    public void uploadImage(Uri uri) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        //System.currentTimeMillis() 예전 사진 이름이였던거

        // 다수의 이미지를 넣기 위해 for문 사용
        for (int index = 0; index < uriArrayList.size(); index++) {

            //name : firebase에 올라가는 이름이다.
            StorageReference imgRef = storageRef.child("name" + index + "." + getFileExtension(uri));
            imgRef.putFile(uriArrayList.get(index)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                            Log.d("로그", "Firebase image upload success");
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                    Log.d("로그", "Firebase image upload Fail");
                }
            });
        }
    }


    // Uri 형태 가져오는 함수
    private String getFileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


}