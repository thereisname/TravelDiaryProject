package com.example.traveldiary.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.adapter.BoardValueAdapter;

import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FragmentBoard extends Fragment implements OnItemClickListener {
    private RecyclerView recyclerView;
    private BoardValueAdapter adapter;
    ImageView imageView;

    private  ArrayList<Integer> arrayStartIndex = new ArrayList<Integer>();
    private  ArrayList<Integer> arrayEndIndex = new ArrayList<Integer>();
    private LinearLayout listView;
    private ArrayList<View> arrayimage = new ArrayList();
    String TAG  = "로그";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        recyclerView = v.findViewById(R.id.boardRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BoardValueAdapter(v.getContext(), this);
        recyclerView.setAdapter(adapter);
        loadDate();


        return v;
    }

    public void loadDate() {
        Bundle bundle = getArguments();
        String userToken = bundle.getString("userToken");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").whereEqualTo("userToken", userToken).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        TextView title, hashTag, content, uploadDate, date;
        ImageView mainImg;


        title = dialog.findViewById(R.id.title);
        uploadDate = dialog.findViewById(R.id.uploadDate);
        date = dialog.findViewById(R.id.date);
        hashTag = dialog.findViewById(R.id.hashTag);


        listView = dialog.findViewById(R.id.listView);

        checkText(item);
        title.setText(item.getTitle());
        hashTag.setText(item.getHashTag());

        uploadDate.setText(getString(R.string.uploadBoard_uploadDate, item.getUploadDate()));
        date.setText(getString(R.string.uploadBoard_date, item.getDate()));


        dialog.show();
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            arrayimage.clear();
            arrayStartIndex.clear();
            arrayEndIndex.clear();
        });

    }

    private void checkText(MyPageValue mp) {
        String str = mp.getCon();

        for (int index = 0; index < str.length(); index++) {
            if (str.charAt(index) == '<' && str.charAt(index + 1) == 'i' && str.charAt(index + 2) == 'm') {
                arrayStartIndex.add(index);
            }
            if (str.charAt(index) == '>' && str.charAt(index - 1) == '"' && str.charAt(index - 2) == '0') {
                arrayEndIndex.add(index);
            }
        }
        if (arrayStartIndex.size() == 0) {
            createTextView(mp.getCon());
        } else {
            if (arrayStartIndex.get(0) != 0) {
                String str0 = mp.getCon().substring(0, arrayStartIndex.get(0));
                createTextView(str0);
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                        createTextView(str1);
                    } else {
                        String str1 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str1);
                    }
                }
            } else {
                for (int i = 0; i < arrayStartIndex.size(); i++) {
                    createImageView();
                    if (i == arrayStartIndex.size() - 1) {
                        String str2 = mp.getCon().substring(arrayEndIndex.get(i) + 1, mp.getCon().length());
                        createTextView(str2);
                    } else {
                        String str3 = mp.getCon().substring(arrayEndIndex.get(i) + 1, arrayStartIndex.get(i + 1));
                        createTextView(str3);
                    }
                }
            }
        }
        Imagedown(mp.getBoardID());


    }
    int imageId = 10000;

    private void createImageView() {
        imageView = new ImageView(getContext());
        imageView.setId(imageId);
        int b = imageView.getId();
        Glide.with(getContext()).load(R.drawable.baseline_image_24).into(imageView);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(param);
        arrayimage.add(imageView);
        listView.addView(imageView);
        imageId++;
    }

    private void createTextView(String str) {
        TextView textViewNm = new TextView(getContext());
        textViewNm.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString());
        textViewNm.setTextSize(15);
        textViewNm.setTextColor(Color.rgb(0, 0, 0));
        textViewNm.setBackgroundColor(Color.rgb(184, 236, 184));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewNm.setLayoutParams(param);
        listView.addView(textViewNm);
    }
    private void Imagedown(String boardID) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("/Image/" + boardID).listAll().addOnSuccessListener(listResult -> {
            int a = listResult.getItems().size();
            for (int i = 0; i < listResult.getItems().size(); i++) {
                StorageReference item = listResult.getItems().get(i);
                int finalI = i;
                item.getDownloadUrl().addOnSuccessListener(command -> {
                    Glide.with(getContext()).load(command).into(((ImageView) arrayimage.get(finalI)));
                });
            }

        }).addOnFailureListener(command -> Log.d("error", "불러오기 실패."));
    }
}