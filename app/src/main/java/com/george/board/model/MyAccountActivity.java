package com.george.board.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class MyAccountActivity  extends ExpandableGroup<MyAccountActivityDetails> {

    ArrayList<MyAccountActivityDetails> cards;
    String name;
    String icon;
    int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<MyAccountActivityDetails> getCards() {
        return cards;
    }

    public void setCards(ArrayList<MyAccountActivityDetails> cards) {
        this.cards = cards;
    }

    public MyAccountActivity(String title, ArrayList<MyAccountActivityDetails> items) {
        super(title, items);
    }
}
