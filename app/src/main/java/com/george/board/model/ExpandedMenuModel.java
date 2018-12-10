package com.george.board.model;

public class ExpandedMenuModel {

    private String iconName = "";
    private String iconImg = "";
    private String url = "";


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public  String getIconImgText() {
        return iconImg;
    }

    public  void setIconImgText(String iconImg) {
        this.iconImg = iconImg;
    }
}