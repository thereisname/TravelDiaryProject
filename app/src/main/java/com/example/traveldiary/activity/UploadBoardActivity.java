package com.example.traveldiary.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.traveldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class UploadBoardActivity extends AppCompatActivity {
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private RichEditor mEditor;
    private TextView mPreview;
    private Uri filePath;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_board);

        db = FirebaseFirestore.getInstance();

        // Create a storage reference from our app
        mEditor = findViewById(R.id.editor);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);

        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");

        mPreview = findViewById(R.id.preview);
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
                        uriArrayList.add(filePath);
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
            startActivity(new Intent(this, MypageActivity.class));
            finish();
        });
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            startActivity(new Intent(this, MainViewActivity.class));
            finish();
        });

        Button uploadBtn = findViewById(R.id.uploadBtn);

        uploadBtn.setOnClickListener(v -> {
            save();
        });
    }

    private ArrayList<Uri> downloadImageUri(String getID) {
        ArrayList<Uri> imageUri = new ArrayList<Uri>();
        Log.d("로그2", imageUri.toString());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Log.d("로그3", imageUri.toString());
        storageReference.child("/Image/pMp11v28f4dXE22tSta0").listAll().addOnSuccessListener(listResult -> {
            Log.d("로그4", imageUri.toString());
            for (StorageReference item : listResult.getItems()) {
                Log.d("로그5", imageUri.toString());
                item.getDownloadUrl().addOnSuccessListener(command -> {
                    imageUri.add(command);
                    Log.d("로그1", String.valueOf(command));

                });
            }
        }).addOnFailureListener(command -> Log.d("error", "불러오기 실패."));
        Log.d("image", String.valueOf(imageUri.size()));
        return imageUri;
    }


    private String changeText(String getID) {
        String str = mEditor.getHtml();
        ArrayList<Integer> strImgStartIndex = new ArrayList<>();
        ArrayList<Integer> strImgEndIndex = new ArrayList<>();

        ArrayList<Uri> images = downloadImageUri(getID);
        Log.d("images", images.toString());
        //이미지 링크 추출하는 loop
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '<' && str.charAt(i + 1) == 'i') {
                strImgStartIndex.add(i + 10);
            }
            if (str.charAt(i) == '>' && str.charAt(i - 2) == '0' && str.charAt(i - 3) == '2') {
                strImgEndIndex.add(i - 21);
            }
        }
        // 이미지 링크 자리에 image 번호로 대체
        int count = strImgStartIndex.size();
        while (count > 0) {
            str = str.replace(str.substring(strImgStartIndex.get(count - 1), strImgEndIndex.get(count - 1)), images.get(count - 1).toString());
            count--;
        }
        return str;
    }

    public void save() {
        Map<String, Object> item = itemCustom();

        db.collection("data").add(item).addOnSuccessListener(documentReference -> {
            String getID = documentReference.getId();
            documentReference.update("boardID", getID);

            if (filePath != null) uploadImage(filePath, getID);
            String changeText = changeText(getID);
            documentReference.update("con", changeText);

            Intent intent = new Intent(this, MainViewActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(UploadBoardActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show());
    }


    private Map<String, Object> itemCustom() {
        Map<String, Object> info = (Map<String, Object>) getIntent().getSerializableExtra("info");
        EditText title = findViewById(R.id.title);
        LocalDateTime now = LocalDateTime.now();
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        Map<String, Object> item = new HashMap<>();
        item.put("uploadDate", formatNow);
        item.put("title", title.getText().toString());
        item.put("date", info.get("date"));
        item.put("mainImg", info.get("mainImg"));
        item.put("hashTag", info.get("hashTag"));
        item.put("userToken", FirebaseAuth.getInstance().getUid());

        return item;
    }

    // 이미지 올리는 곳
    public void uploadImage(Uri uri, String getID) {
        //MAIN 이미지 넣는 곳
        // uriArrayList.add(uri);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // 다수의 이미지를 넣기 위해 for문 사용
        for (int index = 0; index < uriArrayList.size(); index++) {
            //name : firebase에 올라가는 이름이다.
            StorageReference imgRef = storageRef.child("Image").child(getID).child("name" + index + "." + getFileExtension(uri));
            imgRef.putFile(uriArrayList.get(index)).addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl().addOnSuccessListener(uri1 ->
                    Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show())).addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show());
        }
    }

    //  이미지 형태 가져오는 함수
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}