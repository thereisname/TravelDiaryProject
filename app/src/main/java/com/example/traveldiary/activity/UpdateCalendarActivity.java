package com.example.traveldiary.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateCalendarActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private TextView data_picker_text;
    private LinearLayout listView;
    private boolean isChangeDate, isChangeImage;
    private Uri filePath;
    private int count;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_calendar);

        isChangeDate = false;
        isChangeImage = false;
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        MyPageValue mp = new MyPageValue(getIntent().getStringArrayListExtra("hashTag"), getIntent().getStringExtra("date"), getIntent().getStringExtra("boardID"),getIntent().getStringArrayListExtra("route"));
        ImageButton dataRangeBtn = findViewById(R.id.dataRangeBtn);
        ImageView imageButton = findViewById(R.id.imageButton);
        listView = findViewById(R.id.listView);
        data_picker_text = findViewById(R.id.data_picker_text);
        Button next = findViewById(R.id.next);

        for (int i = 0; i < mp.getRoute().size(); i++)
            routeEditor(i, mp.getRoute().get(i));

        data_picker_text.setText(mp.getDate());
        storageReference.child("/Image/" + mp.getBoardID() + "/MainImage.jpg").getDownloadUrl().addOnSuccessListener(command ->
                Glide.with(this)
                        .load(command)
                        .into(imageButton));
        editHashTagChecked(mp.getHashTagArray());

        @SuppressLint("CheckResult")
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result != null) {
                        Glide
                                .with(getApplicationContext()).load(result.getData().getData())
                                .apply(RequestOptions.overrideOf(250, 200))
                                .centerCrop()
                                .into(imageButton);
                        // 이미지 크기를 4:3으로 저장
                        filePath = result.getData().getData();
                        isChangeImage = true;
                    }
                });

        dataRangeBtn.setOnClickListener(v -> calendarPick());
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            activityResultLauncher.launch(intent);
        });

        findViewById(R.id.addLoad).setOnClickListener(v -> addRouteEditor());
        next.setOnClickListener(v -> save(mp));
    }

    private void addRouteEditor() {
        count += 1;
        int dpValue = 300;
        float density = getResources().getDisplayMetrics().density;
        int pixelValue = (int) (dpValue * density);
        EditText editText = new EditText(getApplicationContext());

        editText.setHintTextColor(getColor(R.color.icon));
        editText.setTextColor(getColor(R.color.text_gray));
        editText.setId(count);
        editText.setTextSize(12f);
        editText.setHint("경로");
        editText.setLinkTextColor(getColor(R.color.icon));
        editText.setWidth(pixelValue);
        editText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.icon)));
        editText.setTextAppearance(R.style.uploadCalender_rote);

        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        editText.setLayoutParams(param);
        listView.addView(editText);
    }

    private void routeEditor(int count, String route) {
        int dpValue = 300;
        float density = getResources().getDisplayMetrics().density;
        int pixelValue = (int) (dpValue * density);
        EditText editText = new EditText(getApplicationContext());

        editText.setHintTextColor(getColor(R.color.icon));
        editText.setTextColor(getColor(R.color.text_gray));
        editText.setId(count);
        editText.setTextSize(12f);
        editText.setText(route);
        editText.setLinkTextColor(getColor(R.color.icon));
        editText.setWidth(pixelValue);
        editText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.icon)));
        editText.setTextAppearance(R.style.uploadCalender_rote);

        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        editText.setLayoutParams(param);
        listView.addView(editText);
        this.count = count;
    }

    private void save(MyPageValue mp) {
        DocumentReference doc = db.collection("data").document(mp.getBoardID());
        if (isChangeDate) doc.update("date", data_picker_text.getText());
        if (isChangeImage) {
            StorageReference desertRef = storageReference.child("Image/" + mp.getBoardID() + "/").child("MainImage.jpg");
            desertRef.delete();
            StorageReference mImgRef = storageReference.child("Image").child(mp.getBoardID()).child("MainImage" + "." + getFileExtension(filePath));
            mImgRef.putFile(filePath).addOnSuccessListener(taskSnapshot -> mImgRef.getDownloadUrl()
                            .addOnSuccessListener(uri1 -> Toast.makeText(this, R.string.upload_image_successful, Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, R.string.upload_image_fail, Toast.LENGTH_SHORT).show());
        }
        if(mp.getRoute().size() != 0) doc.update("route", routeUpload(count));
        LocalDateTime now = LocalDateTime.now();
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        doc.update("hashTag", editHashTagCou(),
                "correctedDate", formatNow);
        Intent intent = new Intent(this, MypageActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private ArrayList<String> routeUpload(int size) {
        ArrayList<String> arrRoute = new ArrayList<>();
        for (int i = 0; i <= size; i++) {
            EditText editText = findViewById(i);
            if (!editText.getText().toString().equals("")) arrRoute.add(editText.getText().toString());
        }
        return arrRoute;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    // 클릭 된 hashTag를 찾아 arrayHashTag에 저장하여 반환.
    private ArrayList<String> editHashTagCou() {
        ArrayList<String> arrayHashTag = new ArrayList<>();
        if (((Chip) findViewById(R.id.chip1)).isChecked()) arrayHashTag.add("#혼자 여행 ");
        if (((Chip) findViewById(R.id.chip2)).isChecked()) arrayHashTag.add("#커플 여행 ");
        if (((Chip) findViewById(R.id.chip3)).isChecked()) arrayHashTag.add("#친구와 여행 ");
        if (((Chip) findViewById(R.id.chip4)).isChecked()) arrayHashTag.add("#가족 여행 ");

        if (((Chip) findViewById(R.id.chip10)).isChecked()) arrayHashTag.add("#계획적인 ");
        if (((Chip) findViewById(R.id.chip11)).isChecked()) arrayHashTag.add("#자유로운 ");
        if (((Chip) findViewById(R.id.chip12)).isChecked()) arrayHashTag.add("#휴가 ");
        if (((Chip) findViewById(R.id.chip13)).isChecked()) arrayHashTag.add("#추억 ");
        if (((Chip) findViewById(R.id.chip14)).isChecked()) arrayHashTag.add("#힐링 ");
        if (((Chip) findViewById(R.id.chip15)).isChecked()) arrayHashTag.add("#엑티비티 ");
        if (((Chip) findViewById(R.id.chip16)).isChecked()) arrayHashTag.add("#맛집투어 ");
        if (((Chip) findViewById(R.id.chip17)).isChecked()) arrayHashTag.add("#낭만 ");
        if (((Chip) findViewById(R.id.chip18)).isChecked()) arrayHashTag.add("#감성 ");

        return arrayHashTag;
    }

    // DB에 저장된 hashTag 데이터 불러올 때 checked되도록 설정.
    private void editHashTagChecked(List<String> hashTag) {
        if (hashTag.contains("#혼자 여행 ")) ((Chip) findViewById(R.id.chip1)).setChecked(true);
        if (hashTag.contains("#커플 여행 ")) ((Chip) findViewById(R.id.chip2)).setChecked(true);
        if (hashTag.contains("#친구와 여행 ")) ((Chip) findViewById(R.id.chip3)).setChecked(true);
        if (hashTag.contains("#가족 여행 ")) ((Chip) findViewById(R.id.chip4)).setChecked(true);

        if (hashTag.contains("#계획적인 ")) ((Chip) findViewById(R.id.chip10)).setChecked(true);
        if (hashTag.contains("#자유로운 ")) ((Chip) findViewById(R.id.chip11)).setChecked(true);
        if (hashTag.contains("#휴가 ")) ((Chip) findViewById(R.id.chip12)).setChecked(true);
        if (hashTag.contains("#추억 ")) ((Chip) findViewById(R.id.chip13)).setChecked(true);
        if (hashTag.contains("#힐링 ")) ((Chip) findViewById(R.id.chip14)).setChecked(true);
        if (hashTag.contains("#액티비티 ")) ((Chip) findViewById(R.id.chip15)).setChecked(true);
        if (hashTag.contains("#맛집투어 ")) ((Chip) findViewById(R.id.chip16)).setChecked(true);
        if (hashTag.contains("#낭만 ")) ((Chip) findViewById(R.id.chip17)).setChecked(true);
        if (hashTag.contains("#감성 ")) ((Chip) findViewById(R.id.chip18)).setChecked(true);
    }

    private void calendarPick() {
        //calendar Range
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.mainActivity_dataPick_title))
                .setSelection(
                        new Pair(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                        )
                )
                .build();
        dateRangePicker.show(getSupportFragmentManager(), "Material Date Range Picker");
        dateRangePicker.addOnPositiveButtonClickListener(datePicked -> {
            Long startDate = datePicked.first;
            Long endDate = datePicked.second;
            if (startDate != null && endDate != null) {
                data_picker_text.setText(getString(
                        R.string.uploadCalender_calendar,
                        convertLongToDate(startDate),
                        convertLongToDate(endDate)
                ));
                isChangeDate = true;
            }
        });
    }

    private String convertLongToDate(Long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy.MM.dd", Locale.getDefault()
        );
        return format.format(date);
    }
}
