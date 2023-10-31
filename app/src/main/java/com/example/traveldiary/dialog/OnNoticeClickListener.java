package com.example.traveldiary.dialog;

import android.view.View;

import com.example.traveldiary.value.NoticeValue;

import java.util.ArrayList;

public interface OnNoticeClickListener {
    void onItemSelected(View view, int position, ArrayList<NoticeValue> items);
}
