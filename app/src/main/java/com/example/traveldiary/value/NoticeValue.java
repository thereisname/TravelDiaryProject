package com.example.traveldiary.value;

public class NoticeValue {
    private String title;
    private String date;
    private String con;

    public NoticeValue() {}

    public NoticeValue(String title, String date, String con) {
        this.title = title;
        this.date = date;
        this.con = con;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCon() {
        return con;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCon(String con) {
        this.con = con;
    }
}
