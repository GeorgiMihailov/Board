package com.george.board.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardCardList implements Serializable {
    public ArrayList<BoardCard> list;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    public ArrayList<BoardCard> getList() {
        return list;
    }

    public void setList(ArrayList<BoardCard> list) {
        this.list = list;
    }
}
