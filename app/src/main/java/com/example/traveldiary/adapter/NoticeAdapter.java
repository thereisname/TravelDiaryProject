package com.example.traveldiary.adapter;

import static com.example.traveldiary.R.drawable;
import static com.example.traveldiary.R.id;
import static com.example.traveldiary.R.layout;

import android.content.Context;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveldiary.R;
import com.example.traveldiary.dialog.OnNoticeClickListener;
import com.example.traveldiary.value.NoticeValue;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private ArrayList<NoticeValue> items = new ArrayList<>();
    private OnNoticeClickListener onNoticeClickListener;
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();    // Item의 클릭 상태를 저장할 array 객체
    private int prePosition = -1;   // 직전에 클릭됐던 Item의 position

    public NoticeAdapter(Context context, OnNoticeClickListener onNoticeClickListener) {
        this.context = context;
        this.onNoticeClickListener = onNoticeClickListener;
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(layout.item_noticerecylerview, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder viewHolder, int position) {
        NoticeValue item = items.get(position);
        viewHolder.setItem(item, position);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ConstraintLayout cardView;
        TextView title;
        TextView date;
        TextView noticeDetail;
        ImageView noticeContext;
        LinearLayout noticeLayout;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(id.cardView);
            title = itemView.findViewById(id.noticeTitle);
            date = itemView.findViewById(id.noticeDate);
            noticeContext = itemView.findViewById(id.noticeContext);
            noticeDetail = itemView.findViewById(id.noticeDetail);
            noticeLayout = itemView.findViewById(R.id.noticeLayout);

            //Click View Details Event
            cardView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    onNoticeClickListener.onItemSelected(view, position, items);
            });
        }

        public void setItem(NoticeValue item, int position) {
            this.position = position;

            title.setText(item.getTitle());
            date.setText(item.getDate());
            noticeDetail.setText(Html.fromHtml(item.getCon(), Html.FROM_HTML_MODE_LEGACY));

            changeVisibility(selectedItems.get(position));

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (selectedItems.get(position)) {
                // 펼쳐진 Item을 클릭 시
                selectedItems.delete(position);
            } else {
                // 직전의 클릭됐던 Item의 클릭상태를 지움
                selectedItems.delete(prePosition);
                // 클릭한 Item의 position을 저장
                selectedItems.put(position, true);
            }
            // 해당 포지션의 변화를 알림
            if (prePosition != -1) notifyItemChanged(prePosition);
            notifyItemChanged(position);
            // 클릭된 position 저장
            prePosition = position;
        }

        private void changeVisibility(boolean isExpanded) {
            noticeLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            noticeContext.setImageResource(isExpanded ? drawable.baseline_expand_less_24 : drawable.baseline_expand_more_24);
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
//            int dpValue = 150;
//            float d = context.getResources().getDisplayMetrics().density;
//            int height = (int) (dpValue * d);
//
//            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
//            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
//            // Animation이 실행되는 시간, n/1000초
//            va.setDuration(600);
//            va.addUpdateListener(animation -> {
//                // value는 height 값
//                int value = (int) animation.getAnimatedValue();
//                // imageView의 높이 변경
//                noticeDetail.getLayoutParams().height = value;
//                noticeDetail.requestLayout();
//                // imageView가 실제로 사라지게하는 부분
//                noticeDetail.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//                noticeContext.setImageResource(isExpanded ? drawable.baseline_expand_less_24 : drawable.baseline_expand_more_24);
//            });
//            // Animation start
//            va.start();
        }
    }
}
