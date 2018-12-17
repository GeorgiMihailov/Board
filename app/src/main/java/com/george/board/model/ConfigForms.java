package com.george.board.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ConfigForms implements Serializable {
    private int  Id;
    private String Name;
    private int Type;
    private String defaultValue;
    private ArrayList<DropdownItem> ListItems;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ArrayList<DropdownItem> getListItem() {
        return ListItems;
    }

    public void setListItem(ArrayList<DropdownItem> listItem) {
        ListItems = listItem;
    }


}
