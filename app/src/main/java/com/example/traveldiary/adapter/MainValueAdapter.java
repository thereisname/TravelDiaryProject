package com.example.traveldiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.traveldiary.R;
import com.example.traveldiary.activity.MainViewActivity;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainValueAdapter extends RecyclerView.Adapter<MainValueAdapter.ViewHolder> {
    private ArrayList<MyPageValue> items = new ArrayList<>();

    private Context context;
    private long lastClickTime = 0;
    private static final long CLICK_TIME_INTERVAL = 1000; // 클릭 간격을 1초로 설정
    private int currentVisiblePosition = 0;   //슬라이드 아이템 체크

    // OnItemClickListener itemClickListener
    public MainValueAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MainValueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater minflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = minflater.inflate(R.layout.item_mainrecyclerview, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainValueAdapter.ViewHolder viewHolder, int position) {
        MyPageValue item = items.get(position);
        viewHolder.setItem(item);
    }

    public void setAdapterList(ArrayList<MyPageValue> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(MyPageValue item) {
        items.add(item);
    }

    public void setItem(ArrayList<MyPageValue> items) {
        this.items = items;
    }


    public MyPageValue getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainTitle;
        TextView date;
        ImageView mainImage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_mainView);
            mainImage = itemView.findViewById(R.id.iv_mainImage);
            mainTitle = itemView.findViewById(R.id.tv_mainTitle);
            date = itemView.findViewById(R.id.tv_userEmail);
            cardView.setOnClickListener(view -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > CLICK_TIME_INTERVAL) {
                    lastClickTime = currentTime;
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //itemClickListener.onItemSelected(view, position, items);
                        MyPageValue selectedItem = items.get(position);
                        currentVisiblePosition = position;
                        ((MainViewActivity) context).onItemSelected(selectedItem);

                    }
                }
            });
        }

        public void setItem(MyPageValue item) {
            mainTitle.setText(item.getTitle());
            date.setText(item.getDate());
            mainImage.setImageResource(R.drawable.baseline_image_24);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("/Image/" + item.getBoardID() + "/MainImage.jpg").getDownloadUrl().addOnSuccessListener(command ->
                    Glide.with(context)
                            .load(command)
                            .into(mainImage));
        }
    }
}
