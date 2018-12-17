package com.george.board.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.george.board.R;
import com.george.board.model.MyAccountActivity;
import com.george.board.model.MyAccountActivityDetails;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class MyAccountAdapter extends ExpandableRecyclerViewAdapter<MyAccountActivityViewHolder, MyAccountActivityDetailsViewHolder > {
    private LayoutInflater inflater;
    private ArrayList<? extends ExpandableGroup> groups;
    public MyAccountAdapter(Context context, ArrayList<? extends ExpandableGroup> groups) {
        super(groups);
        this.inflater = LayoutInflater.from(context);
        this.groups = groups;

    }

    @Override
    public MyAccountActivityViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_genre, parent, false);
        return new MyAccountActivityViewHolder(view);
    }

    @Override
    public MyAccountActivityDetailsViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_artist, parent, false);
        return new MyAccountActivityDetailsViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(MyAccountActivityDetailsViewHolder holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {
        final MyAccountActivityDetails artist = ((MyAccountActivity) group).getItems().get(childIndex);
        holder.setArtistName(artist.getName());
    }

    @Override
    public void onBindGroupViewHolder(MyAccountActivityViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setGenreTitle(group);
    }
}