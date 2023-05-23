package com.example.traveldiary.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.adapter.BoardValueAdapter;
import com.example.traveldiary.value.myPageValue;

import java.util.ArrayList;

public class FragmentBoard extends Fragment implements OnItemClickListener {
    private RecyclerView recyclerView;
    private BoardValueAdapter adapter;
    private ArrayList<myPageValue> itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        recyclerView = v.findViewById(R.id.boardRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BoardValueAdapter(v.getContext(), this::onItemSelected);
        itemList = new ArrayList<>();

        recyclerView.setAdapter(adapter);

        adapter.addItem(new myPageValue("제목","BoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoard", R.drawable.baseline_image_24, "#장소  #날짜  #~~와  #날씨 ..."));
        adapter.addItem(new myPageValue("2제목", "BoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoardBoard", R.drawable.baseline_image_24, "#서울  #5월  #친구와  #날씨 ..."));
        adapter.addItem(new myPageValue("3제목", "afasdfasdfasdfasdfuihqwefhuiosab fash fjkhaskfh asfhas", R.drawable.baseline_image_24,"#가평  #6월  #가족과  #맑음 ..."));
        adapter.addItem(new myPageValue("4제목", "asdjfkl hasfjasklfj aksj faj iofjas fjakl;j fioaj fkajifjaskl;dfj  qejfioajfiah fashfjkhaskfhasjkfhasjkfhuias fiasfhas fhjuasfhkasjfi fashef", R.drawable.baseline_image_24, "#장소  #날짜  #~~와  #날씨 ..."));
        adapter.addItem(new myPageValue("5제목", "whfio qfycq94rynqwyrq8n4ryq89yn489qy90tnq90tyq90nyfn90qf8qycnqh0fnqn4r890q4tyf89qyhn0qyhfc9qhf90fc89qtyf89qycn8qfhqwenfgc8weyg89qeg89qwehn4ghcnm8wegh29queghc9weoimgc9qwiejg890we5g89qeg9qwuehgc9qehg89qefghj cqeg890qg9qerygaheyg9swtug;9ypqwa35tgk;l/qrhabg", R.drawable.baseline_image_24, "#장소  #날짜  #~~와  #날씨 ..."));

        recyclerView.setAdapter(adapter);

        return v;
    }

    public void onItemSelected(View view, int position, ArrayList<myPageValue> items) {
//        //Save the clicked position value among the ArrayList values as a resultValue type in the variable item.
//        BoardValue item = items.get(position);
//        //Create a Dialog class to define a custom dialog.
//        final Dialog dialog = new Dialog(getActivity());
//        //Custom Dialog Corner Radius Processing
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        //Hide the title bar of the activity.
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //Set the layout of the custom dialog.
//        dialog.setContentView(R.layout.dialog_board_detail);
//        //Adjust the screen size.
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        Window window = dialog.getWindow();
//        window.setAttributes(lp);
//
//        //Create variables and find in layout.
//        TextView title, hashTag, content;
//        ImageView mainImg;
//
//        title = dialog.findViewById(R.id.title);
//        hashTag = dialog.findViewById(R.id.hashTag);
//        content = dialog.findViewById(R.id.content);
//        mainImg = dialog.findViewById(R.id.mainImg);
//
//        title.setText(item.getTitle());
//        hashTag.setText(item.getHashTag());
//        content.setText(item.getCon());
//        mainImg.setImageResource(item.getMainImg());
//
//
//        dialog.show();
//        ImageView closeButton = dialog.findViewById(R.id.closeButton);
//        closeButton.setOnClickListener(v -> dialog.dismiss());
    }
}