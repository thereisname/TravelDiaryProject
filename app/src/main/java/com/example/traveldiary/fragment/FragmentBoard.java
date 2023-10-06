package com.example.traveldiary.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class FragmentBoard extends Fragment implements OnItemClickListener {
    private RecyclerView recyclerView;
    private BoardValueAdapter adapter;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    LinearLayout content;
    ImageView imageView;
    ArrayList<Integer> arrayStartIndex;
    ArrayList<Integer> arrayEndIndex;
    ArrayList<View> arrayimage;

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
        arrayStartIndex = new ArrayList<Integer>();
        arrayEndIndex = new ArrayList<Integer>();
        arrayimage = new ArrayList();

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
        checkText(item.getCon(), item.getBoardID());
        dialog.show();
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            arrayimage.clear();
            arrayStartIndex.clear();
            arrayEndIndex.clear();
        });

        ImageButton deleteButton = dialog.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteBoard(item.getBoardID(), dialog, position));
    }

    private void checkText(String con, String boardID) {
        // 문자에서 이미지  시작과 끝을 가져오기
        for (int index = 0; index < con.length(); index++) {
            if (con.charAt(index) == '<' && con.charAt(index + 1) == 'i' && con.charAt(index + 2) == 'm') {
                arrayStartIndex.add(index);
            }
            if (con.charAt(index) == '>' && con.charAt(index - 1) == '"' && con.charAt(index - 2) == '0') {
                arrayEndIndex.add(index);
            }
        }
        // 이미지 가져오기
        if (arrayStartIndex.size() == 0) {
            createTextView(con);
        } else {
            if (arrayStartIndex.get(0) != 0) {
                String str0 = con.substring(0, arrayStartIndex.get(0));
                createTextView(str0);
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str1 = con.substring(arrayEndIndex.get(i) + 1, con.length());
                        createTextView(str1);

                    } else {
                        String str1 = con.substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str1);
                    }
                }
            } else {
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str2 = con.substring(arrayEndIndex.get(i) + 1, con.length());
                        createTextView(str2);

                    } else {
                        String str3 = con.substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str3);
                    }
                }
            }
        }
        Imagedown(boardID);
    }

    // Image 다운로드 함수
    private void Imagedown(String boardID) {
        storageReference.child("/Image/" + boardID).listAll().addOnSuccessListener(listResult -> {
            for (int i = 1; i < listResult.getItems().size(); i++) {
                StorageReference item = listResult.getItems().get(i);
                int finalI = i - 1;
                item.getDownloadUrl().addOnSuccessListener(command -> Glide.with(getContext()).load(command).into(((ImageView) arrayimage.get(finalI))));
            }
        });
    }

    int imageId = 10000;

    private void createImageView() {
        imageView = new ImageView(getContext());
        imageView.setId(imageId);
        Glide.with(getContext()).load(R.drawable.baseline_image_24).into(imageView);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(param);
        arrayimage.add(imageView);
        content.addView(imageView);
        imageId++;
    }

    private void createTextView(String str) {
        TextView textViewNm = new TextView(getActivity());
        textViewNm.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
        textViewNm.setTextSize(15);
        textViewNm.setTextColor(Color.rgb(0, 0, 0));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewNm.setLayoutParams(param);
        content.addView(textViewNm);
    }

    public void deleteBoard(String docID, Dialog dialog, int position) {
        db.collection("data").document(docID).delete().addOnSuccessListener(unused -> {
            Toast.makeText(getActivity().getApplicationContext(), "정상적으로 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < arrayimage.size(); i++) {
                StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("contentImage" + i +".jpg");
                desertRef.delete();
            }
            StorageReference desertRef = storageReference.child("Image/" + docID + "/").child("MainImage.jpg");
            desertRef.delete();
            dialog.dismiss();
            adapter.removeDate(position);
        }).addOnFailureListener(command -> Toast.makeText(getActivity().getApplicationContext(), "삭제 실패!", Toast.LENGTH_SHORT).show());
    }
}