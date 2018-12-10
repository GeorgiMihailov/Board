package com.george.board.model;

import java.io.Serializable;

public class DropdownItem implements Serializable {
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String Name;
    public int Id;
}
