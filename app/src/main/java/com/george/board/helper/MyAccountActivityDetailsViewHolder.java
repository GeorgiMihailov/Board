package com.george.board.helper;

import android.view.View;
import android.widget.TextView;

import com.george.board.R;
import com.george.board.model.MyAccountActivityDetails;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class MyAccountActivityDetailsViewHolder extends ChildViewHolder {

    public TextView getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName.setText(artistName);
    }

    private TextView artistName;

    public MyAccountActivityDetailsViewHolder(View itemView) {
        super(itemView);
        artistName = itemView.findViewById(R.id.artist_name);
    }

    public void onBind(MyAccountActivityDetails artist) {
        artistName.setText(artist.getName());
    }
}

