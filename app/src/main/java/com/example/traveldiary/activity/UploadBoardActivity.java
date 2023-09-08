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
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.traveldiary.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import jp.wasabeef.richeditor.RichEditor;

public class UploadBoardActivity extends AppCompatActivity {

    private RichEditor mEditor;
    private TextView mPreview;
    private String userToken;

    private ImageView preImage;
    private Uri filePath;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_board);

        userToken = getIntent().getStringExtra("userToken");

        LoginActivity.db = FirebaseFirestore.getInstance();
        LoginActivity.storage = FirebaseStorage.getInstance();    // 이미지 경로를 저장하기 위한 DB에 접근하기 위환 인스턴스 선언

        // Create a storage reference from our app


        preImage = (ImageView) findViewById(R.id.preimag);

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
                    if(result.getResultCode() == RESULT_OK) {
                        Glide
                                .with(getApplicationContext()).load(result.getData().getData())
                                .apply(RequestOptions.overrideOf(250, 300))
                                .centerCrop()
                                .into(preImage);
                        mEditor.insertImage(String.valueOf(result.getData().getData()), " ", 320);

                       filePath = result.getData().getData();



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

        findViewById(R.id.uploadBtn).setOnClickListener(v -> save(mPreview));
    }


    // DB에 올리는 함수
    public void save(TextView mPreview) {
        EditText title = findViewById(R.id.title);
        LocalDate now = LocalDate.now();
        Map<String, Object> item = new HashMap<>();
        item.put("upload_data", String.valueOf(now));
        item.put("title", title.getText().toString());
        item.put("con", mPreview.getText().toString());
        LoginActivity.db.collection("data").document("one").set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               uploadImage(filePath);
            }
        });


        Intent intent = new Intent(this, MainViewActivity.class);
        intent.putExtra("userToken", userToken);
        startActivity(intent);
        finish();
    }

    // 이미지 올리는 곳
    public void uploadImage(Uri uri){


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
    // Uri 형태 가져오는 함수
    private String getFileExtension(Uri uri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }




}