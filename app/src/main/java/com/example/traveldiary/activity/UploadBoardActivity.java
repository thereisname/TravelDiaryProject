package com.example.traveldiary.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.traveldiary.R;

import com.example.traveldiary.value.BoardValue;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class UploadBoardActivity extends AppCompatActivity {

    private RichEditor mEditor;
    private TextView mPreview;
    private String userToken;
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
        StorageReference storageRef = LoginActivity.storage.getReference();

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
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
                        mEditor.insertImage(String.valueOf(uri), "", 320);
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

    public void save(TextView mPreview) {
        info = (Map<String, Object>) getIntent().getSerializableExtra("info");
        EditText title = findViewById(R.id.title);
        LocalDateTime now = LocalDateTime.now();
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        Map<String, Object> item = new HashMap<>();
        item.put("uploadDate", formatNow);
        item.put("title", title.getText().toString());
        item.put("con", mPreview.getText().toString());
        item.put("date", info.get("date"));
        item.put("mainImg", info.get("mainImg"));
        item.put("hashTag", info.get("hashTag"));

        LoginActivity.db.collection("data").add(item).addOnSuccessListener(documentReference -> {
            String getID = documentReference.getId();
            documentReference.update("boardID", getID);

            mDatabase = FirebaseDatabase.getInstance().getReference("UI");
            BoardValue board = new BoardValue(getID, title.getText().toString(), (String) info.get("mainImg"), (String) info.get("hashTag"));
            mDatabase.child("users").child(userToken).child("board").child(getID).setValue(board);

            Intent intent = new Intent(this, MainViewActivity.class);
            intent.putExtra("userToken", userToken);
            startActivity(intent);
            finish();
        });
    }
}