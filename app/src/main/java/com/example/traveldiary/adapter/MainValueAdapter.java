package com.example.traveldiary.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainValueAdapter extends RecyclerView.Adapter<MainValueAdapter.ViewHolder> {

    private ArrayList<MyPageValue> items = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private Context context;

    public MainValueAdapter(Context context, OnItemClickListener itemClickListener)  {
        this.context = context;
        this.itemClickListener = itemClickListener;
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

    public MyPageValue getItem(int position, MyPageValue item) {
        return items.set(position, item);
    }

    public void setItems(ArrayList<MyPageValue> items) {
        this.items = items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainTitle;
        TextView date;
        ImageView mainImage;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_mainView);
            mainImage = (ImageView) itemView.findViewById(R.id.iv_mainImage);
            mainTitle = (TextView) itemView.findViewById(R.id.tv_mainTitle);
            date = (TextView) itemView.findViewById(R.id.tv_userEmail);

            cardView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    itemClickListener.onItemSelected(view, position, items);
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
                            .into(mainImage)
            );
        }
    }
}
