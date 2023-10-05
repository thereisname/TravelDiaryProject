package com.example.traveldiary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainValueAdapter extends RecyclerView.Adapter<MainValueAdapter.ViewHolder> {

    private ArrayList<MyPageValue> items = new ArrayList<>();

    private Context context;

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

    public MyPageValue getItem(int position, MyPageValue item) {
        return items.set(position, item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mainImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainImage = (ImageView) itemView.findViewById(R.id.iv_mainImage);
            mainTitle = (TextView) itemView.findViewById(R.id.tv_mainTitle);
            userEmail = (TextView) itemView.findViewById(R.id.tv_userEmail);
        }

        public void setItem(MyPageValue item) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("/Image/" + item.getBoardID() + "/MainImage.jpg").getDownloadUrl().addOnSuccessListener(command ->
                    Glide.with(context)
                            .load(command)
                            .into(mainImage));
        }
    }
}
