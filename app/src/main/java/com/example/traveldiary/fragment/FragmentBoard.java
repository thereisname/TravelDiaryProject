package com.example.traveldiary.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.traveldiary.dialog.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.activity.MypageActivity;
import com.example.traveldiary.activity.UpdateCalendarActivity;
import com.example.traveldiary.adapter.BoardValueAdapter;
import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.adapter.ContentUploadAdapter;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;

public class FragmentBoard extends Fragment implements OnItemClickListener {
    private RecyclerView recyclerView;
    private BoardValueAdapter adapter;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RichEditor mEditor;
    private int imageCount = 0;
    LinearLayout content;
    ContentDownloadAdapter contentDownloadAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        recyclerView = v.findViewById(R.id.boardRecyclerView);

        storageReference = FirebaseStorage.getInstance().getReference();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BoardValueAdapter(v.getContext(), this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result != null) {
                        Glide
                                .with(getContext()).load(result.getData().getData())
                                .apply(RequestOptions.overrideOf(250, 300))
                                .centerCrop()
                                .override(320, 240);
                        // 이미지 크기를 4:3으로 저장
                        mEditor.insertImage(String.valueOf(result.getData().getData()), " ", 320);
                    }
                });
        loadData();

        return v;
    }

    public void loadData() {
        db.collection("data").whereEqualTo("userToken", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            MypageActivity.postCount.setText(String.valueOf(queryDocumentSnapshots.size()));
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                MyPageValue mp = queryDocumentSnapshot.toObject(MyPageValue.class);
                adapter.addItem(mp);
            }
            recyclerView.setAdapter(adapter);
        });
    }

    public void onItemSelected(View view, int position, ArrayList<MyPageValue> items) {
        //Save the clicked position value among the ArrayList values as a resultValue type in the variable item.
        MyPageValue item = items.get(position);
        //Create a Dialog class to define a custom dialog.
        final Dialog dialog = new Dialog(getActivity());
        //Custom Dialog Corner Radius Processing
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Hide the title bar of the activity.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set the layout of the custom dialog.
        dialog.setContentView(R.layout.dialog_board_detail);
        //Adjust the screen size.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        //Create variables and find in layout.
        TextView title, hashTag, uploadDate, date;

        title = dialog.findViewById(R.id.title);
        uploadDate = dialog.findViewById(R.id.uploadDate);
        date = dialog.findViewById(R.id.date);
        hashTag = dialog.findViewById(R.id.hashTag);
        content = dialog.findViewById(R.id.content);

        title.setText(item.getTitle());
        hashTag.setText(item.getHashTag());
        uploadDate.setText(getString(R.string.uploadBoard_uploadDate, item.getUploadDate()));
        date.setText(getString(R.string.uploadBoard_date, item.getDate()));

        contentDownloadAdapter = new ContentDownloadAdapter(getActivity(), content, item);
        imageCount = contentDownloadAdapter.checkText();

        dialog.show();
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        ImageButton deleteButton = dialog.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteBoard(item.getBoardID(), dialog, position, imageCount, item.getVersion()));

        ImageButton editButton = dialog.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            editBoard(position, item);
            dialog.dismiss();
        });
    }

    /**
     * 게시물 수정 메서드.
     *
     * @param position 클릭한 게시물 Position. RecyclerView update시 사용.
     * @param item     클릭한 게시물의 내용들이 담겨있는.
     * @Authors thereisname
     * @since 1.0
     */
    private void editBoard(int position, MyPageValue item) {
        final Dialog dialog = new Dialog(getActivity());
        //Custom Dialog Corner Radius Processing
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Hide the title bar of the activity.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set the layout of the custom dialog.
        dialog.setContentView(R.layout.activity_update_board);
        //Adjust the screen size.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        EditText title = dialog.findViewById(R.id.title);
        mEditor = dialog.findViewById(R.id.editor);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setPadding(10, 10, 10, 10);

        title.setText(item.getTitle());
        mEditor.setHtml(contentDownloadAdapter.checkTextEdit());
        dialog.show();

        // 갤러리에서 사진 가져오기
        dialog.findViewById(R.id.action_insert_image).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            activityResultLauncher.launch(intent);
        });

        // rich메뉴들 선택시 적용사항들
        dialog.findViewById(R.id.action_undo).setOnClickListener(v -> mEditor.undo());
        dialog.findViewById(R.id.action_redo).setOnClickListener(v -> mEditor.redo());

        dialog.findViewById(R.id.action_bold).setOnClickListener(v -> mEditor.setBold());

        dialog.findViewById(R.id.action_italic).setOnClickListener(v -> mEditor.setItalic());

        dialog.findViewById(R.id.action_strikethrough).setOnClickListener(v -> mEditor.setStrikeThrough());

        dialog.findViewById(R.id.action_underline).setOnClickListener(v -> mEditor.setUnderline());

        dialog.findViewById(R.id.action_indent).setOnClickListener(v -> mEditor.setIndent());

        dialog.findViewById(R.id.action_outdent).setOnClickListener(v -> mEditor.setOutdent());

        // UploadAdapter가져오는 곳
        ContentUploadAdapter contentUploadAdapter = new ContentUploadAdapter(getActivity());
        // '수정하기' 버튼 클릭 시 DB 업데이트.
        dialog.findViewById(R.id.updateBtn).setOnClickListener(v -> {
            item.setTitle(title.getText().toString());
//            item.setCon(contentUploadAdapter.changeText(mEditor.getHtml()));
//            item.setVersion(contentUploadAdapter.uploadEditImage(item.getBoardID(), imageCount, item.getVersion()));
            db.collection("data").document(item.getBoardID()).update(
                    "title", item.getTitle(),
                    "version", item.getVersion()
            ).addOnSuccessListener(command -> {
                adapter.updateData(position);
                dialog.dismiss();
                Intent intent = new Intent(getContext(), UpdateCalendarActivity.class);
                intent.putExtra("boardID", item.getBoardID());
                intent.putExtra("hashTag", item.getHashTagArray());
                intent.putExtra("date", item.getDate());
                intent.putExtra("route", item.getRoute());
                startActivity(intent);
            });
        });
    }

    // 게시물 삭제 메서드.
    public void deleteBoard(String docID, Dialog dialog, int position, int imageCount, int version) {
        final Dialog dialog2 = new Dialog(getActivity());
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_board_delete);
        dialog2.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog2.getWindow();
        window.setAttributes(lp);

        dialog2.show();

        dialog2.findViewById(R.id.confirm_button).setOnClickListener(v -> {
            db.collection("data").document(docID).delete().addOnSuccessListener(unused -> {
                for (int i = 0; i < imageCount; i++) {
                    StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("contentImage" + version + i + ".jpg");
                    desertRef.delete();
                }
                StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("MainImage.jpg");
                desertRef.delete();
                dialog2.dismiss();
                dialog.dismiss();
                adapter.removeData(position);
                Toast.makeText(getActivity().getApplicationContext(), R.string.mypage_board_deleted_successful, Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(command -> Toast.makeText(getActivity().getApplicationContext(), R.string.mypage_board_deleted_fail, Toast.LENGTH_SHORT).show());
        });
        dialog2.findViewById(R.id.fail_button).setOnClickListener(v -> dialog2.dismiss());
    }
}