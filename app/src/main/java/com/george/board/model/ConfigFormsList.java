package com.george.board.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ConfigFormsList implements Serializable {
    public ArrayList<ConfigForms> Fields;
    public String Url;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        this.Url = url;
    }

    public ArrayList<ConfigForms> getForms() {
        return Fields;
    }

    public void setForms(ArrayList<ConfigForms> forms) {
        this.Fields = forms;
    }
}
