package com.example.traveldiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.dialog.OnNoticeClickListener;
import com.example.traveldiary.R;
import com.example.traveldiary.value.NoticeValue;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>{
    private ArrayList<NoticeValue> items = new ArrayList<>();
    private OnNoticeClickListener onNoticeClickListener;
    private Context context;

    public NoticeAdapter(Context context, OnNoticeClickListener onNoticeClickListener) {
        this.context = context;
        this.onNoticeClickListener = onNoticeClickListener;
    }
    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_noticerecylerview, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder viewHolder, int position) {
        NoticeValue item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(NoticeValue item) {
        items.add(item);
    }

    public void setItems(ArrayList<NoticeValue> items) {
        this.items = items;
    }

    public NoticeValue getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, NoticeValue item) {
        items.set(position, item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cardView;
        TextView title;
        TextView date;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            title = itemView.findViewById(R.id.noticeTitle);
            date = itemView.findViewById(R.id.noticeDate);

            //Click View Details Event
            cardView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onNoticeClickListener.onItemSelected(view, position, items);
                }
            });
        }

        public void setItem(NoticeValue item) {
            title.setText(item.getTitle());
            date.setText(item.getDate());
        }
    }
}
