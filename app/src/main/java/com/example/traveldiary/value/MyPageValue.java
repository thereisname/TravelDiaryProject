package com.example.traveldiary.value;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MyPageValue implements Parcelable {
    private String con;
    private String title;
    private ArrayList<String> hashTag;
    private String date;
    private String uploadDate;
    private String boardID;
    private ArrayList<String> route;
    private ArrayList<String> bookmark;

    public MyPageValue() {
    }

    // Parcelable 객체를 생성하는 생성자
    protected MyPageValue(Parcel in) {
        con = in.readString();
        title = in.readString();
        hashTag = in.readArrayList(null);
        date = in.readString();
        uploadDate = in.readString();
        boardID = in.readString();
        bookmark = in.readArrayList(null);
    }

    // Parcelable 객체를 생성하는 메서드
    public static final Creator<MyPageValue> CREATOR = new Creator<MyPageValue>() {
        @Override
        public MyPageValue createFromParcel(Parcel in) {
            return new MyPageValue(in);
        }

        @Override
        public MyPageValue[] newArray(int size) {
            return new MyPageValue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(con);
        dest.writeString(title);
        dest.writeList(hashTag);
        dest.writeList(route);
        dest.writeList(bookmark);
        dest.writeString(date);
        dest.writeString(uploadDate);
        dest.writeString(boardID);
    }

    public MyPageValue(String con, String title, ArrayList<String> hashTag, String date, String uploadDate, String boardID, ArrayList<String> route, ArrayList<String> bookmark) {
        this.con = con;
        this.title = title;
        this.hashTag = hashTag;
        this.date = date;
        this.uploadDate = uploadDate;
        this.boardID = boardID;
        this.route = route;
        this.bookmark = bookmark;
    }

    public String getCon() {
        return con;
    }

    public String getTitle() {
        return title;
    }

    public String getHashTag() {
        StringBuilder arr = new StringBuilder();
        for (String hash : hashTag) {
            arr.append(hash);
        }
        return arr.toString();
    }

    public String getDate() {
        return date;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getBoardID() {
        return boardID;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public ArrayList<String> getBookmark() {
        return bookmark;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCon(String con) {
        this.con = con;
    }

}