package com.example.traveldiary.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.traveldiary.adapter.ContentUploadAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseFirestore db;

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
        mEditor.setPlaceholder(getString(R.string.uploadBoard_placeholder));
        mEditor.setEditorFontColor(getColor(R.color.text_gray));

        mPreview = findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(text -> mPreview.setText(text));

        findViewById(R.id.action_undo).setOnClickListener(v -> mEditor.undo());
        findViewById(R.id.action_redo).setOnClickListener(v -> mEditor.redo());

        findViewById(R.id.action_bold).setOnClickListener(v -> mEditor.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> mEditor.setItalic());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> mEditor.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> mEditor.setUnderline());

        findViewById(R.id.action_indent).setOnClickListener(v ->
                mEditor.setIndent());

        findViewById(R.id.action_outdent).setOnClickListener(v ->
                mEditor.setOutdent());

        @SuppressLint("CheckResult")
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
                        filePath = result.getData().getData();
                        mEditor.insertImage(String.valueOf(result.getData().getData()), " ", 320);
                        uriArrayList.add(filePath);
                    }
                });

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
        uploadBtn.setOnClickListener(v -> save());
    }

    public void save() {
        Map<String, Object> info = (Map<String, Object>) getIntent().getSerializableExtra("info");
        Map<String, Object> item = itemCustom(info);
        ContentUploadAdapter contentUploadAdapter = new ContentUploadAdapter(uriArrayList, getApplicationContext());
        db.collection("data").add(item).addOnSuccessListener(documentReference -> {
            String getID = documentReference.getId();
            documentReference.update("boardID", getID);
            if (filePath != null)
                contentUploadAdapter.uploadImage(getID, (Uri) info.get("mainImage"));
            String changeText = contentUploadAdapter.changeText(mEditor.getHtml());
            documentReference.update("con", changeText);

            Intent intent = new Intent(this, MainViewActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            Toast.makeText(this, getString(R.string.uploadBoard_upload_successful), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(UploadBoardActivity.this, getString(R.string.uploadBoard_upload_fail), Toast.LENGTH_SHORT).show());
    }

    private Map<String, Object> itemCustom(Map<String, Object> info) {
        EditText title = findViewById(R.id.title);
        LocalDateTime now = LocalDateTime.now();
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        Map<String, Object> item = new HashMap<>();
        item.put("uploadDate", formatNow);
        item.put("title", title.getText().toString());
        item.put("date", info.get("date"));
        item.put("hashTag", info.get("hashTag"));
        item.put("userToken", FirebaseAuth.getInstance().getUid());
        item.put("route", info.get("route"));

        return item;
    }
}