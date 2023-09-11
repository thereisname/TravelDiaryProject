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

import com.example.traveldiary.value.MyPageValue;
import com.example.traveldiary.OnItemClickListener;
import com.example.traveldiary.R;

import java.util.ArrayList;

public class BoardValueAdapter extends RecyclerView.Adapter<BoardValueAdapter.ViewHolder> {
    private ArrayList<MyPageValue> items = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private Context context;

    public BoardValueAdapter(Context context, OnItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public BoardValueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_recyclerview, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
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

    public void setItems(ArrayList<MyPageValue> items) {
        this.items = items;
    }

    public MyPageValue getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, MyPageValue item) {
        items.set(position, item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView image;
        CardView cardView;
        TextView title;
        TextView hashTag;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            content = itemView.findViewById(R.id.content);
            image = itemView.findViewById(R.id.mainImg);
            title = itemView.findViewById(R.id.title);
            hashTag = itemView.findViewById(R.id.hashTag);

            //Click View Details Event
            cardView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemSelected(view, position, items);
                }
            });
        }

        public void setItem(MyPageValue item) {
            title.setText(item.getTitle());
            content.setText(item.getCon());
            image.setImageResource(item.getMainImg());
            hashTag.setText(item.getHashTag());
        }
    }
}