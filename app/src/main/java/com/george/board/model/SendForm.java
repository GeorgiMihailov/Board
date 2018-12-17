package com.george.board.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SendForm implements Serializable {
    public int boardId;
    public  int cardId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String description;
    ArrayList<ConfigForms> forms;

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public ArrayList<ConfigForms> getForms() {
        return forms;
    }

    public void setForms(ArrayList<ConfigForms> forms) {
        this.forms = forms;
    }
}
