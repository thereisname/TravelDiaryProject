package com.example.traveldiary;

import android.view.View;

import com.example.traveldiary.value.myPageValue;

import java.util.ArrayList;

public interface OnItemClickListener {
    void onItemSelected(View view, int position, ArrayList<myPageValue> items);
}