package com.example.traveldiary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.R;
import com.example.traveldiary.value.MyPageValue;

import java.util.ArrayList;

public class MainValueAdapter extends RecyclerView.Adapter<MainValueAdapter.ViewHolder> {

    private ArrayList<MyPageValue> items = new ArrayList<>();

    @NonNull
    @Override
    public MainValueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainrecyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainValueAdapter.ViewHolder holder, int position) {
        holder.onBind(items.get(position));

    }

    public void setAdapterList(ArrayList<MyPageValue> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mainTitle;
        TextView userEmail;
        ImageView mainImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainImage = (ImageView) itemView.findViewById(R.id.iv_mainImage);
            mainTitle = (TextView) itemView.findViewById(R.id.tv_mainTitle);
            userEmail = (TextView) itemView.findViewById(R.id.tv_userEmail);
        }

        void onBind(MyPageValue item) {

        }
    }
}
