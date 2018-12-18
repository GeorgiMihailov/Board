package com.george.board.helper;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import com.george.board.R;
import com.george.board.model.MyAccountActivity;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class MyAccountActivityViewHolder extends GroupViewHolder{

        private TextView genreTitle;
        private TextView iconHolder;
        private TextView numberOfElements;
        private  int id;


        public MyAccountActivityViewHolder(View itemView) {
            super(itemView);
            genreTitle = itemView.findViewById(R.id.genre_title);
            iconHolder = itemView.findViewById(R.id.icon_holder);
            numberOfElements = itemView.findViewById(R.id.number_of_elements);
        }

        public void setGenreTitle(ExpandableGroup group)
        {
            Typeface ta = ResourcesCompat.getFont(itemView.getContext(), R.font.bankicon);
            iconHolder.setTypeface(ta);
            String v = (((MyAccountActivity) group).getIcon());
            String g = new String(Character.toChars(Integer.parseInt(
                    v, 16)));
            genreTitle.setText(((MyAccountActivity) group).getName());
            iconHolder.setText(g);
            String size =String.valueOf(((MyAccountActivity) group).getSize()) ;
            numberOfElements.setText(size);

        }
}