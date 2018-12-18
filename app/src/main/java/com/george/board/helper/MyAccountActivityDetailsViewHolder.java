package com.george.board.helper;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import com.george.board.R;
import com.george.board.model.MyAccountActivityDetails;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.ArrayList;

public class MyAccountActivityDetailsViewHolder extends ChildViewHolder {

    public TextView getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon.setText(icon);
    }

    private TextView icon;
    private TextView type;
    private TextView date;
    private TextView status;
    private  int id;

    public MyAccountActivityDetailsViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        type =itemView.findViewById(R.id.type);
        date = itemView.findViewById(R.id.date);
        status = itemView.findViewById(R.id.status);
    }

    public void onBind(ArrayList<MyAccountActivityDetails> artist, int pos) {
        Typeface ta = ResourcesCompat.getFont(itemView.getContext(), R.font.bankicon);
        Typeface tf =ResourcesCompat.getFont(itemView.getContext(),R.font.fontawesome_webfont);

        if (((MyAccountActivityDetails) artist.get(pos)).getIcon()!=null){
            String v = (((MyAccountActivityDetails) artist.get(pos)).getIcon());
            if (v.startsWith("e")){
                icon.setTypeface(ta);
            }else icon.setTypeface(tf);
            String g = new String(Character.toChars(Integer.parseInt(
                    v, 16)));
            icon.setText(g);
        }


//        id = ((MyAccountActivityDetails) artist.get(pos)).getId();
        type.setText(((MyAccountActivityDetails) artist.get(pos)).getName());
        date.setText(((MyAccountActivityDetails) artist.get(pos)).getDate());
        status.setText(((MyAccountActivityDetails) artist.get(pos)).getStatus());
    }
}

