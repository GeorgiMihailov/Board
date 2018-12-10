package com.george.board.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Menues implements Serializable {
    String parent;
    int id;
    String label;
    String url;
    String icon;

    public ArrayList<Menues> getSubmenu() {
        return submenu;
    }

    public void setSubmenu(ArrayList<Menues> submenu) {
        this.submenu = submenu;
    }

    ArrayList<Menues> submenu;



    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
