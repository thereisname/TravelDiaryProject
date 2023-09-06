package com.example.traveldiary.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.traveldiary.R;
import com.example.traveldiary.adapter.BoardValueAdapter;
import com.example.traveldiary.value.myPageValue;

import java.util.ArrayList;

public class FragmentBookmark extends Fragment {
    private RecyclerView recyclerView;
    private BoardValueAdapter adapter;
    private ArrayList<myPageValue> itemList;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmark, container, false);
        recyclerView = v.findViewById(R.id.bookmarkRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BoardValueAdapter(v.getContext(), this::onItemSelected);
        itemList = new ArrayList<>();

        recyclerView.setAdapter(adapter);

        adapter.addItem(new myPageValue("사랑에 연습이 있었다면", "사랑에 연습이 있었다면\n" +
                "우리는 달라졌을까\n" +
                "내가 널 만난 시간 혹은 그 장소\n" +
                "상황이 달랐었다면 우린 맺어졌을까\n" +
                "\n" +
                "하필 넌 왜 내가 그렇게 철없던 시절에\n" +
                "나타나서 그렇게 예뻤니\n" +
                "너처럼 좋은여자가 왜 날 만나서 그런\n" +
                "과분한 사랑 내게 줬는지\n" +
                "\n" +
                "우리 다시 그때로 돌아가자는게\n" +
                "그게 미친말인가\n" +
                "정신나간 소린가\n" +
                "나는 더 잘할수있고 다신 울리지 않을\n" +
                "자신있는데 그게 왜 말이 안돼\n" +
                "시간이 너무 흘러 알게되었는데\n" +
                "너를 울리지 않고 아껴주는법\n" +
                "세월은 왜 철없는 날 기다려주지 않고 \n" +
                "흘러갔는지 야속해\n" +
                "\n" +
                "지금 너 만나는 그에게도 내게 그랬던 것처럼\n" +
                "예쁘게 웃어주니\n" +
                "너처럼 좋은여자의 사랑 받는 그 남자 \n" +
                "너무 부러워 넌 행복하니\n" +
                "\n" +
                "니옆에 지금 그 남자가 있는게\n" +
                "우리 다시 맺어질수가 없는 이윤가\n" +
                "나는 더 잘할수있고 다신 울리지 않을\n" +
                "자신있는데 그게 왜 말이 안돼\n" +
                "시간이 너무 흘러 알게되었는데\n" +
                "너를 울리지 않고 아껴주는법\n" +
                "세월은 왜 널 잊는법을 알려주지 않고 흘러갔는지", R.drawable.baseline_image_24, "#장소  #날짜  #~~와  #날씨 ..."));
        adapter.addItem(new myPageValue("Heaven(2023)", "왜 이제 왔나요 더 야윈 그대\n" +
                "나만큼 힘들었나요\n" +
                "두 번 살게 하네요 그대 내 삶을\n" +
                "난 모든 걸 버리려 했죠\n" +
                "왜 나를 떠나요 아플 거면서\n" +
                "사랑이 여기 있는데\n" +
                "다신 그러지 마요 내가 죽어요\n" +
                "그댄 나의 숨이니까요\n" +
                "나 그댈 잃는다는 건\n" +
                "내 삶이 다한 것 살아도 지옥인 거죠\n" +
                "그댄 나의 전부 그댄 나의 운명\n" +
                "헤어질 수 없어요\n" +
                "영원보다 먼 곳에 우리 사랑 가져가요\n" +
                "눈물 없는 세상 나의 사랑 하나로만\n" +
                "그대 살게 할게요\n" +
                "그대와 나 영원히 행복할 그곳\n" +
                "Heaven\n" +
                "울어도 되나요 그대 앞에서\n" +
                "고마워 눈물이 나요\n" +
                "기다렸던 날들이 너무나 아파서\n" +
                "그대 다시 미워지네요\n" +
                "사랑을 버린다는 건 모든 걸 잃는 것\n" +
                "상처만 남기잖아요\n" +
                "그댄 나의 전부 그댄 나의 운명\n" +
                "헤어질 수 없어요\n" +
                "영원보다 먼 곳에 우리 사랑 가져가요\n" +
                "눈물 없는 세상 나의 사랑 하나로만\n" +
                "그대 살게 할게요\n" +
                "그대와 나 영원히 행복할 그곳\n" +
                "Heaven\n" +
                "그댄 나의 전부 그댄 나의 운명\n" +
                "헤어질 수 없어요\n" +
                "영원보다 먼 곳에 우리 사랑 가져가요\n" +
                "눈물 없는 세상 나의 사랑 하나로만\n" +
                "그대 살게 할게요\n" +
                "그대와 나 영원히 행복할 그곳\n" +
                "Heaven", R.drawable.baseline_image_24, "#서울  #5월  #친구와  #날씨 ..."));

        recyclerView.setAdapter(adapter);

        return v;
    }

    public void onItemSelected(View view, int position, ArrayList<myPageValue> items) {
        //Save the clicked position value among the ArrayList values as a resultValue type in the variable item.
        myPageValue item = items.get(position);
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
        TextView title, hashTag, content;
        ImageView mainImg;

        title = dialog.findViewById(R.id.title);
        hashTag = dialog.findViewById(R.id.hashTag);
        content = dialog.findViewById(R.id.content);
        mainImg = dialog.findViewById(R.id.mainImg);

        title.setText(item.getTitle());
        hashTag.setText(item.getHashTag());
        content.setText(item.getCon());
        Log.i("img uri", String.valueOf(item.getMainImg()));
        mainImg.setImageResource(item.getMainImg());


        dialog.show();
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

    }
}