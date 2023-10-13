package com.example.traveldiary.value;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class MyPageValue implements Parcelable {
    private String con;
    private String mainImg;
    private String title;
    private String hashTag;
    private String date;
    private String uploadDate;
    private String boardID;
    private ArrayList<String> route;

    public MyPageValue() {}

    // Parcelable 객체를 생성하는 생성자
    protected MyPageValue(Parcel in) {
        con = in.readString();
        mainImg = in.readString();
        title = in.readString();
        hashTag = in.readString();
        date = in.readString();
        uploadDate = in.readString();
        boardID = in.readString();
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
        dest.writeString(mainImg);
        dest.writeString(title);
        dest.writeString(hashTag);
        dest.writeString(date);
        dest.writeString(uploadDate);
        dest.writeString(boardID);
    }

    public MyPageValue(String con, String title, String hashTag, String date, String uploadDate, String boardID, String userToken) {
        this.con = con;
        this.title = title;
        this.hashTag = hashTag;
        this.date = date;
        this.uploadDate = uploadDate;
        this.boardID = boardID;
    }

    public String getCon() {
        return con;
    }

    public String getTitle() {
        return title;
    }

    public String getHashTag() {
        return hashTag;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCon(String con) {
        this.con = con;
    }
}