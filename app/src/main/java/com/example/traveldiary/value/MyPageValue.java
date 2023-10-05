package com.example.traveldiary.value;

public class MyPageValue {
    private String con;
    private String title;
    private String hashTag;
    private String date;
    private String uploadDate;
    private String boardID;
    private String userToken;

    public MyPageValue() {
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

    public String getUserToken() {
        return userToken;
    }
}