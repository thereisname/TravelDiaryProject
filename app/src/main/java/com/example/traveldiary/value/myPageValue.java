package com.example.traveldiary.value;

public class myPageValue {
    private String con;
    private int mainImg;
    private String title;
    private String hashTag;




    public myPageValue(String title, String con, int mainImg, String hashTag) {
        this.title = title;
        this.con = con;
        this.mainImg = mainImg;
        this.hashTag = hashTag;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public void setMainImg(int mainImg) {
        this.mainImg = mainImg;
    }

    public String getCon() {
        return con;
    }

    public int getMainImg() {
        return mainImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}