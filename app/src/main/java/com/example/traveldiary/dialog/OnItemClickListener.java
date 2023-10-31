package com.example.traveldiary.dialog;

import android.view.View;

import com.example.traveldiary.value.MyPageValue;

import java.util.ArrayList;

public interface OnItemClickListener {
    void onItemSelected(View view, int position, ArrayList<MyPageValue> items);
}