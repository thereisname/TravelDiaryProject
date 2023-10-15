package com.example.traveldiary.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.adapter.BoardValueAdapter;
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

        loadDate();

        return v;
    }

    public void loadDate() {
        db.collection("data").whereEqualTo("userToken", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        int imageCount = contentDownloadAdapter.checkText();

        dialog.show();
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        ImageButton deleteButton = dialog.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteBoard(item.getBoardID(), dialog, position, imageCount));

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
        RichEditor mEditor = dialog.findViewById(R.id.editor);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setPadding(10, 10, 10, 10);

        title.setText(item.getTitle());
        mEditor.setHtml(contentDownloadAdapter.checkTextEdit());
        dialog.show();

        // '수정하기' 버튼 클릭 시 DB 업데이트.
        dialog.findViewById(R.id.updateBtn).setOnClickListener(v -> {
            item.setTitle(title.getText().toString());
            db.collection("data").document(item.getBoardID()).update(
                    "title", item.getTitle()
            ).addOnSuccessListener(command -> {
                adapter.updateData(position);
                dialog.dismiss();
            });
        });
    }

    // 게시물 삭제 메서드.
    public void deleteBoard(String docID, Dialog dialog, int position, int imageCount) {
        db.collection("data").document(docID).delete().addOnSuccessListener(unused -> {
            for (int i = 0; i < imageCount; i++) {
                StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("contentImage" + i + ".jpg");
                desertRef.delete();
            }
            StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("MainImage.jpg");
            desertRef.delete();
            dialog.dismiss();
            adapter.removeData(position);
            Toast.makeText(getActivity().getApplicationContext(), "정상적으로 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(command -> Toast.makeText(getActivity().getApplicationContext(), "삭제 실패!", Toast.LENGTH_SHORT).show());
    }
}