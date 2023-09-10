package com.example.traveldiary.value;

import java.util.HashMap;
import java.util.Map;

public class BoardValue {
    public String boardID;
    public String title;
    public String mainImg;
    public String hashTag;

    public BoardValue() {
    }

    public BoardValue(String boardID, String title, String mainImg, String hashTag) {
        this.boardID = boardID;
        this.title = title;
        this.mainImg = mainImg;
        this.hashTag = hashTag;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("boardID", boardID);
        result.put("title", title);
        result.put("mainImg", mainImg);
        result.put("hashTag", hashTag);
        return result;
    }
}
