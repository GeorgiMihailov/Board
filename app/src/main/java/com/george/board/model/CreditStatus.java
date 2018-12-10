package com.george.board.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CreditStatus implements Serializable {
    String id;
    ArrayList<String> names;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }
}
