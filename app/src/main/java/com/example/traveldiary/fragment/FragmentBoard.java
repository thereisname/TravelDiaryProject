package com.example.traveldiary.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.provider.MediaStore;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.activity.MypageActivity;
import com.example.traveldiary.activity.UpdateCalendarActivity;
import com.example.traveldiary.adapter.BoardValueAdapter;
import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.adapter.ContentUploadAdapter;
import com.example.traveldiary.value.MyPageValue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private ArrayList<String> localArray;

    LinearLayout content;
    ContentDownloadAdapter contentDownloadAdapter;

    public interface UploadCompleteListener {
        void onUploadComplete();
    }

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
        deleteButton.setOnClickListener(v -> deleteBoard(item.getBoardID(), dialog, position, imageCount));

        ImageButton editButton = dialog.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            localArray = new ArrayList<>();
            editBoard(position, item);
            localArray = contentDownloadAdapter.downLowdImage();
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

        dialog.findViewById(R.id.action_indent).setOnClickListener(v ->
                mEditor.setIndent());

        dialog.findViewById(R.id.action_outdent).setOnClickListener(v ->
                mEditor.setOutdent());


        // '수정하기' 버튼 클릭 시 DB 업데이트
        dialog.findViewById(R.id.updateBtn).setOnClickListener(v -> {
            ContentUploadAdapter contentUploadAdapter = new ContentUploadAdapter(item);
            contentUploadAdapter.uploadTrans(() -> {
                for (int i = 0; i < imageCount; i++) {
                    StorageReference desertRef = storageReference.child("Image").child(item.getBoardID()).child("contentImage" + (item.getVersion() - 1) + i + ".jpg");
                    desertRef.delete();
                }
                //핸드폰 파일 삭제 코드
                for (int i = 0; i < imageCount; i++) {

                    String folderName = "TraveFolder";
                    String fileName = "contentImage" + (item.getVersion() - 1) + i + ".jpg";
                    File fileToDelete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName + "/" + fileName);

                    if (fileToDelete.exists()) {
                        if (fileToDelete.delete()) {
                            // 파일 삭제 성공
                            Toast.makeText(getContext(), "파일 삭제 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            // 파일 삭제 실패
                            Toast.makeText(getContext(), "파일 삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 파일이 존재하지 않음
                        Toast.makeText(getContext(), "파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // 변경된 글에서 이미지링크 추출 밑 이미지 제목 변경
//            mEditor.setHtml( contentUploadAdapter.changeText(mEditor.getHtml()));
//            // 변경된 글을 item.getCon()에 넣음
//            mEditor.setHtml(item.getCon());
            //int mVersion = contentUploadAdapter.uploadEditImage(item.getBoardID(), imageCount, item.version);
            // 버전 추가할시 넣을 것
            item.setTitle(title.getText().toString());
            db.collection("data").document(item.getBoardID()).update(
                    "title", item.getTitle()
                    //"con", item.getCon()
                    //,"version" , item.getVersion()
                    //버전 추가할시 넣을 것
            ).addOnSuccessListener(command -> {
                adapter.updateData(position);
                dialog.dismiss();
                Intent intent = new Intent(getContext(), UpdateCalendarActivity.class);
                intent.putExtra("boardID", item.getBoardID());
                intent.putExtra("hashTag", item.getHashTagArray());
                intent.putExtra("date", item.getDate());
                startActivity(intent);
            });
        });
    }

    // 게시물 삭제 메서드.
    public void deleteBoard(String docID, Dialog dialog, int position, int imageCount) {
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
                    StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("contentImage" + i + ".jpg");
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