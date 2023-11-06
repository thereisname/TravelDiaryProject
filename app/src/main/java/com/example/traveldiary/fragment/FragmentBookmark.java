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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.dialog.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.adapter.BookmarkValueAdapter;
import com.example.traveldiary.adapter.ContentDownloadAdapter;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentBookmark extends Fragment implements OnItemClickListener {
    private RecyclerView recyclerView;
    private BookmarkValueAdapter adapter;
    private FirebaseFirestore db;
    LinearLayout content;
    ContentDownloadAdapter contentDownloadAdapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmark, container, false);
        recyclerView = v.findViewById(R.id.bookmarkRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookmarkValueAdapter(v.getContext(), this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadData();

        return v;
    }

    public void loadData() {
        db.collection("data").whereArrayContains("bookmark", Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        dialog.setContentView(R.layout.dialog_bookmark_board_detail);
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
        contentDownloadAdapter.checkText();
        contentDownloadAdapter.ImageDown(item.getBoardID());

        ImageButton bookmarkBtn = dialog.findViewById(R.id.bookMarkBtn);
        bookmarkBtn.setImageResource(R.drawable.baseline_bookmark_24);

        bookmarkBtn.setOnClickListener(v -> {
            // db에서 bookmark 제거 시키기
            List<String> bookmarkArray = item.getBookmark();
            bookmarkArray.remove(FirebaseAuth.getInstance().getUid());
            FirebaseFirestore.getInstance().collection("data").document(item.getBoardID()).update("bookmark", bookmarkArray).addOnSuccessListener(command -> {
                adapter.removeBookmark(position);
                bookmarkBtn.setImageResource(R.drawable.baseline_bookmark_border_24);
                dialog.dismiss();
            });
        });

        dialog.show();
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
    }
}