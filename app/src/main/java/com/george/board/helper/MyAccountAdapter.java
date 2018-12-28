package com.george.board.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.george.board.StatusActivity;
import com.george.board.R;
import com.george.board.model.MyAccountActivityDetails;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

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
        View view = inflater.inflate(R.layout.main_myactivity_window, parent, false);
        Drawable shape = view.getBackground();
            GradientDrawable shapeDrawable = (GradientDrawable) shape;
            shapeDrawable.setColor(Color.parseColor(PreferencesManager.getPrimaryColor(view.getContext())));

        return new MyAccountActivityViewHolder(view);
    }

    @Override
    public MyAccountActivityDetailsViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.secondary_myactivity_window, parent, false);

        return new MyAccountActivityDetailsViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(MyAccountActivityDetailsViewHolder holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {
        final ArrayList<MyAccountActivityDetails> artist = (ArrayList<MyAccountActivityDetails>) group.getItems();
        holder.onBind(artist, childIndex);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StatusActivity.class);
                intent.putExtra("cardId", artist.get(flatPosition-1).getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(MyAccountActivityViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setGenreTitle(group);
    }
}